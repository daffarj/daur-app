package com.daur.app.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daur.app.model.KatalogSampah
import com.daur.app.ui.theme.*
import com.daur.app.viewmodel.SetorViewModel
import com.daur.app.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetorSampahScreen(vm: SetorViewModel = viewModel()) {
    val context       = LocalContext.current
    val katalogState  by vm.katalogState.collectAsState()
    val submitState   by vm.submitState.collectAsState()
    val selectedKatalog by vm.selectedKatalog.collectAsState()
    val berat         by vm.berat.collectAsState()
    val catatan       by vm.catatan.collectAsState()
    val fotoBitmap    by vm.fotoBitmap.collectAsState()
    val estimasiPoin  by vm.estimasiPoin.collectAsState()
    val estimasiHarga by vm.estimasiHarga.collectAsState()

    // ── Dialog pilih sumber foto ───────────────────────────
    var showFotoDialog by remember { mutableStateOf(false) }

    // ── URI temp untuk kamera ──────────────────────────────
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // ── Launcher galeri ────────────────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(context.contentResolver, it)
                )
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            }
            vm.setFoto(bitmap)
        }
    }

    // ── Launcher kamera (full resolution via FileProvider) ─
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let { uri ->
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(context.contentResolver, uri)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                vm.setFoto(bitmap)
            }
        }
    }

    // ── Helper buat URI temp kamera ────────────────────────
    fun launchCamera() {
        val cacheDir = File(context.cacheDir, "camera").also { it.mkdirs() }
        val photoFile = File.createTempFile("photo_", ".jpg", cacheDir)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        cameraImageUri = uri
        cameraLauncher.launch(uri)
    }

    // ── Permission kamera ──────────────────────────────────
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCamera()
    }

    // ── Snackbar ───────────────────────────────────────────
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(submitState) {
        when (val s = submitState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("✅ Setoran berhasil dikirim!")
                vm.resetSubmit()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar("❌ ${s.message}")
                vm.resetSubmit()
            }
            else -> {}
        }
    }

    // ── Dialog pilih sumber foto ───────────────────────────
    if (showFotoDialog) {
        AlertDialog(
            onDismissRequest = { showFotoDialog = false },
            title = { Text("Tambah Foto", fontWeight = FontWeight.SemiBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Kamera
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Primary.copy(alpha = 0.07f))
                            .clickable {
                                showFotoDialog = false
                                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = Primary)
                        Text("Ambil Foto", fontWeight = FontWeight.Medium, color = OnSurface)
                    }
                    // Galeri
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Primary.copy(alpha = 0.07f))
                            .clickable {
                                showFotoDialog = false
                                galleryLauncher.launch("image/*")
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.PhotoLibrary, contentDescription = null, tint = Primary)
                        Text("Pilih dari Galeri", fontWeight = FontWeight.Medium, color = OnSurface)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showFotoDialog = false }) {
                    Text("Batal", color = OnSurfaceVariant)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(Background)) {
            TopAppBar(
                title = {
                    Text("Setor Sampah", fontWeight = FontWeight.Bold, color = Primary, fontSize = 18.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // ── Step 1: Pilih Jenis Sampah ─────────────────
                item {
                    StepSection(number = 1, title = "Pilih Jenis Sampah") {
                        when (val s = katalogState) {
                            is UiState.Loading -> {
                                Box(
                                    Modifier.fillMaxWidth().height(80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Primary, modifier = Modifier.size(28.dp))
                                }
                            }
                            is UiState.Error -> {
                                Text(s.message, color = Error, fontSize = 13.sp)
                            }
                            is UiState.Success -> {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    items(s.data) { katalog ->
                                        KatalogChip(
                                            katalog    = katalog,
                                            isSelected = selectedKatalog?.id == katalog.id,
                                            onClick    = { vm.selectedKatalog.value = katalog }
                                        )
                                    }
                                }
                                selectedKatalog?.let { kat ->
                                    Spacer(Modifier.height(8.dp))
                                    Card(
                                        shape     = RoundedCornerShape(12.dp),
                                        colors    = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.06f)),
                                        border    = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.15f)),
                                        elevation = CardDefaults.cardElevation(0.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    kat.nama, fontSize = 14.sp,
                                                    fontWeight = FontWeight.SemiBold, color = OnSurface
                                                )
                                                if (kat.deskripsi.isNotEmpty())
                                                    Text(
                                                        kat.deskripsi, fontSize = 12.sp,
                                                        color = OnSurfaceVariant, maxLines = 2
                                                    )
                                            }
                                            Spacer(Modifier.width(12.dp))
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    "Rp %,.0f/kg".format(kat.hargaPerKg),
                                                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                                                    color = Secondary
                                                )
                                                Text(
                                                    "${kat.poinPerKg} poin/kg",
                                                    fontSize = 12.sp, color = Primary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }

                // ── Step 2: Berat Sampah ──────────────────────
                item {
                    StepSection(number = 2, title = "Berat Sampah (Estimasi)") {
                        Card(
                            shape     = RoundedCornerShape(16.dp),
                            colors    = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F5)),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick  = { vm.kurangBerat() },
                                    modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceContainer)
                                ) { Icon(Icons.Filled.Remove, contentDescription = "Kurang", tint = Primary) }

                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        "%.1f".format(berat), fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold, color = OnSurface
                                    )
                                    Text(
                                        "kg", fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                                        color = OnSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }

                                IconButton(
                                    onClick  = { vm.tambahBerat() },
                                    modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceContainer)
                                ) { Icon(Icons.Filled.Add, contentDescription = "Tambah", tint = Primary) }
                            }
                        }
                    }
                }

                // ── Step 3: Foto Sampah ───────────────────────
                item {
                    StepSection(number = 3, title = "Foto Sampah") {
                        if (fotoBitmap != null) {
                            // ── Preview foto terpilih ──────────
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                Image(
                                    bitmap = fotoBitmap!!.asImageBitmap(),
                                    contentDescription = "Foto sampah",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                // Tombol ganti & hapus
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .clickable { showFotoDialog = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.Edit, contentDescription = "Ganti",
                                            tint = Color.White, modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red.copy(alpha = 0.7f))
                                            .clickable { vm.hapusFoto() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.Delete, contentDescription = "Hapus",
                                            tint = Color.White, modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            // ── Placeholder belum ada foto ─────
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .border(2.dp, OutlineVariant, RoundedCornerShape(16.dp))
                                    .clickable { showFotoDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.AddAPhoto, contentDescription = null,
                                        tint = Primary, modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        "Ketuk untuk ambil foto atau pilih dari galeri",
                                        fontSize = 13.sp, color = OnSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Step 4: Catatan ───────────────────────────
                item {
                    StepSection(number = 4, title = "Catatan (Opsional)") {
                        OutlinedTextField(
                            value = catatan,
                            onValueChange = { vm.catatan.value = it },
                            placeholder = {
                                Text(
                                    "Tambahkan keterangan kondisi sampah, lokasi penjemputan, dll.",
                                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            minLines = 3,
                            maxLines = 5,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction      = ImeAction.Default
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = Primary,
                                unfocusedBorderColor = OutlineVariant,
                                cursorColor          = Primary
                            )
                        )
                    }
                }

                // ── Estimasi Poin & Harga ─────────────────────
                item {
                    Card(
                        shape     = RoundedCornerShape(16.dp),
                        colors    = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.08f)),
                        border    = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.2f)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Poin
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.Stars, contentDescription = null, tint = Primary)
                                    Text(
                                        "Estimasi Poin", fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Primary)
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        "%,d Pts".format(estimasiPoin),
                                        fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White
                                    )
                                }
                            }
                            // Harga
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.Payments, contentDescription = null,
                                        tint = Secondary
                                    )
                                    Text(
                                        "Estimasi Nilai", fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant
                                    )
                                }
                                Text(
                                    "Rp %,.0f".format(estimasiHarga),
                                    fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Secondary
                                )
                            }
                        }
                    }
                }

                // ── Tombol Setor ──────────────────────────────
                item {
                    val isLoading  = submitState is UiState.Loading
                    val isDisabled = katalogState !is UiState.Success || selectedKatalog == null

                    Button(
                        onClick  = { vm.setor() },
                        enabled  = !isLoading && !isDisabled,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape    = CircleShape,
                        colors   = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Setor Sekarang", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ── Katalog Chip ───────────────────────────────────────────
@Composable
private fun KatalogChip(katalog: KatalogSampah, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) Primary else Color.White,
        animationSpec = tween(200), label = "chip_bg"
    )
    val textColor by animateColorAsState(
        targetValue   = if (isSelected) Color.White else OnSurfaceVariant,
        animationSpec = tween(200), label = "chip_text"
    )
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, if (isSelected) Color.Transparent else OutlineVariant, CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = if (katalog.nama.length > 15) katalog.nama.take(15) + "…" else katalog.nama,
            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textColor
        )
    }
}

// ── Step Section ───────────────────────────────────────────
@Composable
private fun StepSection(number: Int, title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text(number.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
        }
        Column { content() }
    }
}