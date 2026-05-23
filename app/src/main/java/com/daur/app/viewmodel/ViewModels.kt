package com.daur.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daur.app.data.SessionManager
import com.daur.app.data.SupabaseClient
import com.daur.app.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ── Generic UI state ───────────────────────────────────────
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    object Empty   : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// ──────────────────────────────────────────────────────────
// PROFIL VIEW MODEL
// ──────────────────────────────────────────────────────────
class ProfilViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<Profile>>(UiState.Loading)
    val state: StateFlow<UiState<Profile>> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val uid   = SessionManager.userId
            val token = SessionManager.accessToken
            if (uid.isEmpty()) {
                // Tampilkan data dummy kalau belum login (preview)
                _state.value = UiState.Success(Profile(namaLengkap = "Pengguna Daur", totalPoin = 0))
                return@launch
            }
            SupabaseClient.getProfile(uid, token)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Gagal memuat profil") }
        }
    }

    fun logout(context: android.content.Context) {
        viewModelScope.launch {
            SessionManager.clear(context)
        }
    }
}

// ──────────────────────────────────────────────────────────
// KATALOG SAMPAH VIEW MODEL
// ──────────────────────────────────────────────────────────
class KatalogViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<KatalogSampah>>>(UiState.Loading)
    val state: StateFlow<UiState<List<KatalogSampah>>> = _state.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Semua")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allItems: List<KatalogSampah> = emptyList()

    val filters = listOf("Semua", "Plastik", "Kertas", "Logam", "Elektronik", "Kaca")

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            SupabaseClient.getKatalogSampah(SessionManager.accessToken)
                .onSuccess {
                    allItems = it
                    applyFilter()
                }
                .onFailure { _state.value = UiState.Error(it.message ?: "Gagal memuat katalog") }
        }
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
        applyFilter()
    }

    fun setSearch(q: String) {
        _searchQuery.value = q
        applyFilter()
    }

    private fun applyFilter() {
        val f = _selectedFilter.value
        val q = _searchQuery.value
        val filtered = allItems.filter { item ->
            (f == "Semua" || item.kategori.equals(f, ignoreCase = true)) &&
                    (q.isEmpty() || item.nama.contains(q, ignoreCase = true))
        }
        _state.value = if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
    }
}

// ──────────────────────────────────────────────────────────
// SETOR SAMPAH VIEW MODEL
// ──────────────────────────────────────────────────────────
class SetorViewModel : ViewModel() {
    private val _katalogState = MutableStateFlow<UiState<List<KatalogSampah>>>(UiState.Loading)
    val katalogState: StateFlow<UiState<List<KatalogSampah>>> = _katalogState.asStateFlow()

    private val _submitState = MutableStateFlow<UiState<String>?>(null)
    val submitState: StateFlow<UiState<String>?> = _submitState.asStateFlow()

    val selectedKatalog = MutableStateFlow<KatalogSampah?>(null)
    val berat = MutableStateFlow(1.0f)

    val estimasiPoin: Int get() =
        ((selectedKatalog.value?.poinPerKg ?: 15) * berat.value).toInt()

    init { loadKatalog() }

    fun loadKatalog() {
        viewModelScope.launch {
            _katalogState.value = UiState.Loading
            SupabaseClient.getKatalogSampah(SessionManager.accessToken)
                .onSuccess {
                    _katalogState.value = if (it.isEmpty()) UiState.Empty else UiState.Success(it)
                    if (it.isNotEmpty()) selectedKatalog.value = it.first()
                }
                .onFailure { _katalogState.value = UiState.Error(it.message ?: "Gagal memuat katalog") }
        }
    }

    fun tambahBerat() { berat.value = (berat.value + 0.5f) }
    fun kurangBerat() { if (berat.value > 0.5f) berat.value = (berat.value - 0.5f).coerceAtLeast(0.5f) }

    fun setor() {
        val katalog = selectedKatalog.value ?: return
        viewModelScope.launch {
            _submitState.value = UiState.Loading
            SupabaseClient.buatSetoran(
                userId    = SessionManager.userId,
                katalogId = katalog.id,
                beratKg   = berat.value.toDouble(),
                token     = SessionManager.accessToken
            )
                .onSuccess { _submitState.value = UiState.Success(it) }
                .onFailure { _submitState.value = UiState.Error(it.message ?: "Gagal mengirim setoran") }
        }
    }

    fun resetSubmit() { _submitState.value = null }
}

