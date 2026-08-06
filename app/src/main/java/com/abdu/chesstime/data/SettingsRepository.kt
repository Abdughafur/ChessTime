package com.abdu.chesstime.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chess_time_settings")

data class UserSettings(
    val timeControlSeconds: Long = 600L,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val themeMode: String = "DARK"
)

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val TIME_CONTROL_SECONDS = longPreferencesKey("time_control_seconds")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            timeControlSeconds = preferences[PreferencesKeys.TIME_CONTROL_SECONDS] ?: 600L,
            soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
            themeMode = preferences[PreferencesKeys.THEME_MODE] ?: "DARK"
        )
    }

    suspend fun updateTimeControl(seconds: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TIME_CONTROL_SECONDS] = seconds
        }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun updateThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }
}

// Created by Abdughafur
// Source: https://github.com/Abdughafur/ChessTime
