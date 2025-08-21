package com.example.mustafakocer.presentation.feature_splash

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mustafakocer.presentation.navigation.contracts.SplashNavActions
import com.example.mustafakocer.presentation.navigation.destinations.SplashScreenRoute

/**
 * Defines the navigation graph for the splash feature.
 *
 * @param navController The main NavController.
 * @param navActions The navigation actions that can be triggered from the splash screen.
 */
fun NavGraphBuilder.splashNavGraph(
    navController: NavController,
    navActions: SplashNavActions,
) {
    composable<SplashScreenRoute> {
        // The SplashRoute composable connects the ViewModel to the SplashScreen UI.
        SplashRoute(navActions = navActions)
    }

    // Other destinations related to this graph (e.g., an "Update Required" screen)
    // could be added here.
}