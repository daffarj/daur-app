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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.daur.app.model.Edukasi
import com.daur.app.ui.theme.*
import com.daur.app.viewmodel.EdukasiViewModel
import com.daur.app.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdukasiLingkunganScreen(
    navController: NavController,
    vm: EdukasiViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val selectedFilter by vm.selectedFilter.collectAsState()
    val searchQuery by vm.searchQuery.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        TopAppBar(
            title = { Text("Edukasi Hijau", fontWeight = FontWeight.Bold, color = Primary, fontSize = 20.sp) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── Hero banner ──────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF004D36), Color(0xFF00694C))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Box(modifier = Modifier.size(100.dp).align(Alignment.TopEnd).offset(x = 24.dp, y = (-24).dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f)))
                    Box(modifier = Modifier.size(60.dp).align(Alignment.BottomStart).offset(x = (-12).dp, y = 12.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.06f)))
                    Column(modifier = Modifier.fillMaxWidth(0.78f)) {
                        Text("Belajar Lebih Hijau 🌿", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Temukan tips, artikel, dan panduan untuk gaya hidup ramah lingkungan.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 19.sp
                        )
                    }
                    Icon(
                        Icons.Outlined.Eco,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.size(80.dp).align(Alignment.CenterEnd)
                    )
                }
            }

            // ── Search Bar ───────────────────────────────
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { vm.setSearch(it) },
                    placeholder = { Text("Cari artikel edukasi...", color = Outline) },
                    leadingIcon  = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Outline) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { vm.setSearch("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Hapus", tint = Outline)
                            }
                        }
                    },
                    modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    shape     = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors    = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = Primary,
                        unfocusedBorderColor    = OutlineVariant,
                        focusedContainerColor   = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            // ── Filter Chips ─────────────────────────────
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                ) {
                    items(vm.filters) { filter ->
                        val isSelected = selectedFilter == filter
                        val label = vm.filterLabels[filter] ?: filter
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) Primary else SurfaceContainer)
                                .clickable { vm.setFilter(filter) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                val icon = filterIcon(filter)
                                if (icon != null)
                                    Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else OnSurfaceVariant, modifier = Modifier.size(14.dp))
                                Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) Color.White else OnSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // ── Section header ────────────────────────────
            item {
                when (val s = state) {
                    is UiState.Success -> {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${s.data.size} Artikel", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                            Text("Terbaru ↓", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Medium)
                        }
                    }
                    else -> Spacer(Modifier.height(12.dp))
                }
            }

            // ── Content ───────────────────────────────────
            when (val s = state) {
                is UiState.Loading -> item {
                    Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is UiState.Empty -> item {
                    EmptyState(
                        icon    = Icons.Outlined.SearchOff,
                        title   = "Artikel tidak ditemukan",
                        message = "Coba kata kunci atau filter kategori lain."
                    )
                }
                is UiState.Error -> item {
                    EmptyState(
                        icon    = Icons.Outlined.ErrorOutline,
                        title   = "Gagal memuat",
                        message = s.message,
                        isError = true,
                        onRetry = { vm.load() }
                    )
                }
                is UiState.Success -> {
                    // Artikel pertama → featured card (besar)
                    if (s.data.isNotEmpty()) {
                        item {
                            FeaturedArtikelCard(
                                edukasi = s.data.first(),
                                onClick = {
                                    navController.navigate("edukasi_detail/${s.data.first().id}")
                                }
                            )
                        }
                    }
                    // Sisanya → list card biasa
                    if (s.data.size > 1) {
                        items(s.data.drop(1), key = { it.id }) { edukasi ->
                            ArtikelCard(
                                edukasi = edukasi,
                                onClick = { navController.navigate("edukasi_detail/${edukasi.id}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Detail Screen ──────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdukasiDetailScreen(
    edukasiId: String,
    navController: NavController,
    vm: EdukasiViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    // Cari artikel dari state
    val edukasi = (state as? UiState.Success)?.data?.find { it.id == edukasiId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Artikel", fontWeight = FontWeight.SemiBold, color = OnSurface, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = OnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        containerColor = Background
    ) { innerPadding ->
        if (edukasi == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                // ── Thumbnail ────────────────────────────
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(kategoriGradient(edukasi.kategori)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = kategoriIcon(edukasi.kategori),
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(120.dp)
                        )
                        // Overlay badge kategori
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                (vm.filterLabels[edukasi.kategori] ?: edukasi.kategori).uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // ── Judul & Meta ─────────────────────────
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                        Text(edukasi.judul, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OnSurface, lineHeight = 30.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = Outline, modifier = Modifier.size(14.dp))
                            val tanggal = edukasi.createdAt.take(10).replace("-", "/").ifEmpty { "—" }
                            Text(tanggal, fontSize = 12.sp, color = Outline)
                            Spacer(Modifier.width(4.dp))
                            // Estimasi baca
                            val wordCount = edukasi.konten.split(" ").size
                            val menit = (wordCount / 200).coerceAtLeast(1)
                            Icon(Icons.Outlined.MenuBook, contentDescription = null, tint = Outline, modifier = Modifier.size(14.dp))
                            Text("$menit menit baca", fontSize = 12.sp, color = Outline)
                        }
                    }
                }

                // ── Divider ───────────────────────────────
                item { HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = OutlineVariant.copy(alpha = 0.3f)) }

                // ── Ringkasan ─────────────────────────────
                if (edukasi.ringkasan.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.07f)),
                            border   = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.15f)),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                                Text(edukasi.ringkasan, fontSize = 14.sp, color = OnSurface, lineHeight = 21.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                // ── Konten ────────────────────────────────
                item {
                    Text(
                        text = edukasi.konten,
                        fontSize = 15.sp,
                        color = OnSurface,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                // ── Tombol Kembali ────────────────────────
                item {
                    Spacer(Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(48.dp),
                        shape  = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Primary)
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Kembali ke Daftar", color = Primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ── Artikel Cards ──────────────────────────────────────────
@Composable
private fun FeaturedArtikelCard(edukasi: Edukasi, onClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp).clickable { onClick() },
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Thumbnail besar
            Box(
                modifier = Modifier.fillMaxWidth().height(160.dp).background(kategoriGradient(edukasi.kategori)),
                contentAlignment = Alignment.Center
            ) {
                Icon(kategoriIcon(edukasi.kategori), contentDescription = null, tint = Color.White.copy(alpha = 0.25f), modifier = Modifier.size(88.dp))
                Box(
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 5.dp)
                ) { Text("FEATURED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 0.8.sp) }
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.3f)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) { Text(vm_filterLabel(edukasi.kategori).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White) }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(edukasi.judul, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = OnSurface, lineHeight = 24.sp, maxLines = 2)
                if (edukasi.ringkasan.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(edukasi.ringkasan, fontSize = 13.sp, color = OnSurfaceVariant, maxLines = 2, lineHeight = 19.sp)
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tanggal = edukasi.createdAt.take(10).replace("-", "/").ifEmpty { "—" }
                    Text(tanggal, fontSize = 12.sp, color = Outline)
                    Box(
                        modifier = Modifier.clip(CircleShape).background(Primary).padding(horizontal = 14.dp, vertical = 6.dp)
                    ) { Text("Baca →", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White) }
                }
            }
        }
    }
}

@Composable
private fun ArtikelCard(edukasi: Edukasi, onClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp).clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        border    = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainer),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Thumbnail kecil
            Box(
                modifier = Modifier.size(88.dp).clip(RoundedCornerShape(12.dp)).background(kategoriGradient(edukasi.kategori)),
                contentAlignment = Alignment.Center
            ) {
                Icon(kategoriIcon(edukasi.kategori), contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(36.dp))
            }

            Column(modifier = Modifier.weight(1f).padding(vertical = 2.dp)) {
                // Kategori badge
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(kategoriColor(edukasi.kategori).copy(alpha = 0.1f)).padding(horizontal = 6.dp, vertical = 2.dp)
                ) { Text(vm_filterLabel(edukasi.kategori), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = kategoriColor(edukasi.kategori)) }

                Spacer(Modifier.height(4.dp))
                Text(edukasi.judul, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface, maxLines = 2, lineHeight = 20.sp)

                if (edukasi.ringkasan.isNotEmpty()) {
                    Spacer(Modifier.height(3.dp))
                    Text(edukasi.ringkasan, fontSize = 12.sp, color = OnSurfaceVariant, maxLines = 1)
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tanggal = edukasi.createdAt.take(10).replace("-", "/").ifEmpty { "—" }
                    Text(tanggal, fontSize = 11.sp, color = Outline)
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ── Helpers ────────────────────────────────────────────────
private fun kategoriGradient(kategori: String): Brush = when (kategori) {
    "tips"       -> Brush.linearGradient(listOf(Color(0xFF004D36), Color(0xFF00694C)))
    "artikel"    -> Brush.linearGradient(listOf(Color(0xFF00494B), Color(0xFF007174)))
    "video"      -> Brush.linearGradient(listOf(Color(0xFF5B0060), Color(0xFF872785)))
    "infografis" -> Brush.linearGradient(listOf(Color(0xFF5A3300), Color(0xFF855400)))
    else         -> Brush.linearGradient(listOf(Color(0xFF004D36), Color(0xFF00694C)))
}

private fun kategoriColor(kategori: String) = when (kategori) {
    "tips"       -> Primary
    "artikel"    -> Color(0xFF006367)
    "video"      -> Color(0xFF7B2082)
    "infografis" -> Secondary
    else         -> Primary
}

private fun kategoriIcon(kategori: String): ImageVector = when (kategori) {
    "tips"       -> Icons.Outlined.Lightbulb
    "artikel"    -> Icons.Outlined.Article
    "video"      -> Icons.Outlined.PlayCircle
    "infografis" -> Icons.Outlined.BarChart
    else         -> Icons.Outlined.Eco
}

private fun filterIcon(filter: String): ImageVector? = when (filter) {
    "tips"       -> Icons.Outlined.Lightbulb
    "artikel"    -> Icons.Outlined.Article
    "video"      -> Icons.Outlined.PlayCircle
    "infografis" -> Icons.Outlined.BarChart
    else         -> null
}

private fun vm_filterLabel(kategori: String) = when (kategori) {
    "tips"       -> "Tips"
    "artikel"    -> "Artikel"
    "video"      -> "Video"
    "infografis" -> "Infografis"
    else         -> kategori.replaceFirstChar { it.uppercase() }
}