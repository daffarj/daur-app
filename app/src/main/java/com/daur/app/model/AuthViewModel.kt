package com.daur.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daur.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

// ── UI State ──────────────────────────────────────────────
sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ── Supabase config dari BuildConfig ──────────────────
    // Nilai didefinisikan di app/build.gradle.kts → buildConfigField
    private val SUPABASE_URL  = BuildConfig.SUPABASE_URL       // https://xxx.supabase.co
    private val SUPABASE_ANON = BuildConfig.SUPABASE_ANON_KEY
    private val AUTH_ENDPOINT = "${SUPABASE_URL}/auth/v1"

    // ── Login ─────────────────────────────────────────────
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email dan password wajib diisi")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = AuthUiState.Error("Format email tidak valid")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val result = loginRequest(email.trim(), password)
                if (result.isSuccess) {
                    _uiState.value = AuthUiState.Success
                } else {
                    _uiState.value = AuthUiState.Error(
                        result.exceptionOrNull()?.message ?: "Login gagal"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Gagal terhubung ke server")
            }
        }
    }

    // ── Register ──────────────────────────────────────────
    fun register(email: String, password: String, nama: String) {
        if (email.isBlank() || password.isBlank() || nama.isBlank()) {
            _uiState.value = AuthUiState.Error("Semua kolom wajib diisi")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Password minimal 6 karakter")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val result = registerRequest(email.trim(), password, nama.trim())
                if (result.isSuccess) {
                    _uiState.value = AuthUiState.Success
                } else {
                    _uiState.value = AuthUiState.Error(
                        result.exceptionOrNull()?.message ?: "Registrasi gagal"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Gagal terhubung ke server")
            }
        }
    }

    fun resetState() { _uiState.value = AuthUiState.Idle }

    // ── HTTP: Login ───────────────────────────────────────
    private suspend fun loginRequest(
        email: String,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val url  = URL("$AUTH_ENDPOINT/token?grant_type=password")
            val body = """{"email":"$email","password":"$password"}"""
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("apikey", SUPABASE_ANON)
                doOutput = true
                outputStream.write(body.toByteArray())
            }
            val code = conn.responseCode
            conn.disconnect()
            if (code == 200) Result.success(Unit)
            else Result.failure(Exception("Login gagal (kode: $code)"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── HTTP: Register ────────────────────────────────────
    private suspend fun registerRequest(
        email: String,
        password: String,
        nama: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val url  = URL("$AUTH_ENDPOINT/signup")
            val body = """{"email":"$email","password":"$password","data":{"nama_lengkap":"$nama"}}"""
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("apikey", SUPABASE_ANON)
                doOutput = true
                outputStream.write(body.toByteArray())
            }
            val code = conn.responseCode
            conn.disconnect()
            if (code == 200 || code == 201) Result.success(Unit)
            else Result.failure(Exception("Registrasi gagal (kode: $code)"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
