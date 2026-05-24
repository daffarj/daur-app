package com.daur.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.daur.app.ui.components.BottomNavBar
import com.daur.app.ui.components.bottomNavItems
import com.daur.app.viewmodel.*

private val bottomNavRoutes = bottomNavItems.map { it.route }

@Composable
fun MainScreen(onLogout: () -> Unit = {}) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "beranda"
    val showBottomBar = bottomNavRoutes.any { currentRoute.startsWith(it) }

    // ── Hoist semua ViewModel ke sini ─────────────────────
    // Supaya bisa dipanggil reload saat tap nav yang sama
    val berandaVm: BerandaViewModel  = viewModel()
    val riwayatVm: RiwayatViewModel  = viewModel()
    val setorVm:   SetorViewModel    = viewModel()
    val hadiahVm:  TukarPoinViewModel = viewModel()
    val edukasiVm: EdukasiViewModel  = viewModel()

    Scaffold(
        modifier  = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onItemClick  = { route ->
                        if (route == currentRoute) {
                            // ── Tap tab yang sedang aktif → reload ─
                            when (route) {
                                "beranda" -> berandaVm.load()
                                "riwayat" -> riwayatVm.load()
                                "setor"   -> setorVm.loadKatalog()
                                "hadiah"  -> hadiahVm.load()
                                "edukasi" -> edukasiVm.load()
                            }
                        } else {
                            // ── Pindah tab ─────────────────────────
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = "beranda",
            modifier         = Modifier.padding(innerPadding)
        ) {
            // ── Beranda ───────────────────────────────────
            composable("beranda") {
                BerandaScreen(
                    onSetor        = { navController.navigate("setor") },
                    onTukarPoin    = { navController.navigate("hadiah") },
                    onLihatRiwayat = { navController.navigate("riwayat") },
                    onProfile      = { navController.navigate("profil") },
                    vm             = berandaVm   // ← pakai VM yang di-hoist
                )
            }

            // ── Setor Sampah ──────────────────────────────
            composable("setor") {
                SetorSampahScreen(vm = setorVm)
            }

            // ── Riwayat Setoran ───────────────────────────
            composable("riwayat") {
                RiwayatSetoranScreen(vm = riwayatVm)
            }

            // ── Tukar Poin ────────────────────────────────
            composable("hadiah") {
                TukarPoinScreen(vm = hadiahVm)
            }

            // ── Katalog Sampah ────────────────────────────
            composable("katalog") {
                KatalogSampahScreen()
            }

            // ── Edukasi (list) ────────────────────────────
            composable("edukasi") {
                EdukasiLingkunganScreen(
                    navController = navController,
                    vm            = edukasiVm    // ← pakai VM yang di-hoist
                )
            }

            // ── Edukasi Detail ────────────────────────────
            composable(
                route     = "edukasi_detail/{edukasiId}",
                arguments = listOf(navArgument("edukasiId") { type = NavType.StringType })
            ) { backStackEntry ->
                val edukasiId = backStackEntry.arguments?.getString("edukasiId") ?: ""
                EdukasiDetailScreen(
                    edukasiId     = edukasiId,
                    navController = navController
                )
            }

            // ── Profil ────────────────────────────────────
            composable("profil") {
                ProfilScreen(onLogout = onLogout)
            }
        }
    }
}