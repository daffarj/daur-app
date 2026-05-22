package com.daur.app.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daur.app.ui.theme.*

data class KatalogItem(
    val nama: String,
    val kategori: String,
    val harga: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val kategoriBg: Color,
    val kategoriText: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KatalogSampahScreen() {
    val filterList = listOf("Semua", "Plastik", "Kertas", "Logam", "Elektronik", "Kaca")
    var selectedFilter by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }

    val katalogList = listOf(
        KatalogItem("Botol Plastik PET", "Plastik", "Rp 2.500 / kg", Icons.Outlined.Inventory2, Primary.copy(0.1f), Primary, Color(0xFF68dbae).copy(0.3f), Color(0xFF00513a)),
        KatalogItem("Kardus Bekas", "Kertas", "Rp 1.800 / kg", Icons.Outlined.AutoStories, Secondary.copy(0.1f), Secondary, Color(0xFFffddb7).copy(0.5f), Color(0xFF653e00)),
        KatalogItem("Kaleng Alumunium", "Logam", "Rp 12.000 / kg", Icons.Outlined.Kitchen, Color(0xFF565d5f).copy(0.1f), Color(0xFF565d5f), Color(0xFFdde4e6).copy(0.5f), Color(0xFF41484a)),
        KatalogItem("Kertas Koran", "Kertas", "Rp 1.200 / kg", Icons.Outlined.Newspaper, Primary.copy(0.1f), Primary, Color(0xFFffddb7).copy(0.5f), Color(0xFF653e00)),
        KatalogItem("E-Waste (Kabel)", "Elektronik", "Rp 5.000 / kg", Icons.Outlined.ElectricBolt, Error.copy(0.1f), Error, Color(0xFFffdad6).copy(0.5f), Color(0xFF93000a)),
    )

    val filtered = katalogList.filter { item ->
        (selectedFilter == "Semua" || item.kategori == selectedFilter) &&
                (searchQuery.isEmpty() || item.nama.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daur", fontWeight = FontWeight.Bold, color = Primary, fontSize = 22.sp) },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifikasi", tint = OnSurface)
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
            // ── Search bar ───────────────────────────────
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari jenis sampah...", color = Color(0xFF6D7A73)) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF6D7A73)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF3F4F5),
                        unfocusedContainerColor = Color(0xFFF3F4F5)
                    )
                )
            }

            // ── Filter chips ─────────────────────────────
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
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

            // ── Title ────────────────────────────────────
            item {
                Text(
                    "Katalog Sampah",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            // ── Katalog cards ─────────────────────────────
            items(filtered) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 5.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainer),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(item.iconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(item.icon, contentDescription = null, tint = item.iconTint, modifier = Modifier.size(28.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(item.nama, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(item.kategoriBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(item.kategori, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = item.kategoriText)
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Row {
                                Text(item.harga.substringBefore("/"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                                Text("/ kg", fontSize = 14.sp, color = OnSurfaceVariant)
                            }
                        }

                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color(0xFF6D7A73))
                    }
                }
            }

            // ── Info banner ───────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFFCAA33))
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                    Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                        Text("Bantu Jaga Lingkungan", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6b4200))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Setiap sampah yang kamu setor akan diproses secara profesional untuk masa depan bumi yang lebih hijau.",
                            fontSize = 13.sp,
                            color = Color(0xFF6b4200).copy(alpha = 0.9f)
                        )
                    }
                    Icon(
                        Icons.Outlined.Eco,
                        contentDescription = null,
                        tint = Color(0xFF6b4200).copy(alpha = 0.2f),
                        modifier = Modifier.size(80.dp).align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}