// ──────────────────────────────────────────────────────────
// RIWAYAT SETORAN VIEW MODEL
// ──────────────────────────────────────────────────────────
class RiwayatViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<Setoran>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Setoran>>> = _state.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Semua")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private var allItems: List<Setoran> = emptyList()

    val filters = listOf("Semua", "menunggu", "diproses", "selesai", "ditolak")
    val filterLabels = mapOf(
        "Semua"    to "Semua",
        "menunggu" to "Menunggu",
        "diproses" to "Diproses",
        "selesai"  to "Selesai",
        "ditolak"  to "Ditolak"
    )

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val uid = SessionManager.userId
            if (uid.isEmpty()) { _state.value = UiState.Empty; return@launch }
            SupabaseClient.getSetoran(uid, SessionManager.accessToken)
                .onSuccess {
                    allItems = it
                    applyFilter()
                }
                .onFailure { _state.value = UiState.Error(it.message ?: "Gagal memuat riwayat") }
        }
    }

    fun setFilter(f: String) {
        _selectedFilter.value = f
        applyFilter()
    }

    private fun applyFilter() {
        val f = _selectedFilter.value
        val filtered = if (f == "Semua") allItems else allItems.filter { it.status == f }
        _state.value = if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
    }
}

// ──────────────────────────────────────────────────────────
// TUKAR POIN VIEW MODEL
// ──────────────────────────────────────────────────────────
class TukarPoinViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<Reward>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Reward>>> = _state.asStateFlow()

    private val _tukarState = MutableStateFlow<UiState<Unit>?>(null)
    val tukarState: StateFlow<UiState<Unit>?> = _tukarState.asStateFlow()

    private val _selectedKategori = MutableStateFlow("Semua Hadiah")
    val selectedKategori: StateFlow<String> = _selectedKategori.asStateFlow()

    private var allItems: List<Reward> = emptyList()

    val kategoriList = listOf("Semua Hadiah", "Voucher", "Produk", "Donasi")

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            SupabaseClient.getReward(SessionManager.accessToken)
                .onSuccess {
                    allItems = it
                    applyFilter()
                }
                .onFailure { _state.value = UiState.Error(it.message ?: "Gagal memuat hadiah") }
        }
    }

    fun setKategori(k: String) {
        _selectedKategori.value = k
        applyFilter()
    }

    private fun applyFilter() {
        val k = _selectedKategori.value
        val filtered = if (k == "Semua Hadiah") allItems
        else allItems.filter { it.kategori.equals(k, ignoreCase = true) }
        _state.value = if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
    }

    fun tukar(reward: Reward) {
        viewModelScope.launch {
            _tukarState.value = UiState.Loading
            SupabaseClient.tukarPoin(
                userId        = SessionManager.userId,
                rewardId      = reward.id,
                poinDigunakan = reward.poinDiperlukan,
                token         = SessionManager.accessToken
            )
                .onSuccess { _tukarState.value = UiState.Success(Unit) }
                .onFailure { _tukarState.value = UiState.Error(it.message ?: "Gagal menukar poin") }
        }
    }

    fun resetTukar() { _tukarState.value = null }
}

// ──────────────────────────────────────────────────────────
// EDUKASI VIEW MODEL
// ──────────────────────────────────────────────────────────
class EdukasiViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<Edukasi>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Edukasi>>> = _state.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Semua")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filters = listOf("Semua", "tips", "artikel", "video", "infografis")
    val filterLabels = mapOf(
        "Semua"      to "Semua",
        "tips"       to "Tips",
        "artikel"    to "Artikel",
        "video"      to "Video",
        "infografis" to "Infografis"
    )

    private var allItems: List<Edukasi> = emptyList()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            SupabaseClient.getEdukasi(SessionManager.accessToken)
                .onSuccess {
                    allItems = it
                    applyFilter()
                }
                .onFailure { _state.value = UiState.Error(it.message ?: "Gagal memuat edukasi") }
        }
    }

    fun setFilter(f: String) {
        _selectedFilter.value = f
        applyFilter()
    }

    fun setSearch(q: String) {
        _searchQuery.value = q
        applyFilter()
    }

    private fun applyFilter() {
        val f = _selectedFilter.value
        val q = _searchQuery.value
        val filtered = allItems.filter { item ->
            (f == "Semua" || item.kategori.equals(f, ignoreCase = true)) &&
                    (q.isEmpty() || item.judul.contains(q, ignoreCase = true))
        }
        _state.value = if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
    }
}