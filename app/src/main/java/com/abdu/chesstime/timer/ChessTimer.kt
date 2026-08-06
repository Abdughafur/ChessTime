package com.abdu.chesstime.timer

import java.util.Locale

enum class ChessPlayer {
    WHITE, BLACK
}

enum class TimerState {
    NOT_STARTED,
    RUNNING,
    PAUSED,
    GAME_OVER
}

data class ChessTimerData(
    val initialTimeMillis: Long = 600_000L,
    val whiteTimeMillis: Long = 600_000L,
    val blackTimeMillis: Long = 600_000L,
    val activePlayer: ChessPlayer? = null,
    val timerState: TimerState = TimerState.NOT_STARTED,
    val whiteMoves: Int = 0,
    val blackMoves: Int = 0,
    val winner: ChessPlayer? = null
)

object TimeFormatter {
    fun formatTime(millis: Long): String {
        if (millis <= 0) return "00:00"
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val tenths = (millis % 1000) / 100

        return if (minutes >= 60) {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            String.format(Locale.US, "%d:%02d:%02d", hours, remainingMinutes, seconds)
        } else if (totalSeconds >= 20) {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        } else {
            String.format(Locale.US, "%02d.%d", seconds, tenths)
        }
    }
}

// Created by Abdughafur
// Source: https://github.com/Abdughafur/ChessTime
