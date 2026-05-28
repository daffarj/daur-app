package com.daur.app.model

// ── Profile (tabel: profiles) ──────────────────────────────
data class Profile(
    val id: String = "",
    val namaLengkap: String = "",
    val noTelepon: String = "",
    val alamat: String = "",
    val fotoUrl: String = "",
    val totalPoin: Int = 0,
    val totalSetoran: Int = 0
)

// ── Katalog Sampah (tabel: katalog_sampah) ─────────────────
data class KatalogSampah(
    val id: String = "",
    val nama: String = "",
    val kategori: String = "",
    val deskripsi: String = "",
    val poinPerKg: Int = 0,
    val hargaPerKg: Double = 0.0,
    val iconUrl: String = "",
    val isActive: Boolean = true
)

// ── Setoran (tabel: setoran) ───────────────────────────────
data class Setoran(
    val id: String = "",
    val userId: String = "",
    val kodeSetoran: String = "",
    val status: String = "menunggu",
    val totalPoin: Int = 0,
    val totalBerat: Double = 0.0,
    val totalHarga: Double = 0.0,
    val catatan: String = "",
    val fotoUrl: String = "",       // ← kolom foto_url dari DB
    val createdAt: String = "",
    val judulItem: String = ""
)

// ── Detail Setoran (tabel: detail_setoran) ─────────────────
data class DetailSetoran(
    val id: String = "",
    val setoranId: String = "",
    val katalogId: String = "",
    val beratKg: Double = 0.0,
    val poinDidapat: Int = 0,
    val hargaDidapat: Double = 0.0
)

// ── Reward (tabel: reward) ─────────────────────────────────
data class Reward(
    val id: String = "",
    val nama: String = "",
    val deskripsi: String = "",
    val poinDiperlukan: Int = 0,
    val stok: Int = 0,
    val gambarUrl: String = "",
    val kategori: String = "",
    val isActive: Boolean = true
)

// ── Penukaran Poin (tabel: penukaran_poin) ─────────────────
data class PenukaranPoin(
    val id: String = "",
    val userId: String = "",
    val rewardId: String = "",
    val kodeTukar: String = "",
    val poinDigunakan: Int = 0,
    val status: String = "menunggu",
    val createdAt: String = ""
)

// ── Edukasi (tabel: edukasi) ───────────────────────────────
data class Edukasi(
    val id: String = "",
    val judul: String = "",
    val konten: String = "",
    val ringkasan: String = "",
    val gambarUrl: String = "",
    val kategori: String = "",
    val isPublished: Boolean = true,
    val createdAt: String = ""
)

// ── Voucher (tabel: voucher) ───────────────────────────────
data class Voucher(
    val id: String = "",
    val kode: String = "",
    val nama: String = "",
    val deskripsi: String = "",
    val tipeDiskon: String = "",
    val nilaiDiskon: Double = 0.0,
    val kuota: Int = 0,
    val kuotaTerpakai: Int = 0,
    val isActive: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
)

// ── User Voucher (tabel: user_voucher) ───────────────────────
data class UserVoucher(
    val id: String = "",
    val userId: String = "",
    val voucherId: String = "",
    val status: String = "belum_digunakan",
    val digunakanPada: String = "",
    val createdAt: String = "",
    val voucher: Voucher? = null
)
