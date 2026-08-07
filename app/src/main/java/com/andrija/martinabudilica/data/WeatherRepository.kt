package com.andrija.martinabudilica.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class MorningInfo(
    val place: String,
    val time: String,
    val temperature: String,
    val weather: String,
    val advice: String
)

class WeatherRepository(private val context: Context) {
    private val locationClient = LocationServices.getFusedLocationProviderClient(context)
    private val http = OkHttpClient()

    @SuppressLint("MissingPermission")
    suspend fun getMorningInfo(): MorningInfo {
        val hasLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        if (!hasLocation) {
            return MorningInfo(
                place = "lokacija nije dopuštena",
                time = currentTime,
                temperature = "—",
                weather = "Prognozu ne mogu provjeriti bez lokacije.",
                advice = "Ali mogu potvrditi da je dan bolji čim si se probudila. 💜"
            )
        }

        return try {
            val location = locationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).await() ?: locationClient.lastLocation.await()

            if (location == null) throw IllegalStateException("Lokacija nije dostupna")
            val place = resolvePlace(location.latitude, location.longitude)
            val url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=${location.latitude}&longitude=${location.longitude}" +
                "&current=temperature_2m,weather_code,precipitation,wind_speed_10m&timezone=auto"
            val body = withContext(Dispatchers.IO) {
                http.newCall(Request.Builder().url(url).build()).execute().use { response ->
                    if (!response.isSuccessful) error("Vrijeme nije dostupno")
                    response.body?.string() ?: error("Prazan odgovor")
                }
            }
            val current = JSONObject(body).getJSONObject("current")
            val temperature = current.getDouble("temperature_2m")
            val code = current.getInt("weather_code")
            val precipitation = current.optDouble("precipitation", 0.0)
            val condition = weatherDescription(code)
            val goodWeather = code <= 3 && precipitation <= 0.1 && temperature >= 12

            MorningInfo(
                place = place,
                time = currentTime,
                temperature = String.format(Locale.getDefault(), "%.0f °C", temperature),
                weather = condition,
                advice = if (goodWeather) {
                    "Danas te čeka lijepo vrijeme — idealno za dobar dan! ☀️"
                } else {
                    "Vrijeme danas malo glumi dramu. Ponesi što treba i ne daj mu da ti pokvari raspoloženje! ☂️"
                }
            )
        } catch (_: Exception) {
            MorningInfo(
                place = "tvoja trenutačna lokacija",
                time = currentTime,
                temperature = "—",
                weather = "Prognoza se trenutačno skriva ispod pokrivača.",
                advice = "Ti si se ipak probudila — znači najteži dio jutra je riješen! 😄"
            )
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun resolvePlace(latitude: Double, longitude: Double): String =
        withContext(Dispatchers.IO) {
            runCatching {
                val address = Geocoder(context, Locale.getDefault())
                    .getFromLocation(latitude, longitude, 1)
                    ?.firstOrNull()
                address?.locality ?: address?.subAdminArea ?: address?.adminArea
            }.getOrNull() ?: "u blizini"
        }

    private fun weatherDescription(code: Int) = when (code) {
        0 -> "Vedro nebo"
        1, 2 -> "Pretežno vedro"
        3 -> "Oblačno"
        45, 48 -> "Maglovito"
        in 51..57 -> "Rosulja"
        in 61..67 -> "Kiša"
        in 71..77 -> "Snijeg"
        in 80..82 -> "Pljuskovi"
        in 85..86 -> "Snježni pljuskovi"
        in 95..99 -> "Grmljavina"
        else -> "Promjenjivo vrijeme"
    }
}
