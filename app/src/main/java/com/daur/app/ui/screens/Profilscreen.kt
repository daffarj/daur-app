package com.daur.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daur.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen() {
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
            // ── Hero Profile ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Primary.copy(alpha = 0.08f), Color.Transparent),
                            startY = 0f,
                            endY = 300f
                        )
                    )
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar
                    Box {
                        Box(
                            modifier = Modifier
                                .size(112.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer)
                                .border(4.dp, Surface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Avatar",
                                tint = Primary,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = "Ganti foto", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Budi Santoso", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    Text("budi.santoso@email.com", fontSize = 14.sp, color = OnSurfaceVariant)
                }
            }

            // ── Stats Row ────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(label = "Setoran", value = "24", valueColor = Primary)
                    Box(modifier = Modifier.height(32.dp).width(1.dp).background(OutlineVariant.copy(alpha = 0.5f)))
                    StatItem(label = "Total Poin", value = "1.250", valueColor = Secondary)
                    Box(modifier = Modifier.height(32.dp).width(1.dp).background(OutlineVariant.copy(alpha = 0.5f)))
                    StatItem(label = "Penukaran", value = "8", valueColor = Primary)
                }
            }

            // ── Settings List ────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("Pengaturan Akun", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Primary, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SettingItem(icon = Icons.Outlined.Notifications, label = "Notifikasi", onClick = {})
                    SettingItem(icon = Icons.Outlined.HelpOutline, label = "Bantuan", onClick = {})
                    SettingItem(icon = Icons.Outlined.Info, label = "Tentang Aplikasi", onClick = {})
                }

                Spacer(Modifier.height(12.dp))

                // Keluar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F5)),
                    elevation = CardDefaults.cardElevation(0.dp),
                    onClick = {}
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.Logout, contentDescription = "Keluar", tint = Error)
                        Text("Keluar", fontSize = 16.sp, color = Error, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F5)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(icon, contentDescription = label, tint = OnSurfaceVariant, modifier = Modifier.size(22.dp))
                Text(label, fontSize = 16.sp, color = OnSurface)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color(0xFF6D7A73), modifier = Modifier.size(22.dp))
        }
    }
}