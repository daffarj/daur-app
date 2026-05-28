package com.daur.app.ui

import android.content.Context
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.daur.app.data.SessionManager
import com.daur.app.ui.screens.LoginScreen
import com.daur.app.ui.screens.MainScreen
import com.daur.app.ui.screens.SplashScreen
import kotlinx.coroutines.launch

object Routes {
    const val SPLASH = "splash"
    const val LOGIN  = "login"
    const val MAIN   = "main"
}

@Composable
fun DaurNavGraph(context: Context, startDestination: String = Routes.SPLASH) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // ── Observasi event session expired dari mana saja ─────
    LaunchedEffect(Unit) {
        SessionManager.sessionExpired.collect {
            // Clear token dulu
            SessionManager.clear(context)
            // Redirect ke login, hapus semua back stack
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
                
            }

        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    val destination = if (SessionManager.isLoggedIn) Routes.MAIN else Routes.LOGIN
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}