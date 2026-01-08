package com.karetlabs.erastos.library.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.karetlabs.erastos.library.ui.theme.AppDimensions.space12
import com.karetlabs.erastos.library.ui.theme.AppDimensions.space16

object AppDimensions {
    // Base spacing scale (4dp increments)
    val spaceNone: Dp = 0.dp
    val space4: Dp = 4.dp
    val space8: Dp = 8.dp
    val space12: Dp = 12.dp
    val space16: Dp = 16.dp
    val space20: Dp = 20.dp
    val space24: Dp = 24.dp
    val space28: Dp = 28.dp
    val space32: Dp = 32.dp
    val space40: Dp = 40.dp
    val space48: Dp = 48.dp
    val space56: Dp = 56.dp
    val space64: Dp = 64.dp
    val space72: Dp = 72.dp
    val space80: Dp = 80.dp
    val space96: Dp = 96.dp
    val space128: Dp = 128.dp

    // Semantic padding
    val paddingTiny: Dp = space4
    val paddingSmall: Dp = space8
    val paddingMedium: Dp = space16
    val paddingLarge: Dp = space24
    val paddingXLarge: Dp = space32

    // Screen padding
    val screenPaddingHorizontal: Dp = space16
    val screenPaddingVertical: Dp = space16

    // Component spacing
    val componentSpacingSmall: Dp = space8
    val componentSpacingMedium: Dp = space12
    val componentSpacingLarge: Dp = space16

    // Divider
    val dividerThickness: Dp = 1.dp
    val dividerIndent: Dp = space16

    val radiusSmall: Dp = 6.dp
    val radiusMedium: Dp = 8.dp
    val radiusBase: Dp = 10.dp
    val radiusLarge: Dp = 14.dp
    val radiusXl: Dp = 18.dp
    val radiusRounded: Dp = 100.dp

    // Icon sizes
    val iconSmall: Dp = 16.dp
    val iconMedium: Dp = 20.dp
    val iconBase: Dp = 24.dp
    val iconLarge: Dp = 32.dp
    val iconXl: Dp = 48.dp
}



object ComponentDimensions {
    // Status Bar
    val statusBarHeight = 24.dp

    // App Bar
    val appBarHeightRegular = 64.dp
    val appBarHeightLarge = 128.dp
    val appBarElevation = 0.dp // MD3 uses tonal elevation

    // Bottom Navigation
    val bottomNavHeight = 80.dp
    val bottomNavIconSize = 24.dp

    // FAB
    val fabSizeSmall = 40.dp
    val fabSizeRegular = 56.dp
    val fabSizeLarge = 96.dp
    val fabIconSize = 24.dp
    val fabElevation = 6.dp

    // Buttons
    val buttonHeightText = 40.dp
    val buttonHeightFilled = 48.dp
    val buttonMinWidth = 48.dp
    val buttonPaddingHorizontal = 24.dp
    val buttonIconSize = 18.dp
    val buttonIconSpacing = 8.dp

    // Text Fields
    val textFieldHeight = 56.dp
    val textFieldMinWidth = 280.dp
    val textFieldCornerRadius = 8.dp

    // Search Bar
    val searchBarHeight = 48.dp
    val searchBarHeightCompact = 40.dp
    val searchBarIconSize = 20.dp

    // Cards
    val cardMinHeight = 80.dp
    val cardElevation = 1.dp
    val cardElevationHover = 3.dp
    val cardPadding = space16

    // Book Cover
    val bookCoverWidthSmall = 96.dp
    val bookCoverWidthXSmall = 56.dp
    val bookCoverHeightSmall = 144.dp
    val bookCoverWidthMedium = 128.dp
    val bookCoverHeightMedium = 192.dp
    val bookCoverWidthLarge = 160.dp
    val bookCoverHeightLarge = 240.dp

    // Book Card (in grid)
    val bookCardWidth = 156.dp
    val bookCardCoverHeight = 234.dp
    val bookCardContentPadding = space12

    // Icons
    val iconSizeSmall = 16.dp
    val iconSizeRegular = 24.dp
    val iconSizeMedium = 32.dp
    val iconSizeLarge = 48.dp

    // Icon Buttons
    val iconButtonSize = 48.dp
    val iconButtonSizeCompact = 40.dp

    // Chips
    val chipHeight = 32.dp
    val fieldChipHeight = 24.dp
    val chipPaddingHorizontal = 16.dp
    val chipIconSize = 18.dp

    // Bottom Sheet
    val bottomSheetHandleWidth = 32.dp
    val bottomSheetHandleHeight = 4.dp
    val bottomSheetHandleTopMargin = 8.dp
    val bottomSheetPeekHeight = 56.dp

    // Dialogs
    val dialogMinWidth = 280.dp
    val dialogMaxWidth = 560.dp
    val dialogPadding = space16

    // List Items
    val listItemHeight = 56.dp
    val listItemHeightTwoLine = 72.dp
    val listItemHeightThreeLine = 88.dp
    val listItemIconSize = 24.dp
    val listItemAvatarSize = 40.dp

    // Dividers
    val dividerThickness = 1.dp

    // Progress Indicators
    val progressIndicatorSize = 48.dp
    val progressIndicatorSizeSmall = 24.dp
    val progressIndicatorThickness = 4.dp

    // Snackbar
    val snackbarMinHeight = 48.dp
    val snackbarMaxWidth = 672.dp
    val snackbarPadding = space16

    // Navigation Drawer
    val navigationDrawerWidth = 360.dp
    val navigationDrawerItemHeight = 56.dp

    // Badges
    val badgeSize = 16.dp
    val badgeSizeLarge = 20.dp

    // Avatars
    val avatarSizeSmall = 32.dp
    val avatarSizeMedium = 40.dp
    val avatarSizeLarge = 56.dp
    val avatarSizeXLarge = 96.dp
}

/**
 * Touch target dimensions
 */
object TouchTarget {
    // Minimum touch target size (Material Design)
    val minimumSize = 48.dp
    val minimumWidth = 48.dp
    val minimumHeight = 48.dp

    // Recommended touch target size
    val recommendedSize = 48.dp
}

/**
 * Elevation levels (for shadow/tonal elevation)
 */
object Elevation {
    val level0 = 0.dp
    val level1 = 1.dp
    val level2 = 3.dp
    val level3 = 6.dp
    val level4 = 8.dp
    val level5 = 12.dp
}

/**
 * Grid spacing
 */
object Grid {
    val columns = 4
    val gutterPhone = 16.dp
    val gutterTablet = 24.dp
    val marginPhone = 16.dp
    val marginTablet = 24.dp

    // Book grid specific
    val bookGridSpacing = 12.dp
    val bookGridColumns = 2
    val bookGridColumnsTablet = 3
}

/**
 * Border widths
 */
object Border {
    val thin = 1.dp
    val medium = 2.dp
    val thick = 4.dp
}

/**
 * Animation durations (in milliseconds)
 */
object AnimationDuration {
    const val instant = 0
    const val fast = 100
    const val normal = 200
    const val slow = 300
    const val slower = 400
    const val slowest = 500
}

/**
 * Z-Index layers
 */
object ZIndex {
    const val background = 0f
    const val content = 1f
    const val elevated = 2f
    const val dropdown = 3f
    const val sticky = 4f
    const val overlay = 5f
    const val modal = 6f
    const val popover = 7f
    const val tooltip = 8f
    const val notification = 9f
    const val top = 10f
}