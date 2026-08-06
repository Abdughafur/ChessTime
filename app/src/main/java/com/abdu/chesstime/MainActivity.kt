package com.abdu.chesstime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abdu.chesstime.ui.AboutScreen
import com.abdu.chesstime.ui.MainScreen
import com.abdu.chesstime.ui.settings.SettingsScreen
import com.abdu.chesstime.ui.theme.ChessTimeTheme
import com.abdu.chesstime.viewmodel.TimerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TimerViewModel = viewModel()
            val timerState by viewModel.timerState.collectAsStateWithLifecycle()
            val userSettings by viewModel.settingsState.collectAsStateWithLifecycle()
            val navController = rememberNavController()

            ChessTimeTheme(themeMode = userSettings.themeMode) {
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            timerState = timerState,
                            userSettings = userSettings,
                            onPlayerTap = { player -> viewModel.handlePlayerTap(player) },
                            onPauseResumeToggle = { viewModel.togglePauseResume() },
                            onPauseTimer = { viewModel.pauseTimer() },
                            onReset = { viewModel.resetGame() },
                            onOpenSettings = { navController.navigate("settings") },
                            onOpenAbout = { navController.navigate("about") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            userSettings = userSettings,
                            onBack = { navController.popBackStack() },
                            onSelectTimeControl = { seconds -> viewModel.updateTimeControl(seconds) },
                            onToggleSound = { enabled -> viewModel.updateSoundEnabled(enabled) },
                            onToggleVibration = { enabled -> viewModel.updateVibrationEnabled(enabled) },
                            onSelectThemeMode = { mode -> viewModel.updateThemeMode(mode) }
                        )
                    }
                    composable("about") {
                        AboutScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

// Created by Abdughafur
// Source: https://github.com/Abdughafur/ChessTime
