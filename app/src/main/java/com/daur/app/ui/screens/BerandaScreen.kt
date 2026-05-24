package com.daur.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daur.app.model.Profile
import com.daur.app.model.Setoran
import com.daur.app.ui.theme.*
import com.daur.app.viewmodel.BerandaState
import com.daur.app.viewmodel.BerandaUiData
import com.daur.app.viewmodel.BerandaViewModel

@Composable
fun BerandaScreen(
    onSetor: () -> Unit = {},
    onTukarPoin: () -> Unit = {},
    onLihatRiwayat: () -> Unit = {},
    onProfile: () -> Unit = {},           // ← navigasi ke profil via avatar
    vm: BerandaViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        when (val s = state) {
            is BerandaState.Loading -> item {
                Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }

            is BerandaState.Error -> item {
                EmptyState(
                    icon    = Icons.Outlined.ErrorOutline,
                    title   = "Gagal memuat",
                    message = s.message,
                    isError = true,
                    onRetry = { vm.load() }
                )
            }

            is BerandaState.Success -> {
                val data = s.data

                // ── Greeting + Avatar ──────────────────
                item {
                    GreetingSection(
                        profile   = data.profile,
                        onRefresh = { vm.load() },
                        onProfile = onProfile          // ← teruskan ke avatar
                    )
                }

                // ── Saldo Poin Card ────────────────────
                item { SaldoPoinCard(profile = data.profile) }

                // ── Quick Actions ──────────────────────
                item {
                    QuickActionSection(
                        onSetor     = onSetor,
                        onTukarPoin = onTukarPoin
                    )
                }

                // ── Target Progress ────────────────────
                item {
                    TargetProgressCard(
                        beratSaatIni = data.totalBeratMingguIni,
                        targetBerat  = data.targetBeratMinggu
                    )
                }

                // ── Aktivitas Terbaru ──────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Aktivitas Terbaru", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                        TextButton(onClick = onLihatRiwayat) {
                            Text("Lihat Semua", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                        }
                    }
                }

                if (data.aktivitasTerbaru.isEmpty()) {
                    item {
                        Card(
                            modifier  = Modifier.fillMaxWidth(),
                            shape     = RoundedCornerShape(16.dp),
                            colors    = CardDefaults.cardColors(containerColor = Surface),
                            elevation = CardDefaults.cardElevation(0.dp),
                            border    = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.Inbox, contentDescription = null,
                                        tint = OnSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Text("Belum ada aktivitas", fontSize = 14.sp, color = OnSurfaceVariant)
                                    TextButton(onClick = onSetor) {
                                        Text("Mulai Setor Sekarang →", color = Primary, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    items(data.aktivitasTerbaru, key = { it.id }) { setoran ->
                        AktivitasItem(setoran = setoran)
                    }
                }

                // ── Eco Tip ────────────────────────────
                item { EcoTipCard() }

                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

// ── Greeting + Avatar ──────────────────────────────────────
@Composable
private fun GreetingSection(
    profile: Profile,
    onRefresh: () -> Unit,
    onProfile: () -> Unit
) {
    val namaDepan = profile.namaLengkap.split(" ").firstOrNull()
        ?.ifEmpty { "Pengguna" } ?: "Pengguna"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Halo, $namaDepan! 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Text(
                text = "Sudahkah kamu mendaur ulang hari ini?",
                fontSize = 14.sp,
                color = OnSurfaceVariant
            )
        }

        // Avatar — klik → profil
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(elevation = 4.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Primary, Color(0xFF004D38))
                    )
                )
                .border(2.dp, PrimaryContainer, CircleShape)
                .clickable { onProfile() },
            contentAlignment = Alignment.Center
        ) {
            if (profile.namaLengkap.isNotEmpty()) {
                Text(
                    text = profile.namaLengkap.first().uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            } else {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Profil",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

// ── Saldo Poin Card ────────────────────────────────────────
@Composable
private fun SaldoPoinCard(profile: Profile) {
    val nilaiRupiah = profile.totalPoin * 10

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(colors = listOf(Primary, Color(0xFF004D38))))
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Outlined.AccountBalanceWallet, contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(20.dp)
                )
                Text(
                    "Saldo Poin", fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "%,d".format(profile.totalPoin),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Poin",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // ── Icon poin — daun eco ───────────────
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFB8F0D8))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Eco, contentDescription = null,
                        tint = Color(0xFF006B3C),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Setara dengan Rp%,d".format(nilaiRupiah),
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.Recycling, contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${profile.totalSetoran} setoran",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// ── Quick Actions ──────────────────────────────────────────
@Composable
private fun QuickActionSection(onSetor: () -> Unit, onTukarPoin: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        QuickActionButton(
            modifier    = Modifier.weight(1f),
            icon        = Icons.Outlined.Recycling,
            iconBgColor = Primary.copy(alpha = 0.1f),
            iconTint    = Primary,
            label       = "Setor Sampah",
            onClick     = onSetor
        )
        QuickActionButton(
            modifier    = Modifier.weight(1f),
            icon        = Icons.Outlined.Redeem,
            iconBgColor = Secondary.copy(alpha = 0.1f),
            iconTint    = Secondary,
            label       = "Tukar Poin",
            onClick     = onTukarPoin
        )
    }
}

@Composable
private fun QuickActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier  = modifier.clickable { onClick() },
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = BorderStroke(1.dp, OutlineVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(28.dp))
            }
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
        }
    }
}

