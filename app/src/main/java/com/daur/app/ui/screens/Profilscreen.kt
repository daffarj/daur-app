package com.daur.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daur.app.model.Profile
import com.daur.app.ui.theme.*
import com.daur.app.viewmodel.ProfilViewModel
import com.daur.app.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(
    onLogout: () -> Unit,
    vm: ProfilViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    // FIX: ganti Scaffold dengan Column biasa supaya tidak double padding
    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        TopAppBar(
            title = { Text("Profil", fontWeight = FontWeight.Bold, color = Primary, fontSize = 20.sp) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
        )
        when (val s = state) {
            is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(icon = Icons.Outlined.ErrorOutline, title = "Gagal memuat profil", message = s.message, isError = true, onRetry = { vm.load() })
            }
            is UiState.Success -> ProfilContent(profile = s.data, onLogout = {
                vm.logout(context)
                onLogout()
            })
            else -> {}
        }
    }
}


@Composable
private fun ProfilContent(profile: Profile, onLogout: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // ── Hero Avatar ──────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Primary.copy(alpha = 0.1f), Color.Transparent),
                            startY = 0f, endY = 350f
                        )
                    )
                    .padding(vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(108.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer)
                                .border(4.dp, Surface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.namaLengkap.firstOrNull()?.uppercase() ?: "?",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Filled.CameraAlt, contentDescription = "Ganti foto", tint = Color.White, modifier = Modifier.size(16.dp)) }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(profile.namaLengkap.ifEmpty { "Pengguna Daur" }, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    if (profile.noTelepon.isNotEmpty())
                        Text(profile.noTelepon, fontSize = 14.sp, color = OnSurfaceVariant)
                }
            }
        }

        // ── Stats Row ─────────────────────────────────────
        item {
            Card(
                modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                border    = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem("Total Setoran", profile.totalSetoran.toString(), Primary)
                    Box(Modifier.height(32.dp).width(1.dp).background(OutlineVariant.copy(alpha = 0.5f)))
                    StatItem("Saldo Poin", "%,d".format(profile.totalPoin), Secondary)
                    Box(Modifier.height(32.dp).width(1.dp).background(OutlineVariant.copy(alpha = 0.5f)))
                    StatItem("Level", levelLabel(profile.totalPoin), Color(0xFF006874))
                }
            }
        }

        // ── Poin progress bar ─────────────────────────────
        item {
            Card(
                modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 16.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                border    = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Progress Level", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                        Text(nextLevelLabel(profile.totalPoin), fontSize = 12.sp, color = OnSurfaceVariant)
                    }
                    Spacer(Modifier.height(10.dp))
                    val progress = levelProgress(profile.totalPoin)
                    LinearProgressIndicator(
                        progress       = { progress },
                        modifier       = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color          = Primary,
                        trackColor     = SurfaceContainer
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("${profile.totalPoin} / ${nextLevelPoin(profile.totalPoin)} poin", fontSize = 12.sp, color = OnSurfaceVariant)
                }
            }
        }

        // ── Menu Pengaturan ───────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("Pengaturan", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Primary,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SettingRow(icon = Icons.Outlined.Person, label = "Edit Profil", onClick = {})
                    SettingRow(icon = Icons.Outlined.Notifications, label = "Notifikasi", onClick = {})
                    SettingRow(icon = Icons.Outlined.Lock, label = "Ubah Password", onClick = {})
                    SettingRow(icon = Icons.Outlined.HelpOutline, label = "Bantuan & FAQ", onClick = {})
                    SettingRow(icon = Icons.Outlined.Info, label = "Tentang Aplikasi", onClick = {})
                }
                Spacer(Modifier.height(12.dp))
                // Logout
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(14.dp),
                    colors    = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.06f)),
                    border    = androidx.compose.foundation.BorderStroke(1.dp, Error.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(0.dp),
                    onClick   = onLogout
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.Logout, contentDescription = null, tint = Error, modifier = Modifier.size(22.dp))
                        Text("Keluar", fontSize = 15.sp, color = Error, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F5)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(icon, contentDescription = label, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                Text(label, fontSize = 15.sp, color = OnSurface)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Outline, modifier = Modifier.size(20.dp))
        }
    }
}

// ── Level helpers ──────────────────────────────────────────
private fun levelLabel(poin: Int) = when {
    poin < 100   -> "Pemula"
    poin < 500   -> "Hijau"
    poin < 1500  -> "Bumi"
    else         -> "Pahlawan"
}
private fun nextLevelLabel(poin: Int) = when {
    poin < 100   -> "Menuju Hijau"
    poin < 500   -> "Menuju Bumi"
    poin < 1500  -> "Menuju Pahlawan"
    else         -> "Level Maks"
}
private fun nextLevelPoin(poin: Int) = when {
    poin < 100   -> 100
    poin < 500   -> 500
    poin < 1500  -> 1500
    else         -> 1500
}
private fun levelProgress(poin: Int): Float = when {
    poin < 100   -> poin / 100f
    poin < 500   -> (poin - 100) / 400f
    poin < 1500  -> (poin - 500) / 1000f
    else         -> 1f
}