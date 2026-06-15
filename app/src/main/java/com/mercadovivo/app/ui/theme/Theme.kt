package com.mercadovivo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MercadoVivoPrimary,
    onPrimary = Color.White,
    secondary = MercadoVivoSecondary,
    onSecondary = Color.White,
    tertiary = MercadoVivoAccent,
    background = MercadoVivoBackground,
    onBackground = MercadoVivoForeground,
    surface = MercadoVivoSurface,
    onSurface = MercadoVivoForeground,
    surfaceVariant = Color(0xFFFDEEE9),
    onSurfaceVariant = Color(0xFFE27553),
    outline = MercadoVivoBorder
)

@Composable
fun MercadoVivoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
