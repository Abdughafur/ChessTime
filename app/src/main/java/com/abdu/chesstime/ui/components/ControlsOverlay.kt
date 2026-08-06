package com.abdu.chesstime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.abdu.chesstime.timer.TimerState

@Composable
fun ControlsOverlay(
    timerState: TimerState,
    onPauseResumeToggle: () -> Unit,
    onReset: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .testTag("controls_overlay"),
        color = Color(0xFF22252A),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("button_settings"),
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }

            val isRunning = timerState == TimerState.RUNNING
            val playPauseColor = if (isRunning) Color(0xFFFF9800) else Color(0xFF4CAF50)

            IconButton(
                onClick = onPauseResumeToggle,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(playPauseColor)
                    .testTag("button_play_pause"),
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause Timer" else "Start Timer",
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = onReset,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("button_reset"),
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Timer"
                )
            }

            IconButton(
                onClick = onOpenAbout,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("button_about"),
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "About App"
                )
            }
        }
    }
}

// Created by Abdughafur
// Source: https://github.com/Abdughafur/ChessTime
