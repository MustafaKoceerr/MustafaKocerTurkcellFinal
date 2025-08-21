package com.example.mustafakocer.presentation.navigation

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    // Create and remember the navigation actions. This prevents them from being
    // recreated on every recomposition, improving performance.
    val navActions = remember(navController, context) {
        AppNavActions(navController, context as Activity)
    }

    NavHost(
        navController = navController,
        startDestination = SplashScreenRoute,
        modifier = modifier
    ) {
        splashNavGraph(
            navController = navController,
            navActions = navActions
        )

        loginNavGraph(
            navController = navController,
            navActions = navActions
        )
    }
}

/**
 * A concrete implementation of the navigation action interfaces.
 * This class centralizes the navigation logic, making it reusable and easier to manage.
 */
private class AppNavActions(
    private val navController: NavHostController,
    private val activity: Activity
) : SplashNavActions, LoginNavActions {

    override fun navigateToHome() {
        val intent = Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        activity.startActivity(intent)
        activity.finish()
    }

    override fun navigateToLogin() {
        navController.navigate(LoginScreenRoute) {
            popUpTo(SplashScreenRoute) { inclusive = true }
        }
    }
}