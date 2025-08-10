package com.example.mustafakocer.presentation.feature_auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mustafakocer.presentation.navigation.contracts.LoginNavActions
import com.example.mustafakocer.presentation.navigation.destinations.LoginScreenRoute

/**
 * Login özelliğinin navigasyon grafiğini tanımlar.
 */
fun NavGraphBuilder.loginNavGraph(
    navController: NavController,
    navActions: LoginNavActions
) {
    composable<LoginScreenRoute> {
        // LoginRoute, ViewModel ile UI arasındaki bağlantıyı kurar.
        LoginRoute(navActions = navActions)
    }
}