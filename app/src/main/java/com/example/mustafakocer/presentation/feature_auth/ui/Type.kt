package com.example.mustafakocer.presentation.feature_auth.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mustafakocer.R

// 1) Font aileleri (XML'deki dosya adlarıyla birebir eşleştir)
val InterFamily = FontFamily(
    Font(R.font.inter_24pt_regular, FontWeight.Normal),
    Font(R.font.inter_24pt_medium, FontWeight.Medium),
    Font(R.font.inter_24pt_semibold, FontWeight.SemiBold),
    Font(R.font.inter_24pt_bold, FontWeight.Bold),
)

val ManropeFamily = FontFamily(
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
)

// 2) Material3 tipografisini, sadece fontFamily/weight’i override ederek kur
val AppTypography: Typography = run {
    val base = Typography()
    Typography(
        // Başlıklar = Manrope (bold)
        displayLarge  = base.displayLarge.copy(  fontFamily = ManropeFamily, fontWeight = FontWeight.Bold ),
        displayMedium = base.displayMedium.copy( fontFamily = ManropeFamily, fontWeight = FontWeight.Bold ),
        displaySmall  = base.displaySmall.copy(  fontFamily = ManropeFamily, fontWeight = FontWeight.Bold ),
        headlineLarge = base.headlineLarge.copy( fontFamily = ManropeFamily, fontWeight = FontWeight.Bold ),
        headlineMedium= base.headlineMedium.copy(fontFamily = ManropeFamily, fontWeight = FontWeight.Bold ),
        headlineSmall = base.headlineSmall.copy( fontFamily = ManropeFamily, fontWeight = FontWeight.Bold ),
        titleLarge    = base.titleLarge.copy(   fontFamily = ManropeFamily, fontWeight = FontWeight.Bold ),
        titleMedium   = base.titleMedium.copy(  fontFamily = ManropeFamily, fontWeight = FontWeight.Bold ),
        titleSmall    = base.titleSmall.copy(   fontFamily = ManropeFamily, fontWeight = FontWeight.Bold ),

        // Gövde & etiketler = Inter
        bodyLarge  = base.bodyLarge.copy(  fontFamily = InterFamily ),
        bodyMedium = base.bodyMedium.copy( fontFamily = InterFamily ),
        bodySmall  = base.bodySmall.copy(  fontFamily = InterFamily ),
        labelLarge = base.labelLarge.copy( fontFamily = InterFamily ),
        labelMedium= base.labelMedium.copy(fontFamily = InterFamily ),
        labelSmall = base.labelSmall.copy( fontFamily = InterFamily ),
    )
}

// 3) Özel “fiyat” stili (tabular numbers)
object AppText {
    val Price = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        fontFeatureSettings = "tnum" // tabular figures
    )
}
