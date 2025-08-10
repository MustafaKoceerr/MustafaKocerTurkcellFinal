package com.example.mustafakocer.presentation.navigation.contracts


/**
 * Splash ekranından tetiklenebilecek navigasyon eylemlerini tanımlar.
 *
 * MİMARİ NOT: Bu arayüz, 'feature_splash'in, 'feature_auth' veya 'MainActivity'
 * hakkında hiçbir şey bilmeden navigasyon talep etmesini sağlayan bir sözleşmedir.
 * Bağımlılığı tersine çeviririz: Splash bu arayüze bağımlıdır, asıl navigasyon
 * mantığı ise bu arayüzü uygular.
 */
interface SplashNavActions {
    fun navigateToHome()
    fun navigateToLogin()
}