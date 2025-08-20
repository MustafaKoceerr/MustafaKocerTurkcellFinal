package com.example.mustafakocer.presentation.navigation.contracts

/**
 * Defines the navigation actions that can be triggered from the Splash screen.
 *
 * ARCHITECTURAL NOTE: This interface is a contract that allows the 'feature_splash'
 * module to request navigation without knowing anything about 'feature_auth' or 'MainActivity'.
 * It inverts the dependency: Splash depends on this interface, and the actual
 * navigation logic implements it.
 */
interface SplashNavActions {
    fun navigateToHome()
    fun navigateToLogin()
}