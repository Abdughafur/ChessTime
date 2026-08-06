package com.abdu.chesstime.ui

import android.app.Activity
import android.content.res.Configuration
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.abdu.chesstime.data.UserSettings
import com.abdu.chesstime.timer.ChessPlayer
import com.abdu.chesstime.timer.ChessTimerData
import com.abdu.chesstime.timer.TimerState
import com.abdu.chesstime.ui.components.ControlsOverlay
import com.abdu.chesstime.ui.components.PlayerTimerCard

@Composable
fun MainScreen(
    timerState: ChessTimerData,
    userSettings: UserSettings,
    onPlayerTap: (ChessPlayer) -> Unit,
    onPauseResumeToggle: () -> Unit,
    onPauseTimer: () -> Unit,
    onReset: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                onPauseTimer()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            onPauseTimer()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(timerState.timerState) {
        val activity = context as? Activity
        if (timerState.timerState == TimerState.RUNNING) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                PlayerTimerCard(
                    player = ChessPlayer.BLACK,
                    timeMillis = timerState.blackTimeMillis,
                    moves = timerState.blackMoves,
                    isActive = timerState.activePlayer == ChessPlayer.BLACK && timerState.timerState == TimerState.RUNNING,
                    isGameOver = timerState.timerState == TimerState.GAME_OVER,
                    isWinner = timerState.winner == ChessPlayer.BLACK,
                    onTap = { onPlayerTap(ChessPlayer.BLACK) },
                    modifier = Modifier.weight(1f)
                )

                PlayerTimerCard(
                    player = ChessPlayer.WHITE,
                    timeMillis = timerState.whiteTimeMillis,
                    moves = timerState.whiteMoves,
                    isActive = timerState.activePlayer == ChessPlayer.WHITE && timerState.timerState == TimerState.RUNNING,
                    isGameOver = timerState.timerState == TimerState.GAME_OVER,
                    isWinner = timerState.winner == ChessPlayer.WHITE,
                    onTap = { onPlayerTap(ChessPlayer.WHITE) },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                PlayerTimerCard(
                    player = ChessPlayer.BLACK,
                    timeMillis = timerState.blackTimeMillis,
                    moves = timerState.blackMoves,
                    isActive = timerState.activePlayer == ChessPlayer.BLACK && timerState.timerState == TimerState.RUNNING,
                    isGameOver = timerState.timerState == TimerState.GAME_OVER,
                    isWinner = timerState.winner == ChessPlayer.BLACK,
                    onTap = { onPlayerTap(ChessPlayer.BLACK) },
                    modifier = Modifier.weight(1f)
                )

                PlayerTimerCard(
                    player = ChessPlayer.WHITE,
                    timeMillis = timerState.whiteTimeMillis,
                    moves = timerState.whiteMoves,
                    isActive = timerState.activePlayer == ChessPlayer.WHITE && timerState.timerState == TimerState.RUNNING,
                    isGameOver = timerState.timerState == TimerState.GAME_OVER,
                    isWinner = timerState.winner == ChessPlayer.WHITE,
                    onTap = { onPlayerTap(ChessPlayer.WHITE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        ControlsOverlay(
            timerState = timerState.timerState,
            onPauseResumeToggle = onPauseResumeToggle,
            onReset = onReset,
            onOpenSettings = {
                onPauseTimer()
                onOpenSettings()
            },
            onOpenAbout = {
                onPauseTimer()
                onOpenAbout()
            },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(12.dp)
        )

        if (timerState.timerState == TimerState.GAME_OVER && timerState.winner != null) {
            val winnerName = if (timerState.winner == ChessPlayer.WHITE) "WHITE" else "BLACK"
            AlertDialog(
                onDismissRequest = {},
                title = {
                    Text(
                        text = "⏰ TIME'S UP!",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                },
                text = {
                    Text(
                        text = "$winnerName wins on time!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .testTag("text_winner_message")
                    )
                },
                confirmButton = {
                    Button(
                        onClick = onReset,
                        modifier = Modifier.testTag("button_new_game")
                    ) {
                        Text("New Game")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onReset,
                        modifier = Modifier.testTag("button_reset_game_dialog")
                    ) {
                        Text("Reset")
                    }
                }
            )
        }
    }
}

// Created by Abdughafur
// Source: https://github.com/Abdughafur/ChessTime
