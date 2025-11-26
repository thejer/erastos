package com.karetlabs.erastos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karetlabs.erastos.R

// Fonts
// Put the font files in: app/src/main/res/font/
// Expected resource names:
// - inter_variable.ttf -> R.font.inter_variable
// - merriweather_regular.ttf -> R.font.merriweather_regular
// - merriweather_bold.ttf -> R.font.merriweather_bold
private val bodyFontFamily = FontFamily(
    // Inter variable font supports many weights in one file (Android O/API 26+).
    // We declare common weights so Compose can match requests correctly.
    Font(R.font.inter_variable, FontWeight.Normal),
    Font(R.font.inter_variable, FontWeight.Medium),
    Font(R.font.inter_variable, FontWeight.SemiBold),
    Font(R.font.inter_variable, FontWeight.Bold),
)

private val headerFontFamily = FontFamily(
    Font(R.font.merriweather_variable, FontWeight.Normal),
    Font(R.font.merriweather_variable, FontWeight.Medium),
    Font(R.font.merriweather_variable, FontWeight.SemiBold),
    Font(R.font.merriweather_variable, FontWeight.Bold),
)

@Immutable
data class AppDimensions(
    val radiusSmall: Dp = 6.dp,
    val radiusMedium: Dp = 8.dp,
    val radiusBase: Dp = 10.dp,
    val radiusLarge: Dp = 14.dp,
    val radiusXl: Dp = 18.dp,

    val spaceSmall: Dp = 8.dp,
    val spaceMedium: Dp = 12.dp,
    val spaceBase: Dp = 16.dp,
    val spaceLarge: Dp = 20.dp,
    val spaceXl: Dp = 24.dp,
)

val LocalDimensions = staticCompositionLocalOf { AppDimensions() }

private const val lineHeightMultiplier = 1.5f
private fun lh(fontSizeSp: Int) = (fontSizeSp * lineHeightMultiplier).sp

@Immutable
data class AppTypography(
    // CSS: --font-size: 16px, --font-weight-normal: 400, --font-weight-medium: 500
    val small: TextStyle = TextStyle(
        fontFamily = bodyFontFamily,
        fontSize = 14.sp,
        lineHeight = lh(14),
        fontWeight = FontWeight.Normal
    ),
    val base: TextStyle = TextStyle(
        fontFamily = bodyFontFamily,
        fontSize = 16.sp,
        lineHeight = lh(16),
        fontWeight = FontWeight.Normal
    ),
    val large: TextStyle = TextStyle(
        fontFamily = bodyFontFamily,
        fontSize = 18.sp,
        lineHeight = lh(18),
        fontWeight = FontWeight.Medium
    ),
    val xl: TextStyle = TextStyle(
        fontFamily = bodyFontFamily,
        fontSize = 20.sp,
        lineHeight = lh(20),
        fontWeight = FontWeight.Medium
    ),
    val x2l: TextStyle = TextStyle(
        fontFamily = bodyFontFamily,
        fontSize = 24.sp,
        lineHeight = lh(24),
        fontWeight = FontWeight.Medium
    ),

    val weightNormal: FontWeight = FontWeight.Normal, // 400
    val weightMedium: FontWeight = FontWeight.Medium, // 500
)

val LocalTypography = staticCompositionLocalOf { AppTypography() }

fun materialTypography(type: AppTypography = AppTypography()): Typography =
    Typography(
        // Headers: Merriweather
        headlineLarge = type.x2l.copy(fontFamily = headerFontFamily, fontWeight = FontWeight.Bold),
        headlineMedium = type.xl.copy(fontFamily = headerFontFamily, fontWeight = FontWeight.Bold),
        headlineSmall = type.large.copy(fontFamily = headerFontFamily, fontWeight = FontWeight.Normal),

        // Titles: Merriweather (sub-headers)
        titleMedium = type.base.copy(fontFamily = headerFontFamily, fontWeight = FontWeight.Bold),

        // Body/labels: Inter
        bodyLarge = type.base,
        labelLarge = type.base.copy(fontWeight = type.weightMedium),
        bodyMedium = type.small,
        labelMedium = type.small.copy(fontWeight = type.weightMedium),
    )

private val LightScheme = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    secondary = secondary,
    onSecondary = onSecondary,
    tertiary = accent,
    onTertiary = onAccent,
    error = destructive,
    onError = onDestructive,
    background = background,
    onBackground = foreground,
    surface = card,
    onSurface = cardForeground,
    surfaceVariant = muted,
    onSurfaceVariant = mutedForeground,
    outline = border,
)

private val DarkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    tertiary = accentDark,
    onTertiary = onAccentDark,
    error = destructiveDark,
    onError = onDestructiveDark,
    background = backgroundDark,
    onBackground = foregroundDark,
    surface = cardDark,
    onSurface = cardForegroundDark,
    surfaceVariant = mutedDark,
    onSurfaceVariant = mutedForegroundDark,
    outline = borderDark,
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

    val dimens = AppDimensions()
    val type = AppTypography()

    CompositionLocalProvider(
        LocalDimensions provides dimens,
        LocalTypography provides type,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkScheme else LightScheme,
            typography = materialTypography(type),
            shapes = shapes(dimens),
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
