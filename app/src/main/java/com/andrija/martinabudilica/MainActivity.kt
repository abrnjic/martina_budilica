package com.andrija.martinabudilica

import android.Manifest
import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andrija.martinabudilica.alarm.AlarmScheduler
import com.andrija.martinabudilica.data.AlarmPreferences
import com.andrija.martinabudilica.data.AlarmSettings
import com.andrija.martinabudilica.ui.theme.MartinaTheme
import java.util.Locale
import androidx.compose.foundation.clickable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MartinaTheme { AlarmSetupScreen() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlarmSetupScreen() {
    val context = LocalContext.current
    val preferences = remember { AlarmPreferences(context) }
    val initial = remember { preferences.load() }
    var hour by remember { mutableIntStateOf(initial.hour) }
    var minute by remember { mutableIntStateOf(initial.minute) }
    var reminder by remember { mutableStateOf(initial.reminder) }
    var enabled by remember { mutableStateOf(initial.enabled) }
    var ringtoneUri by remember { mutableStateOf(initial.ringtoneUri) }
    var daysOfWeek by remember { mutableStateOf(initial.daysOfWeek) }
    var bedtimeReminderEnabled by remember { mutableStateOf(initial.bedtimeReminderEnabled) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    val ringtoneLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val uri = result.data?.getParcelableExtra<Uri>(android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            ringtoneUri = uri?.toString()
        }
    }

    fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions += Manifest.permission.POST_NOTIFICATIONS
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    fun ensureExactAlarmPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val manager = context.getSystemService(AlarmManager::class.java)
        if (manager.canScheduleExactAlarms()) return true
        context.startActivity(
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        )
        Toast.makeText(context, "Dopusti točne alarme pa pokušaj ponovno.", Toast.LENGTH_LONG).show()
        return false
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFFFF1EA), Color(0xFFF4EEFF)))
                )
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Alarm, null, tint = Color.White, modifier = Modifier.size(42.dp))
            }
            Spacer(Modifier.height(14.dp))
            Text("Dobro jutro, Martina!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                "Osobna budilica koja se ne ljuti — samo postaje sve duhovitija.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
            Spacer(Modifier.height(18.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Vrijeme buđenja", fontWeight = FontWeight.SemiBold)
                    Text(
                        String.format(Locale.getDefault(), "%02d:%02d", hour, minute),
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedButton(onClick = {
                        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
                            hour = selectedHour
                            minute = selectedMinute
                        }, hour, minute, true).show()
                    }) { Text("Promijeni vrijeme") }

                    Spacer(Modifier.height(18.dp))
                    OutlinedTextField(
                        value = reminder,
                        onValueChange = { reminder = it },
                        label = { Text("Martinin jutarnji podsjetnik") },
                        placeholder = { Text("Npr. Danas u 10:00 imaš sastanak") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val days = listOf(
                            2 to "Pon", 3 to "Uto", 4 to "Sri", 5 to "Čet", 6 to "Pet", 7 to "Sub", 1 to "Ned"
                        )
                        days.forEach { (calendarDay, label) ->
                            val isSelected = daysOfWeek.contains(calendarDay)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        daysOfWeek = if (isSelected) {
                                            daysOfWeek - calendarDay
                                        } else {
                                            daysOfWeek + calendarDay
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = if (isSelected) Color.White else Color.Black, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(onClick = {
                        val intent = Intent(android.media.RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                            putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TYPE, android.media.RingtoneManager.TYPE_ALARM)
                            putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                            putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                        }
                        ringtoneLauncher.launch(intent)
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (ringtoneUri != null) "Melodija odabrana" else "Odaberi melodiju")
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Podsjetnik za spavanje", fontWeight = FontWeight.Bold)
                            Text("8h prije alarma", fontSize = 13.sp)
                        }
                        Switch(checked = bedtimeReminderEnabled, onCheckedChange = { bedtimeReminderEnabled = it })
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Aktiviraj alarm", fontWeight = FontWeight.Bold)
                            Text(if (enabled) "Spreman je za akciju" else "Trenutačno odmara", fontSize = 13.sp)
                        }
                        Switch(checked = enabled, onCheckedChange = { enabled = it })
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth().height(54.dp),
                onClick = {
                    requestPermissions()
                    val settings = AlarmSettings(hour, minute, reminder.trim(), enabled, ringtoneUri, daysOfWeek, bedtimeReminderEnabled)
                    preferences.save(settings)
                    if (enabled && ensureExactAlarmPermission()) {
                        if (AlarmScheduler.schedule(context, settings)) {
                            Toast.makeText(context, "Alarm je spremljen. Martina nema šanse! 😄", Toast.LENGTH_LONG).show()
                        }
                    } else if (!enabled) {
                        AlarmScheduler.cancel(context)
                        Toast.makeText(context, "Alarm je isključen.", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(Icons.Rounded.Favorite, null)
                Text("  Spremi Martinin alarm")
            }

            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    requestPermissions()
                    val settings = AlarmSettings(hour, minute, reminder.trim(), true, ringtoneUri, daysOfWeek, bedtimeReminderEnabled)
                    preferences.save(settings)
                    enabled = true
                    if (ensureExactAlarmPermission() && AlarmScheduler.scheduleTest(context)) {
                        Toast.makeText(context, "Probni alarm zvoni za 10 sekundi!", Toast.LENGTH_LONG).show()
                    }
                }
            ) {
                Icon(Icons.Rounded.NotificationsActive, null)
                Text("  Isprobaj za 10 sekundi")
            }

            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    context.startActivity(Intent(context, NotebookActivity::class.java))
                }
            ) {
                Icon(Icons.Rounded.Book, null)
                Text("  Otvori Martinin Dnevnik")
            }

            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LocationOn, null, tint = MaterialTheme.colorScheme.secondary)
                Text(
                    " Lokacija se koristi samo nakon buđenja za mjesto i prognozu.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
