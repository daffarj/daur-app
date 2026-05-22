package com.daur.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daur.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SetorSampahScreen() {
    val wasteTypes = listOf("Plastik", "Kertas", "Logam", "Kaca", "Organik")
    var selectedType by remember { mutableStateOf("Plastik") }
    var weight by remember { mutableStateOf(1.0f) }
    val estimasiPoin = (weight * 1500).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Setor Sampah",
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = OnSurface)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Info, contentDescription = "Info", tint = Primary)
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // ── Step 1: Pilih Jenis Sampah ──────────────────
            StepSection(number = 1, title = "Pilih Jenis Sampah") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    wasteTypes.forEach { type ->
                        val isSelected = selectedType == type
                        val bgColor by animateColorAsState(
                            targetValue = if (isSelected) Color(0xFF008560) else Color.White,
                            animationSpec = tween(200), label = "chip_bg"
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else OnSurfaceVariant,
                            animationSpec = tween(200), label = "chip_text"
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(bgColor)
                                .border(1.dp, if (isSelected) Color.Transparent else OutlineVariant, CircleShape)
                                .clickable { selectedType = type }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(type, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                        }
                    }
                }
            }

            // ── Step 2: Berat Sampah ────────────────────────
            StepSection(number = 2, title = "Berat Sampah (Estimasi)") {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F5)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tombol kurang
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer)
                                .clickable {
                                    if (weight > 0.5f) weight = (weight - 0.5f).coerceAtLeast(0.5f)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = "Kurang", tint = Primary)
                        }

                        // Berat display
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "%.1f".format(weight),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Text(
                                text = "kg",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        // Tombol tambah
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer)
                                .clickable { weight += 0.5f },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Tambah", tint = Primary)
                        }
                    }
                }
            }

            // ── Step 3: Foto Sampah ─────────────────────────
            StepSection(number = 3, title = "Foto Sampah") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(2.dp, OutlineVariant, RoundedCornerShape(16.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = Outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Ambil foto sampah yang ingin disetor\nuntuk verifikasi cepat.",
                            fontSize = 13.sp,
                            color = OnSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }

            // ── Preview estimasi poin ───────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.08f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.2f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Stars, contentDescription = null, tint = Primary)
                        Text("Estimasi Poin", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF008560))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "%,d Pts".format(estimasiPoin),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // ── Tombol setor ────────────────────────────────
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Setor Sekarang", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StepSection(number: Int, title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text(number.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
        }
        content()
    }
}