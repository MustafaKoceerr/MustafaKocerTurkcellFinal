package com.example.mustafakocer.presentation.navigation

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.mustafakocer.presentation.feature_auth.loginNavGraph
import com.example.mustafakocer.presentation.feature_splash.splashNavGraph
import com.example.mustafakocer.presentation.navigation.contracts.LoginNavActions
import com.example.mustafakocer.presentation.navigation.contracts.SplashNavActions
import com.example.mustafakocer.presentation.navigation.destinations.LoginScreenRoute
import com.example.mustafakocer.presentation.navigation.destinations.SplashScreenRoute
import com.example.mustafakocer.presentation.shell.MainActivity

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current

    NavHost(
        navController = navController,
        startDestination = SplashScreenRoute,
        modifier = modifier
    ) {
        // 1. Her NavGraph için ayrı ayrı, anonim 'object' implementasyonları oluşturuyoruz.
        splashNavGraph(
            navController = navController,
            navActions = object : SplashNavActions {
                override fun navigateToHome() {
                    // 2. Tekrarlanan mantığı private bir yardımcı fonksiyona yönlendiriyoruz.
                    activity?.navigateToHomeAndFinish()
                }

                override fun navigateToLogin() {
                    navController.navigate(LoginScreenRoute) {
                        popUpTo(SplashScreenRoute) { inclusive = true }
                    }
                }
            }
        )

        loginNavGraph(
            navController = navController,
            navActions = object : LoginNavActions {
                override fun navigateToHome() {
                    // 2. Tekrarlanan mantığı aynı yardımcı fonksiyona yönlendiriyoruz.
                    activity?.navigateToHomeAndFinish()
                }
            }
        )
    }
}

/**
 * MainActivity'ye giden ve mevcut Activity'yi sonlandıran,
 * tekrarlanan navigasyon mantığını kapsülleyen özel bir yardımcı fonksiyon.
 * Bu bir extension function olduğu için, sadece bu dosya içinden erişilebilir (private).
 */
private fun Activity.navigateToHomeAndFinish() {
    val intent = Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
    finish()
}