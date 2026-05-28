package com.daur.app.viewmodel

import android.graphics.Bitmap
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
    val berat           = MutableStateFlow(1.0f)
    val catatan         = MutableStateFlow("")         // ← input catatan
    val fotoBitmap      = MutableStateFlow<Bitmap?>(null) // ← foto dari kamera/galeri

    // Estimasi poin reaktif — update otomatis saat berat/katalog berubah
    val estimasiPoin: StateFlow<Int> = combine(selectedKatalog, berat) { katalog, b ->
        ((katalog?.poinPerKg ?: 0) * b).toInt()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Estimasi harga reaktif
    val estimasiHarga: StateFlow<Double> = combine(selectedKatalog, berat) { katalog, b ->
        (katalog?.hargaPerKg ?: 0.0) * b
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

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

    fun tambahBerat() { berat.value = berat.value + 0.5f }
    fun kurangBerat() { berat.value = (berat.value - 0.5f).coerceAtLeast(0.5f) }
    fun setFoto(bitmap: Bitmap?) { fotoBitmap.value = bitmap }
    fun hapusFoto() { fotoBitmap.value = null }

    fun setor() {
        val katalog = selectedKatalog.value ?: return
        val beratDouble = berat.value.toDouble()
        val totalPoin   = (katalog.poinPerKg * beratDouble).toInt()
        val totalHarga  = katalog.hargaPerKg * beratDouble

        viewModelScope.launch {
            _submitState.value = UiState.Loading
            SupabaseClient.buatSetoran(
                userId     = SessionManager.userId,
                katalogId  = katalog.id,
                beratKg    = beratDouble,
                totalPoin  = totalPoin,
                totalHarga = totalHarga,
                catatan    = catatan.value,
                fotoBitmap = fotoBitmap.value,
                token      = SessionManager.accessToken
            )
                .onSuccess {
                    // Reset form setelah berhasil
                    catatan.value    = ""
                    fotoBitmap.value = null
                    _submitState.value = UiState.Success(it)
                }
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

    // ── State untuk proses delete ──────────────────────────
    private val _deleteState = MutableStateFlow<UiState<Unit>?>(null)
    val deleteState: StateFlow<UiState<Unit>?> = _deleteState.asStateFlow()

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
                .onSuccess { allItems = it; applyFilter() }
                .onFailure { _state.value = UiState.Error(it.message ?: "Gagal memuat riwayat") }
        }
    }

    fun setFilter(f: String) { _selectedFilter.value = f; applyFilter() }

    fun deleteSetoran(setoranId: String, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            SupabaseClient.deleteSetoran(setoranId, SessionManager.accessToken)
                .onSuccess {
                    // Hapus dari list lokal tanpa perlu reload penuh
                    allItems = allItems.filter { it.id != setoranId }
                    applyFilter()
                    _deleteState.value = UiState.Success(Unit)
                    onDeleted()
                }
                .onFailure {
                    _deleteState.value = UiState.Error(it.message ?: "Gagal menghapus setoran")
                }
        }
    }

    fun resetDelete() { _deleteState.value = null }

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
    private val _state = MutableStateFlow<UiState<List<UserVoucher>>>(UiState.Loading)
    val state: StateFlow<UiState<List<UserVoucher>>> = _state.asStateFlow()

    private val _klaimState = MutableStateFlow<UiState<Unit>?>(null)
    val klaimState: StateFlow<UiState<Unit>?> = _klaimState.asStateFlow()

    private val _selectedKategori = MutableStateFlow("Voucher Saya")
    val selectedKategori: StateFlow<String> = _selectedKategori.asStateFlow()

    private var allItems: List<UserVoucher> = emptyList()

    val kategoriList = listOf("Voucher Saya")

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val userId = SessionManager.userId
            if (userId.isEmpty()) {
                _state.value = UiState.Error("Sesi habis. Silakan login kembali.")
                return@launch
            }
            SupabaseClient.getUserVouchers(userId, SessionManager.accessToken)
                .onSuccess { allItems = it; applyFilter() }
                .onFailure { 
                    // Jika gagal memuat (misal belum ada data atau RLS error untuk pengguna baru),
                    // anggap sebagai belum ada voucher (list kosong)
                    allItems = emptyList()
                    applyFilter()
                }
        }
    }

    fun setKategori(k: String) { _selectedKategori.value = k; applyFilter() }

    private fun applyFilter() {
        _state.value = if (allItems.isEmpty()) UiState.Empty else UiState.Success(allItems)
    }

    fun klaim(kode: String) {
        if (kode.isBlank()) return
        viewModelScope.launch {
            _klaimState.value = UiState.Loading
            SupabaseClient.klaimVoucherKode(SessionManager.userId, kode, SessionManager.accessToken)
                .onSuccess {
                    _klaimState.value = UiState.Success(Unit)
                    load() // Reload setelah berhasil klaim
                }
                .onFailure {
                    _klaimState.value = UiState.Error(it.message ?: "Gagal mengklaim voucher")
                }
        }
    }

    fun resetKlaim() { _klaimState.value = null }
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
                .onSuccess { allItems = it; applyFilter() }
                .onFailure { _state.value = UiState.Error(it.message ?: "Gagal memuat edukasi") }
        }
    }

    fun setFilter(f: String) { _selectedFilter.value = f; applyFilter() }

    fun setSearch(q: String) { _searchQuery.value = q; applyFilter() }

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