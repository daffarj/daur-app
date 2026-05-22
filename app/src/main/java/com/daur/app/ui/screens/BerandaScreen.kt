package com.daur.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daur.app.ui.theme.*

@Composable
fun BerandaScreen(
    onSetor: () -> Unit = {},
    onTukarPoin: () -> Unit = {},
    onLihatRiwayat: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        GreetingSection()
        Spacer(Modifier.height(16.dp))
        SaldoPoinCard()
        Spacer(Modifier.height(24.dp))
        QuickActionSection(onSetor = onSetor, onTukarPoin = onTukarPoin)
        Spacer(Modifier.height(24.dp))
        TargetProgressCard()
        Spacer(Modifier.height(24.dp))
        AktivitasTerbaruSection(onLihatSemua = onLihatRiwayat)
        Spacer(Modifier.height(24.dp))
        EcoTipCard()
        Spacer(Modifier.height(24.dp))
    }
}

// ── Greeting ───────────────────────────────────────────────
@Composable
private fun GreetingSection() {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text       = "Halo, Rizky!",
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = OnSurface
            )
            Text(
                text     = "Sudahkah kamu mendaur ulang hari ini?",
                fontSize = 14.sp,
                color    = OnSurfaceVariant
            )
        }
        Box(
            modifier         = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PrimaryContainer)
                .border(2.dp, Primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Filled.Person,
                contentDescription = "Avatar",
                tint               = Primary,
                modifier           = Modifier.size(28.dp)
            )
        }
    }
}

// ── Saldo Poin Card ────────────────────────────────────────
@Composable
private fun SaldoPoinCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(colors = listOf(Primary, Color(0xFF004D38)))
            )
            .padding(24.dp)
    ) {
        // Dekorasi
        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )

        Column {
            // Label
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector        = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint               = Color.White.copy(alpha = 0.9f),
                    modifier           = Modifier.size(20.dp)
                )
                Text(
                    text       = "Saldo Poin",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color.White.copy(alpha = 0.9f)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Nilai poin
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment     = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text       = "12.450",
                        fontSize   = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                    Text(
                        text       = "Poin",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White.copy(alpha = 0.7f),
                        modifier   = Modifier.padding(bottom = 6.dp)
                    )
                }
                Box(
                    modifier         = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFCAA33))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Filled.MonetizationOn,
                        contentDescription = null,
                        tint               = Color(0xFF6B4200),
                        modifier           = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(Modifier.height(12.dp))

            // Rupiah
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text     = "Setara dengan Rp124.500",
                    fontSize = 12.sp,
                    color    = Color.White.copy(alpha = 0.8f)
                )
                Icon(
                    imageVector        = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint               = Color.White.copy(alpha = 0.8f),
                    modifier           = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ── Quick Actions ──────────────────────────────────────────
@Composable
private fun QuickActionSection(onSetor: () -> Unit, onTukarPoin: () -> Unit) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
            modifier            = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = label,
                    tint               = iconTint,
                    modifier           = Modifier.size(28.dp)
                )
            }
            Text(
                text       = label,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = OnSurface
            )
        }
    }
}

// ── Target Progress ────────────────────────────────────────
@Composable
private fun TargetProgressCard() {
    val animatedProgress by animateFloatAsState(
        targetValue   = 0.85f,
        animationSpec = tween(1000),
        label         = "progress"
    )

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F5)),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Target Setoran Mingguan",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = OnSurface
                )
                Text(
                    text       = "85%",
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Primary
                )
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

            Text(
                text      = "Tinggal 2.5kg lagi untuk mencapai target!",
                fontSize  = 11.sp,
                color     = OnSurfaceVariant,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

// ── Aktivitas Terbaru ──────────────────────────────────────
@Composable
private fun AktivitasTerbaruSection(onLihatSemua: () -> Unit) {
    Column {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = "Aktivitas Terbaru",
                fontSize   = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color      = OnSurface
            )
            TextButton(onClick = onLihatSemua) {
                Text(
                    text       = "Lihat Semua",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Primary
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AktivitasItem(
                icon        = Icons.Outlined.DeleteSweep,
                iconTint    = Primary,
                title       = "Setoran Plastik PET",
                subtitle    = "Kemarin • 2.5 kg",
                poin        = "+500 Poin",
                poinColor   = Primary,
                status      = "Selesai",
                statusBg    = Primary.copy(alpha = 0.1f),
                statusColor = Primary
            )
            AktivitasItem(
                icon        = Icons.Outlined.ShoppingCartCheckout,
                iconTint    = Secondary,
                title       = "Voucher Belanja Indomaret",
                subtitle    = "12 Okt 2023",
                poin        = "-5.000 Poin",
                poinColor   = Error,
                status      = "Diproses",
                statusBg    = Color(0xFFE1E3E4),
                statusColor = OnSurfaceVariant
            )
            AktivitasItem(
                icon        = Icons.Outlined.Inventory2,
                iconTint    = Primary,
                title       = "Setoran Kertas & Karton",
                subtitle    = "10 Okt 2023 • 1.2 kg",
                poin        = "+240 Poin",
                poinColor   = Primary,
                status      = "Selesai",
                statusBg    = Primary.copy(alpha = 0.1f),
                statusColor = Primary
            )
        }
    }
}

@Composable
private fun AktivitasItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    poin: String,
    poinColor: Color,
    status: String,
    statusBg: Color,
    statusColor: Color
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier              = Modifier.padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE7E8E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = iconTint,
                    modifier           = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = title,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = OnSurface
                )
                Text(
                    text     = subtitle,
                    fontSize = 11.sp,
                    color    = OnSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text       = poin,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = poinColor
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text       = status,
                        fontSize   = 10.sp,
                        color      = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ── Eco Tip Card ───────────────────────────────────────────
@Composable
private fun EcoTipCard() {
    val borderColor = Primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .drawBehind {
                // Border kiri 4dp
                drawLine(
                    color       = borderColor,
                    start       = Offset(0f, 0f),
                    end         = Offset(0f, size.height),
                    strokeWidth = 12.dp.toPx()
                )
            }
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector        = Icons.Filled.Lightbulb,
            contentDescription = null,
            tint               = Primary,
            modifier           = Modifier.size(24.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text       = "Tips Hijau",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = OnSurface
            )
            Text(
                text     = "Bilas botol plastik dari sisa minuman untuk mempermudah proses daur ulang.",
                fontSize = 14.sp,
                color    = OnSurfaceVariant
            )
        }
    }
}
