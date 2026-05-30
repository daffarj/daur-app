package com.daur.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daur.app.model.UserVoucher
import com.daur.app.ui.theme.*
import com.daur.app.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherDetailSheet(
    userVoucher: UserVoucher,
    onDismiss: () -> Unit,
    onGunakan: () -> Unit,
    gunakanState: UiState<Unit>?
) {
    val voucher = userVoucher.voucher ?: return
    val isLoading = gunakanState is UiState.Loading

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header dengan tombol close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Detail Voucher", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                IconButton(onClick = onDismiss, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = OnSurface)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card dengan icon voucher
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Secondary.copy(alpha = 0.1f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalOffer,
                    contentDescription = null,
                    tint = Secondary,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nama voucher
            Text(
                voucher.nama,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Deskripsi
            if (voucher.deskripsi.isNotEmpty()) {
                Text(
                    voucher.deskripsi,
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Info Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainer)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow("Tipe Diskon", voucher.tipeDiskon.replaceFirstChar { it.uppercase() })
                DetailRow(
                    "Nilai Diskon",
                    if (voucher.tipeDiskon.equals("persen", ignoreCase = true)) {
                        "${voucher.nilaiDiskon.toInt()}%"
                    } else {
                        "Rp ${String.format("%.0f", voucher.nilaiDiskon)}"
                    }
                )
                DetailRow("Kode", voucher.kode)
                DetailRow("Status", userVoucher.status.replace("_", " ").replaceFirstChar { it.uppercase() })
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Gunakan
            Button(
                onClick = onGunakan,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gunakan & Hapus Voucher", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = OnSurfaceVariant)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
    }
}
