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

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Temanızı uygulayın.
            LoginScreenComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. NavController'ı en üst seviyede oluşturun.
                    val navController = rememberNavController()

                    // 2. Tüm navigasyon mantığını yönetmesi için AppNavHost'u çağırın.
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}