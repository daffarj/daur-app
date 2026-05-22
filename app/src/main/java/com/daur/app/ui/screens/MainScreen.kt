package com.daur.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.daur.app.ui.components.BottomNavBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "beranda"

    Scaffold(
        modifier  = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onItemClick  = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState    = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = "beranda",
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable("beranda") {
                BerandaScreen(
                    onSetor        = { navController.navigate("setor") },
                    onTukarPoin    = { navController.navigate("hadiah") },
                    onLihatRiwayat = { navController.navigate("riwayat") }
                )
            }
            composable("setor")   { SetorSampahScreen() }
            composable("riwayat") { RiwayatSetoranScreen() }
            composable("hadiah")  { TukarPoinScreen() }
            composable("profil")  { ProfilScreen() }
            composable("katalog") { KatalogSampahScreen() }
            composable("edukasi") { EdukasiLingkunganScreen() }
        }
    }
}
