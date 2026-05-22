package com.daur.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daur.app.ui.theme.*

data class RiwayatItem(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val tanggal: String,
    val judul: String,
    val berat: String,
    val poin: String,
    val poinColor: Color,
    val status: String,
    val statusColor: Color,
    val lokasi: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatSetoranScreen() {
    val filterList = listOf("Semua", "Plastik", "Kertas", "Logam")
    var selectedFilter by remember { mutableStateOf("Semua") }
    var expandedIndex by remember { mutableStateOf(-1) }

    val riwayatList = listOf(
        RiwayatItem(
            icon = Icons.Outlined.Recycling,
            iconBg = Primary.copy(alpha = 0.1f),
            iconTint = Primary,
            tanggal = "12 Okt 2023 • 14:20",
            judul = "Botol Plastik PET",
            berat = "2.5 kg",
            poin = "+150 Poin",
            poinColor = Primary,
            status = "Selesai",
            statusColor = Primary,
            lokasi = "Drop Point Kebayoran"
        ),
        RiwayatItem(
            icon = Icons.Outlined.Description,
            iconBg = Secondary.copy(alpha = 0.1f),
            iconTint = Secondary,
            tanggal = "08 Okt 2023 • 09:15",
            judul = "Kertas Karton & HVS",
            berat = "5.2 kg",
            poin = "+280 Poin",
            poinColor = Primary,
            status = "Selesai",
            statusColor = Primary,
            lokasi = "Pick-up Driver (Anto)"
        ),
        RiwayatItem(
            icon = Icons.Outlined.Inventory2,
            iconBg = Color(0xFF565d5f).copy(alpha = 0.1f),
            iconTint = Color(0xFF565d5f),
            tanggal = "Baru Saja",
            judul = "Kaleng Alumunium",
            berat = "0.8 kg",
            poin = "Memproses",
            poinColor = Secondary,
            status = "Verifikasi",
            statusColor = Secondary,
            lokasi = "Drop Point Sudirman"
        ),
        RiwayatItem(
            icon = Icons.Outlined.Recycling,
            iconBg = Primary.copy(alpha = 0.1f),
            iconTint = Primary,
            tanggal = "25 Sep 2023 • 17:05",
            judul = "Gelas Plastik",
            berat = "1.2 kg",
            poin = "+75 Poin",
            poinColor = Primary,
            status = "Selesai",
            statusColor = Primary,
            lokasi = "Drop Point Menteng"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Riwayat", fontWeight = FontWeight.Bold, color = Primary, fontSize = 20.sp)
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifikasi", tint = OnSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ── Header ──────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text("Aktivitas Setoran", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Spacer(Modifier.height(4.dp))
                    Text("Lacak kontribusi lingkungan Anda di sini.", fontSize = 14.sp, color = OnSurfaceVariant)
                }
            }

            // ── Filter chips ─────────────────────────────
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    items(filterList) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF008560) else SurfaceContainer)
                                .border(1.dp, if (isSelected) Color.Transparent else OutlineVariant, CircleShape)
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                filter,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ── Riwayat cards ────────────────────────────
            items(riwayatList.indices.toList()) { index ->
                val item = riwayatList[index]
                val isExpanded = expandedIndex == index
                val rotate by animateFloatAsState(
                    targetValue = if (isExpanded) 180f else 0f,
                    animationSpec = tween(200),
                    label = "expand_rotate"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .clickable { expandedIndex = if (isExpanded) -1 else index },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(item.iconBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(item.icon, contentDescription = null, tint = item.iconTint, modifier = Modifier.size(24.dp))
                                }
                                Column {
                                    Text(item.tanggal, fontSize = 11.sp, color = OnSurfaceVariant)
                                    Text(item.judul, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                    Text(item.berat, fontSize = 14.sp, color = Color(0xFF6D7A73))
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(item.poin, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = item.poinColor)
                                Icon(
                                    Icons.Filled.ExpandMore,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(22.dp).rotate(rotate)
                                )
                            }
                        }

                        // Expanded detail
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                                Spacer(Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    // Gambar placeholder
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(112.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SurfaceContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Outlined.Image, contentDescription = null, tint = Color(0xFF6D7A73), modifier = Modifier.size(40.dp))
                                    }
                                    // Detail info
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        DetailInfoBox(label = "STATUS", value = item.status, valueColor = item.statusColor, bgColor = item.statusColor.copy(alpha = 0.1f))
                                        DetailInfoBox(label = "LOKASI DROP", value = item.lokasi, valueColor = OnSurface, bgColor = Color(0xFFF3F4F5))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Lihat lebih banyak ───────────────────────
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedButton(
                        onClick = {},
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6D7A73)),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Lihat Lebih Banyak", color = Primary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoBox(label: String, value: String, valueColor: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(8.dp)
    ) {
        Column {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D7A73), letterSpacing = 0.5.sp)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
        }
    }
}