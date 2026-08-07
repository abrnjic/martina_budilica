package com.andrija.martinabudilica.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MartinaColors = lightColorScheme(
    primary = Color(0xFF7B5AE0),
    onPrimary = Color.White,
    secondary = Color(0xFFFF6B6B),
    tertiary = Color(0xFFFFB84D),
    background = Color(0xFFFFF8F4),
    surface = Color.White,
    onBackground = Color(0xFF292338),
    onSurface = Color(0xFF292338)
)

@Composable
fun MartinaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = MartinaColors, content = content)
}
