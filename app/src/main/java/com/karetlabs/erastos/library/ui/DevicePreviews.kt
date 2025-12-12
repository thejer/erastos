package com.karetlabs.erastos.library.ui

import androidx.compose.ui.tooling.preview.Preview

/**
 * Defines custom preview annotations for various device screen sizes.
 * - `@SmallDevicePreview`: Generates a preview for a small device (5.81-inch screen).
 * - `@MediumDevicePreview`: Generates a preview for a medium device (6.2-inch screen).
 * - `@LargeDevicePreview`: Generates a preview for a large device (6.8-inch screen).
 *
 * By applying the `@DevicePreviews` annotation to a composable function, all three screen sizes will be previewed by default.
 * For more granular control, you can apply the individual device-specific annotations as needed.
 */

@TinyDevicePreview
@SmallDevicePreview
@MediumDevicePreview
@LargeDevicePreview
annotation class DevicePreviews

@Preview(
    name = "Tiny Device - 4.65 inch",
    device = "spec:width=720px,height=1280px,dpi=320",
    showSystemUi = false,
    showBackground = true
)
annotation class TinyDevicePreview

@Preview(
    name = "Small Device - 5.81 inch",
    device = "spec:width=1080px,height=2340px,dpi=443",
    showSystemUi = false,
    showBackground = true
)
annotation class SmallDevicePreview

@Preview(
    name = "Medium Device - 6.2 inch",
    device = "spec:width=1080px,height=2400px,dpi=421",
    showSystemUi = false,
    showBackground = true
)
annotation class MediumDevicePreview

@Preview(
    name = "Large Device - 6.8 inch",
    device = "spec:width=1440px,height=3088px,dpi=500",
    showSystemUi = false,
    showBackground = true
)
annotation class LargeDevicePreview