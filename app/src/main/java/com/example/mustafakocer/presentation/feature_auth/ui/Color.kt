package com.example.mustafakocer.presentation.feature_auth.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

/**
 * Material 3 renk paletleri — XML paletleriyle hizalı (Light Softer / Dark Softer)
 */

// ---- LIGHT (Softer) ----
private val PrimaryLight = Color(0xFF1F3A5F)           // Logo rengi
private val OnPrimaryLight = Color(0xFFFFFFFF)
private val PrimaryContainerLight = Color(0xFFDEE6F7)
private val OnPrimaryContainerLight = Color(0xFF0B1C36)

private val SecondaryLight = Color(0xFF5B7083)
private val OnSecondaryLight = Color(0xFFFFFFFF)
private val SecondaryContainerLight = Color(0xFFE3E9F1)
private val OnSecondaryContainerLight = Color(0xFF18222C)

private val TertiaryLight = Color(0xFF0EA5E9)
private val OnTertiaryLight = Color(0xFFFFFFFF)
private val TertiaryContainerLight = Color(0xFFCFEFFF)
private val OnTertiaryContainerLight = Color(0xFF002031)

private val ErrorLight = Color(0xFFB3261E)
private val OnErrorLight = Color(0xFFFFFFFF)
private val ErrorContainerLight = Color(0xFFF9DEDC)
private val OnErrorContainerLight = Color(0xFF410E0B)

private val BackgroundLight = Color(0xFFF7F8FA)
private val OnBackgroundLight = Color(0xFF0E1320)

private val SurfaceLight = Color(0xFFFFFFFF)
private val OnSurfaceLight = Color(0xFF0E1320)
private val SurfaceVariantLight = Color(0xFFE5EAF0)
private val OnSurfaceVariantLight = Color(0xFF435366)

private val OutlineLight = Color(0xFF8A98A7)
private val OutlineVariantLight = Color(0xFFC7D0DA)

private val SurfaceTintLight = PrimaryLight
private val InverseSurfaceLight = Color(0xFF1D232E)
private val InverseOnSurfaceLight = Color(0xFFE4E6EB)
private val InversePrimaryLight = Color(0xFF90A9D9)
private val ScrimLight = Color(0xFF000000)

// ---- DARK (Softer) ----
private val PrimaryDark = Color(0xFF90A9D9)
private val OnPrimaryDark = Color(0xFF0D1B2E)
private val PrimaryContainerDark = Color(0xFF314768)
private val OnPrimaryContainerDark = Color(0xFFDEE6F7)

private val SecondaryDark = Color(0xFFA7B4C4)
private val OnSecondaryDark = Color(0xFF1A252F)
private val SecondaryContainerDark = Color(0xFF3D4B5C)
private val OnSecondaryContainerDark = Color(0xFFE2E9F1)

private val TertiaryDark = Color(0xFF64C8F0)
private val OnTertiaryDark = Color(0xFF002331)
private val TertiaryContainerDark = Color(0xFF195973)
private val OnTertiaryContainerDark = Color(0xFFCFF4FF)

private val ErrorDark = Color(0xFFFFB4A9)
private val OnErrorDark = Color(0xFF680003)
private val ErrorContainerDark = Color(0xFF93000A)
private val OnErrorContainerDark = Color(0xFFFFDAD4)

private val BackgroundDark = Color(0xFF161C26)
private val OnBackgroundDark = Color(0xFFE4E6EB)

private val SurfaceDark = Color(0xFF1D232E)
private val OnSurfaceDark = Color(0xFFE4E6EB)
private val SurfaceVariantDark = Color(0xFF3E495A)
private val OnSurfaceVariantDark = Color(0xFFC8D0DA)

private val OutlineDark = Color(0xFF8A95A3)
private val OutlineVariantDark = Color(0xFF46505F)

private val SurfaceTintDark = PrimaryDark
private val InverseSurfaceDark = Color(0xFFF7F8FA)
private val InverseOnSurfaceDark = Color(0xFF1D232E)
private val InversePrimaryDark = Color(0xFF1F3A5F)
private val ScrimDark = Color(0xFF000000)

// Expose as ColorScheme
val LightAppColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inversePrimary = InversePrimaryLight,
    surfaceTint = SurfaceTintLight,
    scrim = ScrimLight
)

val DarkAppColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark,
    surfaceTint = SurfaceTintDark,
    scrim = ScrimDark
)
