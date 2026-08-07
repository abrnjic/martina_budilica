package com.andrija.martinabudilica.alarm

import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andrija.martinabudilica.data.AlarmPreferences
import com.andrija.martinabudilica.data.MorningInfo
import com.andrija.martinabudilica.data.WeatherRepository
import com.andrija.martinabudilica.ui.theme.MartinaTheme
import kotlinx.coroutines.delay

class AlarmActivity : ComponentActivity() {
    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOverLockScreen()
        startAlarmSound()
        setContent {
            MartinaTheme {
                AlarmExperience(
                    onAwake = { stopAlarmSound() },
                    onFinish = { finishAndRemoveTask() }
                )
            }
        }
    }

    private fun showOverLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )
    }

    private fun startAlarmSound() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(this@AlarmActivity, uri)
            isLooping = true
            prepare()
            start()
        }
    }

    private fun stopAlarmSound() {
        player?.runCatching {
            if (isPlaying) stop()
            release()
        }
        player = null
        getSystemService(NotificationManager::class.java).cancel(AlarmReceiver.NOTIFICATION_ID)
    }

    override fun onDestroy() {
        stopAlarmSound()
        super.onDestroy()
    }
}

@Composable
private fun AlarmExperience(onAwake: () -> Unit, onFinish: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val settings = remember { AlarmPreferences(context).load() }
    val jokes = remember {
        listOf(
            "Dobro jutro, Martina! Sunce je već na poslu. ☀️",
            "Martinaaa… jastuk kaže da je vrijeme za prekid veze. 😴",
            "Drugi krug! Kava se zabrinula gdje si. ☕",
            "Hitna vijest: krevet te drži kao taoca! 🚨",
            "Ako sad ustaneš, obećavamo da ponedjeljak neće saznati. 🤫",
            "Posljednji poziv za let Martina–Dobar dan! ✈️"
        )
    }
    var jokeIndex by remember { mutableIntStateOf(0) }
    var awake by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var morningInfo by remember { mutableStateOf<MorningInfo?>(null) }

    BackHandler(enabled = !awake) { }

    LaunchedEffect(awake) {
        if (!awake) {
            while (true) {
                delay(30_000)
                if (jokeIndex < jokes.lastIndex) jokeIndex++
            }
        }
    }

    LaunchedEffect(loading) {
        if (loading) {
            morningInfo = WeatherRepository(context).getMorningInfo()
            loading = false
        }
    }

    DisposableEffect(Unit) {
        onDispose { }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (awake) listOf(Color(0xFFFFF2C9), Color(0xFFF2ECFF))
                    else listOf(Color(0xFF4B318E), Color(0xFF241A42))
                )
            )
            .padding(22.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!awake) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(112.dp).background(Color(0xFFFFB84D), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⏰", fontSize = 58.sp)
                }
                Spacer(Modifier.height(30.dp))
                AnimatedContent(targetState = jokes[jokeIndex], label = "alarm-joke") { joke ->
                    Text(
                        joke,
                        color = Color.White,
                        fontSize = 28.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "Poruka broj ${jokeIndex + 1} od ${jokes.size}",
                    color = Color.White.copy(alpha = 0.65f)
                )
                Spacer(Modifier.height(42.dp))
                Button(
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    onClick = {
                        awake = true
                        loading = true
                        onAwake()
                    }
                ) {
                    Icon(Icons.Rounded.CheckCircle, null, modifier = Modifier.size(28.dp))
                    Text("  BUDNA SAM!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            MorningGreeting(
                info = morningInfo,
                reminder = settings.reminder,
                loading = loading,
                onFinish = onFinish
            )
        }
    }
}

@Composable
private fun MorningGreeting(
    info: MorningInfo?,
    reminder: String,
    loading: Boolean,
    onFinish: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("☀️", fontSize = 68.sp)
        Text(
            "Bravo, Martina!",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Dobro jutro, ljepotice. Dan je upravo postao bolji jer si otvorila oči. 💜",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            lineHeight = 25.sp,
            modifier = Modifier.padding(vertical = 14.dp)
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading || info == null) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Gledam kroz prozor umjesto tebe…")
                }
            } else {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoRow(Icons.Rounded.LocationOn, "Nalaziš se: ${info.place}")
                    InfoRow(Icons.Rounded.Schedule, "Sada je ${info.time}")
                    InfoRow(Icons.Rounded.WbSunny, "${info.temperature} · ${info.weather}")
                    Text(info.advice, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (reminder.isNotBlank()) {
            Spacer(Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE7E1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Rounded.Notifications, null, tint = MaterialTheme.colorScheme.secondary)
                    Text("  Podsjetnik: $reminder", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.height(22.dp))
        Button(modifier = Modifier.fillMaxWidth().height(54.dp), onClick = onFinish) {
            Text("Kreni u dan 🚀", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Text("  $text")
    }
}
