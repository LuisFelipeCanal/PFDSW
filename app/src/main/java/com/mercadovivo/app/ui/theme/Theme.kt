package com.mercadovivo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = MercadoVivoPrimary,
    secondary = MercadoVivoSecondary,
    tertiary = MercadoVivoTertiary,
    background = MercadoVivoBackground,
    surface = MercadoVivoSurface,
    error = MercadoVivoError
)

private val DarkColorScheme = darkColorScheme(
    primary = MercadoVivoPrimary,
    secondary = MercadoVivoSecondary,
    tertiary = MercadoVivoTertiary,
    background = MercadoVivoBackground,
    surface = MercadoVivoSurface,
    error = MercadoVivoError
)

@Composable
fun MercadoVivoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = MercadoVivoTypography,
        content = content
    )
}

