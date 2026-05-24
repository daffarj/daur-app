package com.daur.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "session")

object SessionManager {
    private val TOKEN_KEY   = stringPreferencesKey("access_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")

    var accessToken: String = ""
    var userId: String = ""
    val isLoggedIn: Boolean get() = accessToken.isNotEmpty()

    // ── Event stream: emit true saat sesi expired ─────────
    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpired = _sessionExpired.asSharedFlow()

    // Dipanggil dari SupabaseClient saat dapat 401
    fun notifySessionExpired() {
        _sessionExpired.tryEmit(Unit)
    }

    // Simpan ke disk
    suspend fun save(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY]   = accessToken
            prefs[USER_ID_KEY] = userId
        }
    }

    // Load dari disk saat app start
    suspend fun load(context: Context) {
        val prefs = context.dataStore.data.first()
        accessToken = prefs[TOKEN_KEY]   ?: ""
        userId      = prefs[USER_ID_KEY] ?: ""
    }

    // Clear saat logout
    suspend fun clear(context: Context) {
        accessToken = ""
        userId = ""
        context.dataStore.edit { it.clear() }
    }
}