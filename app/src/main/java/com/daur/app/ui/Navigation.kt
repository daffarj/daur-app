package com.daur.app.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.daur.app.ui.screens.LoginScreen
import com.daur.app.ui.screens.MainScreen
import com.daur.app.ui.screens.SplashScreen

// ── Routes level-atas (Splash → Login → Main) ─────────────
// Route di dalam tab (beranda, setor, dst) dikelola oleh MainScreen
object Routes {
    const val SPLASH = "splash"
    const val LOGIN  = "login"
    const val MAIN   = "main"
}

@Composable
fun DaurNavGraph(startDestination: String = Routes.SPLASH) {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Login / Register Screen
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Main Screen (Scaffold + BottomNavBar + semua tab)
        composable(Routes.MAIN) {
            MainScreen()
        }
    }
}
