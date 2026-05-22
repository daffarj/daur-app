package com.daur.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daur.app.ui.theme.*

data class ArtikelItem(
    val judul: String,
    val ringkasan: String,
    val kategori: String,
    val kategoriColor: Color,
    val kategoriBg: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdukasiLingkunganScreen() {
    val filterList = listOf("Semua", "Tips Daur Ulang", "Kompos", "Plastik")
    var selectedFilter by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }
    var bookmarkedItems by remember { mutableStateOf(setOf<Int>()) }

    val artikelList = listOf(
        ArtikelItem(
            judul = "5 Langkah Mudah Memilah Sampah Rumah Tangga",
            ringkasan = "Mulai kebiasaan baik dari dapur Anda dengan cara sederhana...",
            kategori = "Tips Daur Ulang",
            kategoriColor = Primary,
            kategoriBg = Primary.copy(alpha = 0.1f)
        ),
        ArtikelItem(
            judul = "Panduan Kompos Organik untuk Pemula",
            ringkasan = "Ubah sisa makanan menjadi nutrisi bagi tanaman kesayangan Anda.",
            kategori = "Kompos",
            kategoriColor = Secondary,
            kategoriBg = Secondary.copy(alpha = 0.1f)
        ),
        ArtikelItem(
            judul = "Mengurangi Jejak Plastik dalam 30 Hari",
            ringkasan = "Tantangan harian untuk gaya hidup yang lebih berkelanjutan.",
            kategori = "Plastik",
            kategoriColor = Color(0xFF565d5f),
            kategoriBg = Color(0xFF565d5f).copy(alpha = 0.1f)
        ),
        ArtikelItem(
            judul = "Kertas Bekas Jadi Barang Berkelas",
            ringkasan = "Inspirasi kerajinan tangan dari tumpukan koran lama.",
            kategori = "Tips Daur Ulang",
            kategoriColor = Primary,
            kategoriBg = Primary.copy(alpha = 0.1f)
        )
    )

    val filtered = artikelList.filter { item ->
        (selectedFilter == "Semua" || item.kategori == selectedFilter) &&
                (searchQuery.isEmpty() || item.judul.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daur", fontWeight = FontWeight.Bold, color = Primary, fontSize = 22.sp) },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifikasi", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ── Hero header ──────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text("Edukasi Hijau", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Temukan cara terbaik untuk menjaga bumi tetap bersih melalui pengelolaan sampah yang cerdas.",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            // ── Search bar ───────────────────────────────
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari artikel edukasi...", color = Color(0xFF6D7A73)) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF6D7A73)) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = OutlineVariant,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            // ── Filter chips ─────────────────────────────
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
                ) {
                    items(filterList) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) Primary else SurfaceContainer)
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(filter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) Color.White else OnSurfaceVariant)
                        }
                    }
                }
            }

            // ── Artikel cards ─────────────────────────────
            items(filtered.indices.toList()) { index ->
                val artikel = filtered[index]
                val isBookmarked = bookmarkedItems.contains(artikelList.indexOf(artikel))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 5.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainer),
                    elevation = CardDefaults.cardElevation(1.dp),
                    onClick = {}
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Gambar thumbnail placeholder
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(artikel.kategoriBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Eco, contentDescription = null, tint = artikel.kategoriColor, modifier = Modifier.size(40.dp))
                        }

                        Column(
                            modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(artikel.kategoriBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(artikel.kategori, fontSize = 11.sp, color = artikel.kategoriColor, fontWeight = FontWeight.SemiBold)
                                }
                                val realIdx = artikelList.indexOf(artikel)
                                IconButton(
                                    onClick = {
                                        bookmarkedItems = if (isBookmarked)
                                            bookmarkedItems - realIdx
                                        else
                                            bookmarkedItems + realIdx
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = "Simpan",
                                        tint = if (isBookmarked) Primary else Color(0xFF6D7A73),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Text(
                                artikel.judul,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                maxLines = 2
                            )
                            Text(
                                artikel.ringkasan,
                                fontSize = 12.sp,
                                color = OnSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}