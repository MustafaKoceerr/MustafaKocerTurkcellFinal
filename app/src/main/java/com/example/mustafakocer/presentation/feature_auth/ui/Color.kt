package com.example.mustafakocer.presentation.feature_auth.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

// ---- LIGHT ----
private val PrimaryLight = Color(0xFF1F3A5F)
private val OnPrimaryLight = Color(0xFFFFFFFF)
private val PrimaryContainerLight = Color(0xFFD6E3FF)
private val OnPrimaryContainerLight = Color(0xFF001A43)

private val SecondaryLight = Color(0xFF5B7083)
private val OnSecondaryLight = Color(0xFFFFFFFF)
private val SecondaryContainerLight = Color(0xFFDFE7EF)
private val OnSecondaryContainerLight = Color(0xFF14202A)

private val TertiaryLight = Color(0xFF0EA5E9)
private val OnTertiaryLight = Color(0xFFFFFFFF)
private val TertiaryContainerLight = Color(0xFFCBEFFF)
private val OnTertiaryContainerLight = Color(0xFF001F29)

private val ErrorLight = Color(0xFFB3261E)
private val OnErrorLight = Color(0xFFFFFFFF)
private val ErrorContainerLight = Color(0xFFF9DEDC)
private val OnErrorContainerLight = Color(0xFF410E0B)

private val BackgroundLight = Color(0xFFF7F8FA)
private val OnBackgroundLight = Color(0xFF0E1320)

private val SurfaceLight = Color(0xFFFFFFFF)
private val OnSurfaceLight = Color(0xFF0E1320)
private val SurfaceVariantLight = Color(0xFFE5EAF0)
private val OnSurfaceVariantLight = Color(0xFF425266)

private val OutlineLight = Color(0xFF8A98A7)
private val OutlineVariantLight = Color(0xFFC7D0DA)

private val SurfaceTintLight = PrimaryLight
private val InverseSurfaceLight = Color(0xFF1F2937)
private val InverseOnSurfaceLight = Color(0xFFE5E7EB)
private val InversePrimaryLight = Color(0xFFABC2FF)
private val ScrimLight = Color(0xFF000000)

// ---- DARK ----
private val PrimaryDark = Color(0xFFABC2FF)
private val OnPrimaryDark = Color(0xFF0B1B3A)
private val PrimaryContainerDark = Color(0xFF133250)
private val OnPrimaryContainerDark = Color(0xFFD6E3FF)

private val SecondaryDark = Color(0xFFB7C4D0)
private val OnSecondaryDark = Color(0xFF1B2630)
private val SecondaryContainerDark = Color(0xFF3B4854)
private val OnSecondaryContainerDark = Color(0xFFDFE7EF)

private val TertiaryDark = Color(0xFF7AD3FF)
private val OnTertiaryDark = Color(0xFF002638)
private val TertiaryContainerDark = Color(0xFF004B63)
private val OnTertiaryContainerDark = Color(0xFFCBEFFF)

private val ErrorDark = Color(0xFFFFB4AB)
private val OnErrorDark = Color(0xFF690005)
private val ErrorContainerDark = Color(0xFF93000A)
private val OnErrorContainerDark = Color(0xFFFFDAD6)

private val BackgroundDark = Color(0xFF0B121A)
private val OnBackgroundDark = Color(0xFFE5E7EB)

private val SurfaceDark = Color(0xFF0F141B)
private val OnSurfaceDark = Color(0xFFE5E7EB)
private val SurfaceVariantDark = Color(0xFF3F4A58)
private val OnSurfaceVariantDark = Color(0xFFC7D0DA)

private val OutlineDark = Color(0xFF8A98A7)
private val OutlineVariantDark = Color(0xFF3F4A58)

private val SurfaceTintDark = PrimaryDark
private val InverseSurfaceDark = Color(0xFFF7F8FA)
private val InverseOnSurfaceDark = Color(0xFF0E1320)
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
