@file:Suppress("unused")

package com.karetlabs.erastos.library.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.karetlabs.erastos.R

val bodyFontFamily = FontFamily(
    Font(R.font.inter_body_thin, FontWeight.Thin),
    Font(R.font.inter_body_extra_light, FontWeight.ExtraLight),
    Font(R.font.inter_body_light, FontWeight.Light),
    Font(R.font.inter_body_regular, FontWeight.Normal),
    Font(R.font.inter_body_medium, FontWeight.Medium),
    Font(R.font.inter_body_semi_bold, FontWeight.SemiBold),
    Font(R.font.inter_body_bold, FontWeight.Bold),
    Font(R.font.inter_body_extra_bold, FontWeight.ExtraBold),
    Font(R.font.inter_body_black, FontWeight.Black),
)

val headerFontFamily = FontFamily(
    Font(R.font.inter_header_thin, FontWeight.Thin),
    Font(R.font.inter_header_extralight, FontWeight.ExtraLight),
    Font(R.font.inter_header_light, FontWeight.Light),
    Font(R.font.inter_header_regular, FontWeight.Normal),
    Font(R.font.inter_header_medium, FontWeight.Medium),
    Font(R.font.inter_header_semibold, FontWeight.SemiBold),
    Font(R.font.inter_header_bold, FontWeight.Bold),
    Font(R.font.inter_header_extrabold, FontWeight.ExtraBold),
    Font(R.font.inter_header_black, FontWeight.Black),
)

val LocalDimensions = staticCompositionLocalOf { AppDimensions }

val LocalTypography = staticCompositionLocalOf { AppTypography() }

fun materialTypography(type: AppTypography = AppTypography()): Typography =
    Typography(
        // Display
        displayLarge = type.displayLarge,
        displayMedium = type.displayMedium,
        displaySmall = type.displaySmall,

        // Headline
        headlineLarge = type.headlineLarge,
        headlineMedium = type.headlineMedium,
        headlineSmall = type.headlineSmall,

        // Title
        titleLarge = type.titleLarge,
        titleMedium = type.titleMedium,
        titleSmall = type.titleSmall,

        // Body
        bodyLarge = type.bodyLarge,
        bodyMedium = type.bodyMedium,
        bodySmall = type.bodySmall,

        // Label
        labelLarge = type.labelLarge,
        labelMedium = type.labelMedium,
        labelSmall = type.labelSmall,
    )

private val LightScheme = lightColorScheme(
    // Primary
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,

    // Secondary
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,

    // Tertiary
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,

    // Error
    error = error,
    onError = onError,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,

    // Background & Surface
    background = background,
    onBackground = foreground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,

    // Outline & Other
    outline = outline,
    outlineVariant = outlineVariant,
    scrim = scrim,
    inverseSurface = inverseSurface,
    inverseOnSurface = inverseOnSurface,
    inversePrimary = inversePrimary,

    // Elevation tint
    surfaceTint = surfaceTint,
)

private val DarkScheme = darkColorScheme(
    // Primary
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,

    // Secondary
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,

    // Tertiary
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,

    // Error
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,

    // Background & Surface
    background = backgroundDark,
    onBackground = foregroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,

    // Outline & Other
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,

    // Elevation tint
    surfaceTint = surfaceTintDark,
)

private fun shapes(d: AppDimensions) = Shapes(
    extraSmall = RoundedCornerShape(d.radiusSmall),
    small = RoundedCornerShape(d.radiusMedium),
    medium = RoundedCornerShape(d.radiusBase),
    large = RoundedCornerShape(d.radiusLarge),
    extraLarge = RoundedCornerShape(d.radiusXl),
)

val typography: AppTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalTypography.current

val dimension: AppDimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalDimensions.current

@Composable
fun ErastosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val type = AppTypography()

    CompositionLocalProvider(
        LocalDimensions provides AppDimensions,
        LocalTypography provides type,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkScheme else LightScheme,
            typography = materialTypography(type),
            shapes = shapes(AppDimensions),
            content = content
        )
    }

//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            val window = (view.context as Activity).window
//            window.statusBarColor = colorScheme.primary.toArgb()
//            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
//        }
//    }

}
