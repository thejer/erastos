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

private val bodyFontFamily = FontFamily(
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

private val headerFontFamily = FontFamily(
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

@Immutable
data class AppDimensions(
    val radiusSmall: Dp = 6.dp,
    val radiusMedium: Dp = 8.dp,
    val radiusBase: Dp = 10.dp,
    val radiusLarge: Dp = 14.dp,
    val radiusXl: Dp = 18.dp,
    val radiusRounded: Dp = 100.dp,

    val spaceXSmall: Dp = 4.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceMedium: Dp = 12.dp,
    val spaceBase: Dp = 16.dp,
    val spaceLarge: Dp = 20.dp,
    val spaceXl: Dp = 24.dp,

    // Icon sizes
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 20.dp,
    val iconBase: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val iconXl: Dp = 48.dp,
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
    val headlineLarge: TextStyle = x2l.copy(
        fontFamily = headerFontFamily,
        fontWeight = FontWeight.SemiBold
    ),
    val headlineMedium: TextStyle = xl.copy(
        fontFamily = headerFontFamily,
        fontWeight = FontWeight.SemiBold
    ),
    val headlineSmall: TextStyle = large.copy(
        fontFamily = headerFontFamily,
        fontWeight = FontWeight.Normal
    ),
    val titleMedium: TextStyle = base.copy(
        fontFamily = headerFontFamily,
        fontWeight = FontWeight.Bold
    ),
    val weightNormal: FontWeight = FontWeight.Normal, // 400
    val weightMedium: FontWeight = FontWeight.Medium, // 500
    // Body/labels: Inter
    val bodyLarge: TextStyle = base,
    val labelLarge: TextStyle = base.copy(fontWeight = weightMedium),
    val bodyMedium: TextStyle = small,
    val labelMedium: TextStyle = small.copy(fontWeight = weightMedium),
)

val LocalTypography = staticCompositionLocalOf { AppTypography() }

fun materialTypography(type: AppTypography = AppTypography()): Typography =
    Typography(
        // Headers: Merriweather
        headlineLarge = type.x2l.copy(
            fontFamily = headerFontFamily,
            fontWeight = FontWeight.Bold
        ),
        headlineMedium = type.xl.copy(fontFamily = headerFontFamily, fontWeight = FontWeight.Bold),
        headlineSmall = type.large.copy(fontFamily = headerFontFamily, fontWeight = FontWeight.Normal),

        // Titles: Merriweather (sub-headers)
        titleMedium = type.base.copy(
            fontFamily = headerFontFamily,
            fontWeight = FontWeight.Bold
        ),

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
