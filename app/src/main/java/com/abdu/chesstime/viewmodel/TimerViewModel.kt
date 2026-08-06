package com.abdu.chesstime.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abdu.chesstime.data.SettingsRepository
import com.abdu.chesstime.data.UserSettings
import com.abdu.chesstime.sound.SoundManager
import com.abdu.chesstime.timer.ChessPlayer
import com.abdu.chesstime.timer.ChessTimerData
import com.abdu.chesstime.timer.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)
    private val soundManager = SoundManager(application)

    private val _settingsState = MutableStateFlow(UserSettings())
    val settingsState: StateFlow<UserSettings> = _settingsState.asStateFlow()

    private val _timerState = MutableStateFlow(ChessTimerData())
    val timerState: StateFlow<ChessTimerData> = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private var lastTickTime: Long = 0L

    init {
        viewModelScope.launch {
            repository.userSettingsFlow.collectLatest { settings ->
                _settingsState.value = settings
                if (_timerState.value.timerState == TimerState.NOT_STARTED) {
                    val initialMillis = settings.timeControlSeconds * 1000L
                    _timerState.update {
                        it.copy(
                            initialTimeMillis = initialMillis,
                            whiteTimeMillis = initialMillis,
                            blackTimeMillis = initialMillis
                        )
                    }
                }
            }
        }
    }

    fun handlePlayerTap(player: ChessPlayer) {
        val currentTimer = _timerState.value
        val settings = _settingsState.value

        when (currentTimer.timerState) {
            TimerState.NOT_STARTED -> {
                val nextPlayer = if (player == ChessPlayer.WHITE) ChessPlayer.BLACK else ChessPlayer.WHITE
                val updatedTimer = if (player == ChessPlayer.WHITE) {
                    currentTimer.copy(
                        timerState = TimerState.RUNNING,
                        activePlayer = nextPlayer,
                        whiteMoves = 1
                    )
                } else {
                    currentTimer.copy(
                        timerState = TimerState.RUNNING,
                        activePlayer = nextPlayer,
                        blackMoves = 1
                    )
                }
                _timerState.value = updatedTimer
                soundManager.playSwitchTone(settings.soundEnabled)
                soundManager.triggerVibration(settings.vibrationEnabled)
                startTicker()
            }
            TimerState.RUNNING -> {
                if (currentTimer.activePlayer == player) {
                    val nextPlayer = if (player == ChessPlayer.WHITE) ChessPlayer.BLACK else ChessPlayer.WHITE
                    _timerState.update {
                        it.copy(
                            activePlayer = nextPlayer,
                            whiteMoves = if (player == ChessPlayer.WHITE) it.whiteMoves + 1 else it.whiteMoves,
                            blackMoves = if (player == ChessPlayer.BLACK) it.blackMoves + 1 else it.blackMoves
                        )
                    }
                    soundManager.playSwitchTone(settings.soundEnabled)
                    soundManager.triggerVibration(settings.vibrationEnabled)
                    lastTickTime = SystemClock.elapsedRealtime()
                }
            }
            TimerState.PAUSED -> {
                if (currentTimer.activePlayer == player) {
                    _timerState.update { it.copy(timerState = TimerState.RUNNING) }
                    soundManager.playSwitchTone(settings.soundEnabled)
                    soundManager.triggerVibration(settings.vibrationEnabled)
                    startTicker()
                }
            }
            TimerState.GAME_OVER -> {}
        }
    }

    fun togglePauseResume() {
        val currentTimer = _timerState.value
        val settings = _settingsState.value

        when (currentTimer.timerState) {
            TimerState.RUNNING -> {
                stopTicker()
                _timerState.update { it.copy(timerState = TimerState.PAUSED) }
                soundManager.triggerVibration(settings.vibrationEnabled)
            }
            TimerState.PAUSED -> {
                _timerState.update { it.copy(timerState = TimerState.RUNNING) }
                soundManager.triggerVibration(settings.vibrationEnabled)
                startTicker()
            }
            TimerState.NOT_STARTED -> {
                _timerState.update {
                    it.copy(
                        timerState = TimerState.RUNNING,
                        activePlayer = ChessPlayer.WHITE
                    )
                }
                soundManager.playSwitchTone(settings.soundEnabled)
                soundManager.triggerVibration(settings.vibrationEnabled)
                startTicker()
            }
            TimerState.GAME_OVER -> {
                resetGame()
            }
        }
    }

    fun pauseTimer() {
        if (_timerState.value.timerState == TimerState.RUNNING) {
            stopTicker()
            _timerState.update { it.copy(timerState = TimerState.PAUSED) }
        }
    }

    fun resetGame() {
        stopTicker()
        val initialMillis = _settingsState.value.timeControlSeconds * 1000L
        _timerState.value = ChessTimerData(
            initialTimeMillis = initialMillis,
            whiteTimeMillis = initialMillis,
            blackTimeMillis = initialMillis,
            activePlayer = null,
            timerState = TimerState.NOT_STARTED,
            whiteMoves = 0,
            blackMoves = 0,
            winner = null
        )
    }

    fun updateTimeControl(seconds: Long) {
        viewModelScope.launch {
            repository.updateTimeControl(seconds)
            resetGame()
        }
    }

    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSoundEnabled(enabled)
        }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateVibrationEnabled(enabled)
        }
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            repository.updateThemeMode(mode)
        }
    }

    private fun startTicker() {
        stopTicker()
        lastTickTime = SystemClock.elapsedRealtime()

        timerJob = viewModelScope.launch {
            while (_timerState.value.timerState == TimerState.RUNNING) {
                delay(50L)
                val now = SystemClock.elapsedRealtime()
                val delta = now - lastTickTime
                lastTickTime = now

                val current = _timerState.value
                val active = current.activePlayer ?: continue

                if (active == ChessPlayer.WHITE) {
                    val newTime = current.whiteTimeMillis - delta
                    if (newTime <= 0) {
                        onGameOver(winner = ChessPlayer.BLACK, whiteTime = 0L, blackTime = current.blackTimeMillis)
                        break
                    } else {
                        _timerState.update { it.copy(whiteTimeMillis = newTime) }
                    }
                } else {
                    val newTime = current.blackTimeMillis - delta
                    if (newTime <= 0) {
                        onGameOver(winner = ChessPlayer.WHITE, whiteTime = current.whiteTimeMillis, blackTime = 0L)
                        break
                    } else {
                        _timerState.update { it.copy(blackTimeMillis = newTime) }
                    }
                }
            }
        }
    }

    private fun stopTicker() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun onGameOver(winner: ChessPlayer, whiteTime: Long, blackTime: Long) {
        stopTicker()
        val settings = _settingsState.value
        _timerState.update {
            it.copy(
                timerState = TimerState.GAME_OVER,
                winner = winner,
                whiteTimeMillis = whiteTime,
                blackTimeMillis = blackTime
            )
        }
        soundManager.playGameOverTone(settings.soundEnabled)
        soundManager.triggerGameOverVibration(settings.vibrationEnabled)
    }

    override fun onCleared() {
        super.onCleared()
        stopTicker()
        soundManager.release()
    }
}

// Created by Abdughafur
// Source: https://github.com/Abdughafur/ChessTime
