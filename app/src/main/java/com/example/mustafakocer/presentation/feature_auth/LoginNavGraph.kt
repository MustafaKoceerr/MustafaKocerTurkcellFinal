package com.example.mustafakocer.presentation.feature_auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mustafakocer.presentation.navigation.contracts.LoginNavActions
import com.example.mustafakocer.presentation.navigation.destinations.LoginScreenRoute

/**
 * Defines the navigation graph for the entire login feature.
 *
 * This modular approach allows the main `AppNavHost` to include the login flow
 * with a single function call, keeping the main navigation graph clean and organized.
 *
 * @param navController The main NavController, passed down for potential nested navigation.
 * @param navActions The specific navigation actions that can be triggered from the login feature.
 */
fun NavGraphBuilder.loginNavGraph(
    navController: NavController,
    navActions: LoginNavActions
) {
    composable<LoginScreenRoute> {
        // The LoginRoute composable connects the ViewModel to the LoginScreen UI.
        LoginRoute(navActions = navActions)
    }
}