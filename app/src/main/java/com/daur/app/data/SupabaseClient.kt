package com.daur.app.data

import com.daur.app.BuildConfig
import com.daur.app.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object SupabaseClient {

    private val BASE_URL = BuildConfig.SUPABASE_URL
    private val ANON_KEY = BuildConfig.SUPABASE_ANON_KEY

    // ── GET ────────────────────────────────────────────────
    private suspend fun get(
        path: String,
        token: String? = null,
        params: Map<String, String> = emptyMap()
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // ✅ Fix: takeIf isNotBlank() agar empty string tidak lolos
            val effectiveToken = token?.takeIf { it.isNotBlank() } ?: ANON_KEY

            // 🔍 Debug log — hapus setelah masalah solved
            android.util.Log.d(
                "DAUR_DEBUG",
                "GET $path | token ada: ${!token.isNullOrBlank()} | pakai anon: ${effectiveToken == ANON_KEY}"
            )

            val query = if (params.isEmpty()) ""
            else "?" + params.entries.joinToString("&") { "${it.key}=${it.value}" }
            val url = URL("$BASE_URL/rest/v1$path$query")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
                setRequestProperty("apikey", ANON_KEY)
                setRequestProperty("Authorization", "Bearer $effectiveToken")
                setRequestProperty("Content-Type", "application/json")
            }
            val code = conn.responseCode
            val body = try {
                conn.inputStream.bufferedReader().readText()
            } catch (_: Exception) {
                conn.errorStream?.bufferedReader()?.readText() ?: ""
            }
            conn.disconnect()

            // 🔍 Debug log — hapus setelah masalah solved
            android.util.Log.d("DAUR_DEBUG", "GET $path → HTTP $code")

            if (code in 200..299) {
                Result.success(body)
            } else {
                val msg = when (code) {
                    401  -> "Sesi habis, silakan login ulang"
                    403  -> "Akses ditolak (403) — token: ${effectiveToken.take(20)}..."
                    404  -> "Tabel tidak ditemukan"
                    else -> "Error $code: $body"
                }
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            val msg = when {
                e.message?.contains("Unable to resolve host") == true ||
                        e.message?.contains("No address associated") == true -> "Tidak ada koneksi internet"
                e.message?.contains("timeout") == true ||
                        e.message?.contains("timed out") == true -> "Koneksi timeout, coba lagi"
                e.message?.contains("CLEARTEXT") == true -> "Koneksi diblokir (cleartext)"
                e.message?.contains("SSLHandshake") == true ||
                        e.message?.contains("SSL") == true -> "Gagal verifikasi SSL"
                else -> "Gagal terhubung: ${e.javaClass.simpleName}"
            }
            Result.failure(Exception(msg))
        }
    }

    // ── POST ───────────────────────────────────────────────
    private suspend fun post(
        path: String,
        body: String,
        token: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // ✅ Fix: sama seperti get(), pakai takeIf isNotBlank()
            val effectiveToken = token?.takeIf { it.isNotBlank() } ?: ANON_KEY

            // 🔍 Debug log — hapus setelah masalah solved
            android.util.Log.d(
                "DAUR_DEBUG",
                "POST $path | token ada: ${!token.isNullOrBlank()} | pakai anon: ${effectiveToken == ANON_KEY}"
            )

            val url = URL("$BASE_URL/rest/v1$path")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10_000
                readTimeout = 10_000
                setRequestProperty("apikey", ANON_KEY)
                setRequestProperty("Authorization", "Bearer $effectiveToken")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Prefer", "return=representation")
                doOutput = true
                outputStream.write(body.toByteArray())
            }
            val code = conn.responseCode
            val resp = try {
                conn.inputStream.bufferedReader().readText()
            } catch (_: Exception) {
                conn.errorStream?.bufferedReader()?.readText() ?: ""
            }
            conn.disconnect()

            // 🔍 Debug log — hapus setelah masalah solved
            android.util.Log.d("DAUR_DEBUG", "POST $path → HTTP $code")

            if (code in 200..299) {
                Result.success(resp)
            } else {
                val msg = when (code) {
                    401 -> "Sesi habis, silakan login ulang"
                    403 -> "Tidak punya akses (403)"
                    404 -> "Data tidak ditemukan"
                    else -> "Gagal menyimpan data (kode: $code) | $resp"
                }
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            val msg = when {
                e.message?.contains("Unable to resolve host") == true ||
                        e.message?.contains("No address associated") == true -> "Tidak ada koneksi internet"
                e.message?.contains("timeout") == true ||
                        e.message?.contains("timed out") == true -> "Koneksi timeout, coba lagi"
                e.message?.contains("SSL") == true -> "Gagal verifikasi SSL"
                else -> "Gagal terhubung: ${e.javaClass.simpleName}"
            }
            Result.failure(Exception(msg))
        }
    }

    // ── PROFILE ────────────────────────────────────────────
    suspend fun getProfile(userId: String, token: String): Result<Profile> {
        return get(
            "/profiles", token,
            mapOf("id" to "eq.$userId", "select" to "*")
        ).map { json ->
            val arr = JSONArray(json)
            if (arr.length() == 0) throw Exception("Profile tidak ditemukan")
            arr.getJSONObject(0).toProfile()
        }
    }

    // ── KATALOG SAMPAH ─────────────────────────────────────
    suspend fun getKatalogSampah(
        token: String? = null,
        kategori: String? = null
    ): Result<List<KatalogSampah>> {
        val params = mutableMapOf(
            "is_active" to "eq.true",
            "select" to "*",
            "order" to "nama.asc"
        )
        if (kategori != null && kategori != "Semua") {
            params["kategori"] = "eq.${kategori.lowercase()}"
        }
        return get("/katalog_sampah", token, params).map { json ->
            JSONArray(json).toList { it.toKatalog() }
        }
    }

    // ── SETORAN ────────────────────────────────────────────
    suspend fun getSetoran(userId: String, token: String): Result<List<Setoran>> {
        return get(
            "/setoran", token,
            mapOf(
                "user_id" to "eq.$userId",
                "select" to "*",
                "order" to "created_at.desc"
            )
        ).map { json ->
            JSONArray(json).toList { it.toSetoran() }
        }
    }

    suspend fun buatSetoran(
        userId: String,
        katalogId: String,
        beratKg: Double,
        token: String
    ): Result<String> {
        val kode = "STR-${System.currentTimeMillis()}"
        val setoranBody = JSONObject().apply {
            put("user_id", userId)
            put("kode_setoran", kode)
            put("status", "menunggu")
            put("total_berat", beratKg)
            put("total_poin", 0)
            put("total_harga", 0.0)
        }.toString()

        val setoranResult = post("/setoran", setoranBody, token)
        if (setoranResult.isFailure) {
            return Result.failure(setoranResult.exceptionOrNull() ?: Exception("Gagal membuat setoran"))
        }

        val setoranId = JSONArray(setoranResult.getOrThrow())
            .getJSONObject(0).getString("id")

        val detailBody = JSONObject().apply {
            put("setoran_id", setoranId)
            put("katalog_id", katalogId)
            put("berat_kg", beratKg)
            put("poin_didapat", 0)
            put("harga_didapat", 0.0)
        }.toString()
        post("/detail_setoran", detailBody, token)

        return Result.success(setoranId)
    }

    // ── REWARD ─────────────────────────────────────────────
    suspend fun getReward(
        token: String? = null,
        kategori: String? = null
    ): Result<List<Reward>> {
        val params = mutableMapOf(
            "is_active" to "eq.true",
            "select" to "*",
            "order" to "poin_diperlukan.asc"
        )
        if (kategori != null && kategori != "Semua Hadiah") {
            params["kategori"] = "eq.${kategori.lowercase()}"
        }
        return get("/reward", token, params).map { json ->
            JSONArray(json).toList { it.toReward() }
        }
    }

    suspend fun tukarPoin(
        userId: String,
        rewardId: String,
        poinDigunakan: Int,
        token: String
    ): Result<Unit> {
        val body = JSONObject().apply {
            put("user_id", userId)
            put("reward_id", rewardId)
            put("kode_tukar", "TKR-${System.currentTimeMillis()}")
            put("poin_digunakan", poinDigunakan)
            put("status", "menunggu")
        }.toString()
        return post("/penukaran_poin", body, token).map { }
    }

    // ── EDUKASI ────────────────────────────────────────────
    suspend fun getEdukasi(
        token: String? = null,
        kategori: String? = null,
        search: String? = null
    ): Result<List<Edukasi>> {
        val params = mutableMapOf(
            "is_published" to "eq.true",
            "select" to "*",
            "order" to "created_at.desc"
        )
        if (kategori != null && kategori != "Semua") {
            params["kategori"] = "eq.${kategori.lowercase()}"
        }
        if (!search.isNullOrBlank()) {
            params["judul"] = "ilike.*$search*"
        }
        return get("/edukasi", token, params).map { json ->
            JSONArray(json).toList { it.toEdukasi() }
        }
    }
}

