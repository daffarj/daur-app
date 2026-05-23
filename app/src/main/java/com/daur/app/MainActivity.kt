package com.daur.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.daur.app.data.SessionManager
import com.daur.app.ui.DaurNavGraph
import com.daur.app.ui.theme.DaurTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // ✅ setContent di DALAM launch — nunggu load() selesai dulu
        lifecycleScope.launch {
            SessionManager.load(this@MainActivity)
            setContent {
                DaurTheme {
                    DaurNavGraph()
                }
            }
        }
    }
}