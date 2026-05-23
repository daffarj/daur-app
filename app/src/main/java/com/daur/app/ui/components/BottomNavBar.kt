package com.daur.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daur.app.ui.theme.*

data class BottomNavItem(
    val route: String,
    val label: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
)

// Profil dihapus dari nav — diakses via avatar di Beranda
val bottomNavItems = listOf(
    BottomNavItem("beranda", "Beranda", Icons.Filled.Home,     Icons.Outlined.Home),
    BottomNavItem("riwayat", "Riwayat", Icons.Filled.History,  Icons.Outlined.History),
    BottomNavItem("setor",   "Setor",   Icons.Filled.AddCircle, Icons.Outlined.AddCircle), // tengah — FAB
    BottomNavItem("hadiah",  "Hadiah",  Icons.Filled.Redeem,   Icons.Outlined.Redeem),
    BottomNavItem("edukasi", "Edukasi", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
)

@Composable
fun BottomNavBar(currentRoute: String, onItemClick: (String) -> Unit) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        color           = Surface,
        shadowElevation = 8.dp,
        shape           = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                if (item.route == "setor") {
                    // ── FAB Setor di tengah ────────────────
                    SetorFabButton(
                        isSelected = currentRoute == item.route,
                        onClick    = { onItemClick(item.route) }
                    )
                } else {
                    NavItemView(
                        item       = item,
                        isSelected = currentRoute == item.route,
                        onClick    = { onItemClick(item.route) }
                    )
                }
            }
        }
    }
}

// ── FAB Setor ──────────────────────────────────────────────
@Composable
private fun SetorFabButton(isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick           = onClick
            )
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .offset(y = (-12).dp)  // naik sedikit ke atas
                .shadow(
                    elevation    = 8.dp,
                    shape        = CircleShape,
                    ambientColor = Primary.copy(alpha = 0.3f),
                    spotColor    = Primary.copy(alpha = 0.5f)
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Primary, Color(0xFF004D38))
                    )
                )
                .clickable(
                    indication        = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick           = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Filled.Add,
                contentDescription = "Setor Sampah",
                tint               = Color.White,
                modifier           = Modifier.size(28.dp)
            )
        }
        // Label di bawah FAB (offset sama agar tetap aligned)
        Text(
            text       = "Setor",
            fontSize   = 9.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color      = if (isSelected) Primary else OnSurfaceVariant,
            modifier   = Modifier.offset(y = (-8).dp)
        )
    }
}

// ── Nav item biasa ─────────────────────────────────────────
@Composable
private fun NavItemView(item: BottomNavItem, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) PrimaryContainer else Color.Transparent,
        animationSpec = tween(200), label = "navBg"
    )
    val contentColor by animateColorAsState(
        targetValue   = if (isSelected) Primary else OnSurfaceVariant,
        animationSpec = tween(200), label = "navContent"
    )
    val hPadding by animateDpAsState(
        targetValue   = if (isSelected) 12.dp else 0.dp,
        animationSpec = tween(200), label = "navPad"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick           = onClick
            )
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(bgColor)
                .padding(horizontal = hPadding, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = if (isSelected) item.iconSelected else item.iconUnselected,
                contentDescription = item.label,
                tint               = contentColor,
                modifier           = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text       = item.label,
            fontSize   = 9.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color      = contentColor
        )
    }
}