package com.daur.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daur.app.ui.theme.*

data class HadiahItem(
    val nama: String,
    val deskripsi: String,
    val poin: String,
    val kategori: String,
    val iconBg: Color,
    val iconColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TukarPoinScreen() {
    val kategoriList = listOf("Semua Hadiah", "Voucher", "Pulsa", "Sembako")
    var selectedKategori by remember { mutableStateOf("Semua Hadiah") }

    val hadiahList = listOf(
        HadiahItem("Pulsa All Operator Rp50.000", "Top up saldo pulsa instan untuk semua provider seluler Indonesia.", "5.000 Pts", "Pulsa", Color(0xFF00694C).copy(0.1f), Primary),
        HadiahItem("Voucher Indomaret Rp100rb", "Voucher belanja digital yang dapat digunakan di seluruh gerai Indomaret.", "10.000 Pts", "Voucher", Color(0xFF855400).copy(0.1f), Secondary),
        HadiahItem("Paket Sembako Berkah", "Terdiri dari 2kg beras premium, 1L minyak goreng, dan 1kg gula pasir.", "7.500 Pts", "Sembako", Color(0xFF00694C).copy(0.1f), Primary),
        HadiahItem("Saldo GoPay Rp25.000", "Saldo instan yang akan dikirim langsung ke nomor GoPay terdaftar Anda.", "2.500 Pts", "Voucher", Color(0xFF855400).copy(0.1f), Secondary),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Daur", fontWeight = FontWeight.Bold, color = Primary, fontSize = 22.sp)
                },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Saldo Poin Card ──────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF008560))
                    .padding(20.dp)
            ) {
                // Dekorasi
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 32.dp, y = (-32).dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.2f))
                )
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-16).dp, y = 16.dp)
                        .clip(CircleShape)
                        .background(Secondary.copy(alpha = 0.1f))
                )
                Column {
                    Text("Saldo Poin Anda", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.9f))
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Stars, contentDescription = null, tint = Color(0xFFFCAA33), modifier = Modifier.size(32.dp))
                        Text("12.450 Poin", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text("Tukarkan poinmu sebelum 31 Des 2023", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }

            // ── Filter tabs ──────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(kategoriList) { kat ->
                    val isSelected = selectedKategori == kat
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) Primary else SurfaceContainer)
                            .clickable { selectedKategori = kat }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(kat, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) Color.White else OnSurface)
                    }
                }
            }

            // ── Grid hadiah ───────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val filteredList = if (selectedKategori == "Semua Hadiah") hadiahList
                else hadiahList.filter { it.kategori == selectedKategori }

                filteredList.chunked(2).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowItems.forEach { hadiah ->
                            HadiahCard(item = hadiah, modifier = Modifier.weight(1f))
                        }
                        if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HadiahCard(item: HadiahItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            // Gambar area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(item.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CardGiftcard,
                    contentDescription = null,
                    tint = item.iconColor,
                    modifier = Modifier.size(48.dp)
                )
                // Badge poin
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Secondary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Filled.Stars, contentDescription = null, tint = Color(0xFF2a1700), modifier = Modifier.size(12.dp))
                        Text(item.poin, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2a1700))
                    }
                }
            }

            // Konten
            Column(modifier = Modifier.padding(12.dp)) {
                Text(item.nama, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text(item.deskripsi, fontSize = 12.sp, color = OnSurfaceVariant, maxLines = 2)
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Tukar", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}