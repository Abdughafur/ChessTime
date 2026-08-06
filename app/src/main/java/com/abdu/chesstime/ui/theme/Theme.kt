package com.abdu.chesstime.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ChessDarkPrimary,
    secondary = ChessDarkSecondary,
    background = ChessDarkBackground,
    surface = ChessDarkSurface
)

private val LightColorScheme = lightColorScheme(
    primary = ChessLightPrimary,
    secondary = ChessLightSecondary,
    background = ChessLightBackground,
    surface = ChessLightSurface
)

@Composable
fun ChessTimeTheme(
    themeMode: String = "DARK",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Created by Abdughafur
// Source: https://github.com/Abdughafur/ChessTime
