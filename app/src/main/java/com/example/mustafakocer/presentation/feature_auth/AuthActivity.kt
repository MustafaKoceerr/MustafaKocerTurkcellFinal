package com.example.mustafakocer.presentation.feature_auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.mustafakocer.presentation.feature_auth.ui.LoginScreenComposeTheme
import com.example.mustafakocer.presentation.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main entry point activity for the application's UI.
 * It sets up the Jetpack Compose content and hosts the navigation graph via [AppNavHost].
 */
@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginScreenComposeTheme {
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
}