// ── Extensions ─────────────────────────────────────────────
private fun <T> JSONArray.toList(mapper: (JSONObject) -> T): List<T> {
    return (0 until length()).map { mapper(getJSONObject(it)) }
}

private fun JSONObject.toProfile() = Profile(
    id           = optString("id"),
    namaLengkap  = optString("nama_lengkap"),
    noTelepon    = optString("no_telepon"),
    alamat       = optString("alamat"),
    fotoUrl      = optString("foto_url"),
    totalPoin    = optInt("total_poin", 0),
    totalSetoran = optInt("total_setoran", 0)
)

private fun JSONObject.toKatalog() = KatalogSampah(
    id         = optString("id"),
    nama       = optString("nama"),
    kategori   = optString("kategori"),
    deskripsi  = optString("deskripsi"),
    poinPerKg  = optInt("poin_per_kg", 0),
    hargaPerKg = optDouble("harga_per_kg", 0.0),
    iconUrl    = optString("icon_url"),
    isActive   = optBoolean("is_active", true)
)

private fun JSONObject.toSetoran() = Setoran(
    id          = optString("id"),
    userId      = optString("user_id"),
    kodeSetoran = optString("kode_setoran"),
    status      = optString("status"),
    totalPoin   = optInt("total_poin", 0),
    totalBerat  = optDouble("total_berat", 0.0),
    totalHarga  = optDouble("total_harga", 0.0),
    catatan     = optString("catatan"),
    createdAt   = optString("created_at")
)

private fun JSONObject.toReward() = Reward(
    id             = optString("id"),
    nama           = optString("nama"),
    deskripsi      = optString("deskripsi"),
    poinDiperlukan = optInt("poin_diperlukan", 0),
    stok           = optInt("stok", 0),
    gambarUrl      = optString("gambar_url"),
    kategori       = optString("kategori"),
    isActive       = optBoolean("is_active", true)
)

private fun JSONObject.toEdukasi() = Edukasi(
    id          = optString("id"),
    judul       = optString("judul"),
    konten      = optString("konten"),
    ringkasan   = optString("ringkasan"),
    gambarUrl   = optString("gambar_url"),
    kategori    = optString("kategori"),
    isPublished = optBoolean("is_published", true),
    createdAt   = optString("created_at")
)