// ── Target Progress ────────────────────────────────────────
@Composable
private fun TargetProgressCard(beratSaatIni: Double, targetBerat: Double) {
    val persen = if (targetBerat > 0) (beratSaatIni / targetBerat).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = persen,
        animationSpec = tween(1000),
        label = "progress"
    )
    val persenLabel = (persen * 100).toInt()

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F5)),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Target Setoran Mingguan",
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface
                )
                Text("$persenLabel%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Primary)
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Primary.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(Primary)
                )
            }
            Spacer(Modifier.height(8.dp))
            val sisaBerat = (targetBerat - beratSaatIni).coerceAtLeast(0.0)
            Text(
                text = if (sisaBerat > 0)
                    "Tinggal %.1f kg lagi untuk mencapai target!".format(sisaBerat)
                else
                    "🎉 Target minggu ini tercapai!",
                fontSize = 11.sp,
                color = if (sisaBerat > 0) OnSurfaceVariant else Primary,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

// ── Aktivitas Item ─────────────────────────────────────────
@Composable
private fun AktivitasItem(setoran: Setoran) {
    val (statusBg, statusColor, poinColor) = when (setoran.status) {
        "selesai"  -> Triple(Primary.copy(alpha = 0.1f), Primary, Primary)
        "diproses" -> Triple(Secondary.copy(alpha = 0.1f), Secondary, Secondary)
        "ditolak"  -> Triple(Error.copy(alpha = 0.1f), Error, Error)
        else       -> Triple(SurfaceContainer, OnSurfaceVariant, OnSurfaceVariant)
    }
    val statusLabel = when (setoran.status) {
        "selesai"  -> "Selesai"
        "diproses" -> "Diproses"
        "ditolak"  -> "Ditolak"
        else       -> "Menunggu"
    }
    val icon = when (setoran.status) {
        "selesai" -> Icons.Outlined.CheckCircle
        "ditolak" -> Icons.Outlined.Cancel
        else      -> Icons.Outlined.Recycling
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = setoran.kodeSetoran.ifEmpty { "Setoran" },
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface
                )
                Text(
                    text = buildString {
                        val tgl = setoran.createdAt.take(10).replace("-", "/")
                        if (tgl.isNotEmpty() && tgl != "//") append("$tgl • ")
                        append("%.1f kg".format(setoran.totalBerat))
                    },
                    fontSize = 11.sp, color = OnSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                if (setoran.totalPoin > 0) {
                    Text(
                        text = "+${setoran.totalPoin} Pts",
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = poinColor
                    )
                } else {
                    Text("— Pts", fontSize = 14.sp, color = OnSurfaceVariant)
                }
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(statusLabel, fontSize = 10.sp, color = statusColor, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// ── Eco Tip Card ───────────────────────────────────────────
@Composable
private fun EcoTipCard() {
    val tips = listOf(
        "Bilas botol plastik dari sisa minuman untuk mempermudah proses daur ulang.",
        "Pisahkan sampah organik dan anorganik dari rumah untuk hasil daur ulang lebih baik.",
        "Gunakan tas belanja kain untuk mengurangi sampah plastik sekali pakai.",
        "Kertas bekas satu sisi masih bisa digunakan untuk catatan atau print draft."
    )
    val tip = remember { tips.random() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .drawBehind {
                drawLine(
                    color = Primary,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 12.dp.toPx()
                )
            }
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Tips Hijau", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
            Text(tip, fontSize = 14.sp, color = OnSurfaceVariant)
        }
    }
}