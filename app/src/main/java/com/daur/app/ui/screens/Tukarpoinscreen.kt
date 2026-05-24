package com.daur.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daur.app.model.Reward
import com.daur.app.ui.theme.*
import com.daur.app.viewmodel.TukarPoinViewModel
import com.daur.app.viewmodel.UiState
import com.daur.app.data.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TukarPoinScreen(vm: TukarPoinViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val selectedKategori by vm.selectedKategori.collectAsState()
    val tukarState by vm.tukarState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(tukarState) {
        when (val t = tukarState) {
            is UiState.Success -> { snackbarHostState.showSnackbar("✅ Penukaran berhasil!"); vm.resetTukar() }
            is UiState.Error   -> { snackbarHostState.showSnackbar("❌ ${t.message}"); vm.resetTukar() }
            else -> {}
        }
    }

    // FIX: Box + Column menggantikan Scaffold agar tidak double padding
    // SnackbarHost diletakkan sebagai overlay di dalam Box
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(Background)) {
            TopAppBar(
                title = { Text("Tukar Poin", fontWeight = FontWeight.Bold, color = Primary, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // ── Saldo Poin Card ──────────────────────────
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Primary)
                            .padding(20.dp)
                    ) {
                        Box(modifier = Modifier.size(128.dp).align(Alignment.TopEnd).offset(x = 32.dp, y = (-32).dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)))
                        Box(modifier = Modifier.size(80.dp).align(Alignment.BottomStart).offset(x = (-20).dp, y = 20.dp).clip(CircleShape).background(Secondary.copy(alpha = 0.15f)))
                        Column {
                            Text("Saldo Poin Anda", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                            Spacer(Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Filled.Stars, contentDescription = null, tint = Color(0xFFFCAA33), modifier = Modifier.size(30.dp))
                                Text("— Poin", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                    Text("Poin bertambah otomatis setiap setoran selesai", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // ── Filter tabs ──────────────────────────────
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        items(vm.kategoriList) { kat ->
                            val isSelected = selectedKategori == kat
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) Primary else SurfaceContainer)
                                    .clickable { vm.setKategori(kat) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(kat, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else OnSurface)
                            }
                        }
                    }
                }

                // ── Grid Hadiah ──────────────────────────────
                item {
                    when (val s = state) {
                        is UiState.Loading -> Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Primary)
                        }
                        is UiState.Empty -> EmptyState(icon = Icons.Outlined.CardGiftcard, title = "Belum ada hadiah", message = "Hadiah kategori ini sedang tidak tersedia.")
                        is UiState.Error -> EmptyState(icon = Icons.Outlined.ErrorOutline, title = "Gagal memuat", message = s.message, isError = true, onRetry = { vm.load() })
                        is UiState.Success -> {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                s.data.chunked(2).forEach { row ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        row.forEach { reward ->
                                            RewardCard(
                                                reward   = reward,
                                                modifier = Modifier.weight(1f),
                                                onTukar  = { vm.tukar(reward) },
                                                isLoading = tukarState is UiState.Loading
                                            )
                                        }
                                        if (row.size == 1) Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // PENTING: Meletakkan SnackbarHost di dalam Box agar bisa melayang di atas konten screen
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun RewardCard(reward: Reward, modifier: Modifier, onTukar: () -> Unit, isLoading: Boolean) {
    val (iconBg, iconColor) = when (reward.kategori) {
        "voucher" -> Secondary.copy(alpha = 0.1f) to Secondary
        "donasi"  -> Color(0xFF006B5B).copy(alpha = 0.1f) to Color(0xFF006B5B)
        else      -> Primary.copy(alpha = 0.1f) to Primary
    }
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        border    = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            // Thumbnail
            Box(modifier = Modifier.fillMaxWidth().height(110.dp).background(iconBg), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when (reward.kategori) {
                        "voucher" -> Icons.Outlined.LocalOffer
                        "donasi"  -> Icons.Outlined.Favorite
                        else      -> Icons.Outlined.Inventory2
                    },
                    contentDescription = null, tint = iconColor, modifier = Modifier.size(44.dp)
                )
                // Badge poin
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).clip(CircleShape).background(Secondary).padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Filled.Stars, contentDescription = null, tint = Color(0xFF2a1700), modifier = Modifier.size(11.dp))
                        Text("%,d".format(reward.poinDiperlukan), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2a1700))
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(reward.nama, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurface, maxLines = 2, lineHeight = 18.sp)
                if (reward.deskripsi.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(reward.deskripsi, fontSize = 11.sp, color = OnSurfaceVariant, maxLines = 2)
                }
                Spacer(Modifier.height(4.dp))
                // Stok
                Text("Stok: ${reward.stok}", fontSize = 11.sp, color = if (reward.stok > 5) Primary else Error)
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick  = onTukar,
                    enabled  = !isLoading && reward.stok > 0,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape    = CircleShape,
                    colors   = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                    else Text("Tukar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}