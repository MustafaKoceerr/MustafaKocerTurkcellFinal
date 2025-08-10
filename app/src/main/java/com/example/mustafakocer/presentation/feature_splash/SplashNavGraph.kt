package com.example.mustafakocer.presentation.feature_splash

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mustafakocer.presentation.navigation.contracts.SplashNavActions
import com.example.mustafakocer.presentation.navigation.destinations.SplashScreenRoute

/**
 * Splash özelliğinin navigasyon grafiğini tanımlar.
 *
 * @param navController Ana navigasyon kontrolcüsü.
 * @param navActions Splash ekranından tetiklenecek navigasyon eylemleri.
 */
fun NavGraphBuilder.splashNavGraph(
    navController: NavController,
    navActions: SplashNavActions,
) {
    composable<SplashScreenRoute> {
        // SplashRoute, ViewModel ile UI arasındaki bağlantıyı kurar.
        SplashRoute(navActions = navActions)
    }

    // Bu grafiğe ait diğer ekranlar (eğer olsaydı) buraya eklenebilirdi.
    // Örneğin, bir "Uygulama Güncellemesi Gerekli" ekranı.
}