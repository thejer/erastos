package com.karetlabs.erastos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.karetlabs.erastos.auth.AuthDeepLink
import com.karetlabs.erastos.data.source.remote.SupabaseClientProvider
import com.karetlabs.erastos.library.ui.theme.ErastosTheme
import com.karetlabs.erastos.nav.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.auth.handleDeeplinks
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var supabaseClientProvider: SupabaseClientProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleAuthIntent(intent)
        setContent {
            ErastosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAuthIntent(intent)
    }

    private fun handleAuthIntent(authIntent: Intent?) {
        val data = authIntent?.data ?: return
        if (data.scheme != AuthDeepLink.SCHEME || data.host != AuthDeepLink.HOST) return

        if (data.path == AuthDeepLink.RECOVERY_PATH) {
            getSharedPreferences(AuthDeepLink.PREFERENCES_NAME, MODE_PRIVATE)
                .edit()
                .putBoolean(AuthDeepLink.PASSWORD_RECOVERY_KEY, true)
                .apply()
        }
        runCatching { supabaseClientProvider.get().handleDeeplinks(authIntent) }
    }
}
