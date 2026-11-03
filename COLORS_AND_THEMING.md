# Colors & Theming Guide

**Project:** Erastos Book Library  
**Last Updated:** April 17, 2026  
**Status:** Active

---

## Table of Contents

1. [Overview](#overview)
2. [Color Palette](#color-palette)
3. [Theme System](#theme-system)
4. [Material 3 Integration](#material-3-integration)
5. [Using Colors in Compose](#using-colors-in-compose)
6. [Custom Theme Composables](#custom-theme-composables)
7. [Dark Mode Support](#dark-mode-support)
8. [Typography & Dimensions](#typography--dimensions)
9. [Best Practices](#best-practices)
10. [Troubleshooting](#troubleshooting)

---

## Overview

Erastos uses a comprehensive theming system built on **Material Design 3** with support for both light and dark modes. The color system is organized by semantic meaning and usage context, making it easy to maintain consistency across the application.

### Color Architecture

```
Color.kt (Raw colors)
    ↓
Theme.kt (MaterialTheme setup)
    ↓
Custom Typography & Dimensions
    ↓
Composables (Use colors via theme)
```

---

## Color Palette

### Light Mode Colors

The light theme defines semantic colors that map to Material 3 specifications:

```kotlin
// Basic Colors
val background = Color(0xFFFFFBFE)           // Main background
val foreground = Color(0xFF1C1B1F)           // Main text
val card = Color(0xFFF6F6F6)                 // Card surfaces
val cardForeground = Color(0xFF1C1B1F)       // Card text

// Primary (Main brand color)
val primary = Color(0xFF6750A4)              // Purple
val onPrimary = Color(0xFFFFFFFF)            // White text on primary
val primaryContainer = Color(0xFFEADDFF)     // Light purple background
val onPrimaryContainer = Color(0xFF21005D)   // Dark purple text on container

// Secondary (Supporting color)
val secondary = Color(0xFF625B71)            // Purple-gray
val onSecondary = Color(0xFFFFFFFF)          // White text on secondary
val secondaryContainer = Color(0xFFE8DEF8)   // Light purple-gray
val onSecondaryContainer = Color(0xFF1D192B) // Dark text on container

// Tertiary/Accent (Complementary color)
val accent = Color(0xFF7D5260)               // Rose
val onAccent = Color(0xFFFFFFFF)             // White text on accent
val tertiaryContainer = Color(0xFFFFD8E4)    // Light rose
val onTertiaryContainer = Color(0xFF31111D)  // Dark rose text

// Error/Destructive (For alerts/delete)
val destructive = Color(0xFFB3261E)          // Red
val onDestructive = Color(0xFFFFFFFF)        // White text on destructive
val errorContainer = Color(0xFFF9DEDC)       // Light red background
val onErrorContainer = Color(0xFF410E0B)     // Dark red text

// Surface & Neutral Colors
val muted = Color(0xFFE7E0EC)                // Muted background
val mutedForeground = Color(0xFF49454F)      // Muted text
val border = Color(0xFFCAC4D0)               // Border lines
val ring = Color(0xFF79747E)                 // Focus ring/outline
val surfaceVariant = muted                   // Variant surface
val onSurfaceVariant = mutedForeground       // Text on variant

// Input & Interactive
val inputBackground = Color(0xFFE7E0EC)      // Input field background
val switchBackground = Color(0xFFCAC4D0)     // Switch/toggle background

// Special Colors
val shadow = Color(0xFF000000)               // Shadow color
val scrim = Color(0xFF000000)                // Modal scrim overlay
val link = Color(0xFF5495FF)                 // Link color
```

### Dark Mode Colors

```kotlin
// Dark mode uses inverted brightness but maintains hue relationships
val backgroundDark = Color(0xFF1C1B1F)           // Dark background
val foregroundDark = Color(0xFFE6E1E5)           // Light text

val primaryDark = Color(0xFFA78BFA)              // Lighter purple
val onPrimaryDark = Color(0xFF381E72)            // Darker purple text

val secondaryDark = Color(0xFFCCC2DC)            // Lighter purple-gray
val onSecondaryDark = Color(0xFF332D41)          // Darker text

// ... other colors follow similar light/dark pattern
```

### Chart Colors

For data visualization and charts:

```kotlin
// Light mode
val chart1 = Color(0xFFF54900)    // Orange
val chart2 = Color(0xFF009689)    // Teal
val chart3 = Color(0xFF104E64)    // Navy
val chart4 = Color(0xFFFFB900)    // Amber
val chart5 = Color(0xFFFE9A00)    // Orange-yellow

// Dark mode
val chart1Dark = Color(0xFF1447E6)  // Blue
val chart2Dark = Color(0xFF00BC7D)  // Green
val chart3Dark = Color(0xFFFE9A00)  // Orange
val chart4Dark = Color(0xFFAD46FF)  // Purple
val chart5Dark = Color(0xFFFF2056)  // Red
```

### Sidebar Colors

For navigation sidebar components:

```kotlin
// Light mode
val sidebar = Color(0xFFFAFAFA)           // Sidebar background
val sidebarForeground = Color(0xFF0A0A0A) // Text
val sidebarPrimary = Color(0xFF030213)    // Accent
val sidebarBorder = Color(0xFFE5E5E5)     // Border

// Dark mode
val sidebarDark = Color(0xFF171717)
val sidebarForegroundDark = Color(0xFFFAFAFA)
// ... etc
```

---

## Theme System

### File Structure

```
library/ui/theme/
├── Color.kt              # Color definitions
├── Type.kt               # Typography definitions
├── Dimension.kt          # Spacing & sizing
├── Theme.kt              # Main theme composable
└── ...other theme files
```

### Theme.kt Implementation Pattern

The theme should follow this structure:

```kotlin
package com.karetlabs.erastos.library.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.ColorScheme

private val LightColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = accent,
    onTertiary = onAccent,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    error = destructive,
    onError = onDestructive,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,
    background = background,
    onBackground = foreground,
    surface = card,
    onSurface = cardForeground,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    outline = outline,
    outlineVariant = outlineVariant,
    inverseSurface = inverseSurface,
    inverseOnSurface = inverseOnSurface,
    inversePrimary = inversePrimary,
    surfaceTint = surfaceTint,
    scrim = scrim,
)

private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    // ... all colors mapped for dark mode
)

@Composable
fun ErastosTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // From Type.kt
        content = content
    )
}
```

---

## Material 3 Integration

### Color Scheme Mapping

Material 3 requires these semantic colors to be mapped:

| Material 3 Property | Purpose | Light Value | Dark Value |
|---|---|---|---|
| `primary` | Main brand color | `#6750A4` | `#A78BFA` |
| `onPrimary` | Text on primary | `#FFFFFF` | `#381E72` |
| `primaryContainer` | Container background | `#EADDFF` | `#4F378B` |
| `onPrimaryContainer` | Text on container | `#21005D` | `#EADDFF` |
| `secondary` | Supporting color | `#625B71` | `#CCC2DC` |
| `onSecondary` | Text on secondary | `#FFFFFF` | `#332D41` |
| `tertiary` | Complementary color | `#7D5260` | `#EFB8C8` |
| `error` | Error/destructive | `#B3261E` | `#F2B8B5` |
| `background` | Screen background | `#FFFBFE` | `#1C1B1F` |
| `surface` | Card/surface backgrounds | `#F6F6F6` | `#1C1B1F` |
| `outline` | Borders and dividers | `#79747E` | `#938F99` |
| `surfaceVariant` | Secondary surface | `#E7E0EC` | `#49454F` |

### Accessing Theme Colors in Composables

```kotlin
@Composable
fun MyComposable() {
    val colorScheme = MaterialTheme.colorScheme
    
    // Access colors
    val backgroundColor = colorScheme.background
    val textColor = colorScheme.onBackground
    val primaryColor = colorScheme.primary
    val errorColor = colorScheme.error
    
    // Use in UI
    Box(modifier = Modifier.background(backgroundColor)) {
        Text(
            text = "Hello",
            color = textColor
        )
    }
}
```

---

## Using Colors in Compose

### Best Practices

#### 1. Always Use Theme Colors

```kotlin
// ✅ Good - Uses theme color
Text(
    text = "Title",
    color = MaterialTheme.colorScheme.onBackground
)

// ❌ Bad - Hardcoded color
Text(
    text = "Title",
    color = Color(0xFF1C1B1F)
)
```

#### 2. Use Semantic Color Names

```kotlin
// ✅ Good - Clear intent
Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ),
    onClick = { }
) {
    Text("Action")
}

// ✅ Good - For destructive actions
Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    ),
    onClick = { }
) {
    Text("Delete")
}
```

#### 3. Combine with Opacity for Variations

```kotlin
@Composable
fun MutedText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        style = MaterialTheme.typography.bodyMedium
    )
}
```

#### 4. Use Surface for Layering

```kotlin
@Composable
fun Card(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Text(
            text = "Content",
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp)
        )
    }
}
```

### Color Examples for Common Components

#### Text

```kotlin
// Primary text
Text(
    text = "Heading",
    color = MaterialTheme.colorScheme.onBackground,
    style = MaterialTheme.typography.headlineLarge
)

// Secondary text (muted)
Text(
    text = "Description",
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    style = MaterialTheme.typography.bodyMedium
)
```

#### Buttons

```kotlin
// Primary button
Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
    ),
    onClick = { }
) {
    Text("Primary Action")
}

// Secondary button
OutlinedButton(
    colors = OutlinedButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    onClick = { }
) {
    Text("Secondary Action")
}

// Destructive button
Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.error
    ),
    onClick = { }
) {
    Text("Delete")
}
```

#### Cards

```kotlin
Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Card Title")
        Text("Card content")
    }
}
```

#### Input Fields

```kotlin
TextField(
    value = text,
    onValueChange = { text = it },
    colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
    ),
    modifier = Modifier.fillMaxWidth()
)
```

#### Chips

```kotlin
FilterChip(
    selected = isSelected,
    onClick = { },
    label = { Text("Chip") },
    colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        labelColor = MaterialTheme.colorScheme.onSurface,
        containerColor = MaterialTheme.colorScheme.surface
    )
)
```

#### Dividers

```kotlin
HorizontalDivider(
    color = MaterialTheme.colorScheme.outline,
    thickness = 1.dp
)

VerticalDivider(
    color = MaterialTheme.colorScheme.outlineVariant,
    thickness = 1.dp
)
```

---

## Custom Theme Composables

### Creating Theme-Aware Composables

```kotlin
// In library/ui/theme/Theme.kt or custom extension file

@Composable
fun CustomErrorText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
    )
}

@Composable
fun SuccessButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50) // Success green
        ),
        modifier = modifier
    ) {
        Text(text)
    }
}
```

### Creating Color Extensions

```kotlin
// Extension functions for easier access
val MaterialTheme.colorScheme.successContainer: Color
    @Composable get() = Color(0xFFC8E6C9)

val MaterialTheme.colorScheme.onSuccessContainer: Color
    @Composable get() = Color(0xFF1B5E20)

// Usage
Box(modifier = Modifier.background(MaterialTheme.colorScheme.successContainer)) {
    Text(
        text = "Success",
        color = MaterialTheme.colorScheme.onSuccessContainer
    )
}
```

---

## Dark Mode Support

### Automatic Dark Mode Detection

```kotlin
@Composable
fun ErastosTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(), // Automatically detects
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(colorScheme = colorScheme, content = content)
}
```

### Testing Dark Mode in Previews

```kotlin
@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Composable
fun PreviewLight() {
    ErastosTheme(useDarkTheme = false) {
        MyScreen()
    }
}

@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun PreviewDark() {
    ErastosTheme(useDarkTheme = true) {
        MyScreen()
    }
}
```

### Manual Dark Mode Toggle (Optional)

For apps that need manual theme control:

```kotlin
// In a settings/preferences file
object ThemePreferences {
    var isDarkMode by mutableStateOf(false)
        private set
    
    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
    }
}

// Usage in theme
@Composable
fun ErastosTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (ThemePreferences.isDarkMode) DarkColorScheme else LightColorScheme,
        content = content
    )
}
```

---

## Typography & Dimensions

### Typography System

Material 3 typography consists of 14 predefined styles:

```kotlin
// From Type.kt
val Typography = Typography(
    // Display styles (large, prominent text)
    displayLarge = TextStyle(
        fontSize = 57.sp,
        lineHeight = 64.sp,
        fontWeight = FontWeight.W400,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontSize = 45.sp,
        lineHeight = 52.sp,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontSize = 36.sp,
        lineHeight = 44.sp,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp
    ),
    // Headline styles
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp
    ),
    // Title styles
    titleLarge = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.W500,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.W500,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.W500,
        letterSpacing = 0.1.sp
    ),
    // Body styles
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.4.sp
    ),
    // Label styles
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.W500,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W500,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W500,
        letterSpacing = 0.5.sp
    )
)
```

### Using Typography

```kotlin
@Composable
fun TextExamples() {
    Column(spacing = 8.dp) {
        // Headings
        Text("Page Title", style = MaterialTheme.typography.headlineLarge)
        Text("Section Title", style = MaterialTheme.typography.headlineMedium)
        Text("Subsection", style = MaterialTheme.typography.titleLarge)
        
        // Body text
        Text("Body content goes here", style = MaterialTheme.typography.bodyLarge)
        Text("Smaller body text", style = MaterialTheme.typography.bodyMedium)
        
        // Labels/Captions
        Text("Caption text", style = MaterialTheme.typography.labelSmall)
    }
}
```

### Dimensions & Spacing

Spacing should be managed through a centralized dimensions object:

```kotlin
// In library/ui/theme/Dimension.kt
object Dimension {
    // Padding
    val paddingTiny = 4.dp
    val paddingSmall = 8.dp
    val paddingMedium = 16.dp
    val paddingLarge = 24.dp
    val paddingXLarge = 32.dp
    
    // Screen padding
    val screenPaddingHorizontal = 16.dp
    val screenPaddingVertical = 24.dp
    
    // Component spacing
    val componentSpacingSmall = 8.dp
    val componentSpacingMedium = 16.dp
    val componentSpacingLarge = 24.dp
    
    // Icon sizes
    val iconSmall = 16.dp
    val iconMedium = 24.dp
    val iconLarge = 32.dp
    
    // Corner radius
    val radiusSmall = 4.dp
    val radiusMedium = 8.dp
    val radiusLarge = 16.dp
}

// Usage
val dimension = Dimension

Text(
    text = "Hello",
    modifier = Modifier.padding(
        horizontal = dimension.screenPaddingHorizontal,
        vertical = dimension.screenPaddingVertical
    )
)
```

---

## Best Practices

### Color Selection Rules

1. **Maintain Contrast**
   - Text on backgrounds must meet WCAG AA standards (4.5:1 ratio for body text)
   - Test contrast using accessibility tools

2. **Semantic Consistency**
   - Use `error` color only for errors, destructive actions, and alerts
   - Use `primary` for main actions and navigation
   - Use `secondary` for supporting actions

3. **Avoid Color Drift**
   ```kotlin
   // ❌ Bad - Creates many similar colors
   val button1 = Color(0xFF6750A4)
   val button2 = Color(0xFF6854A8)
   val button3 = Color(0xFF6A58AC)
   
   // ✅ Good - Uses established palette
   val primary = Color(0xFF6750A4)
   val primaryLight = primaryDark.copy(alpha = 0.8f)
   val primaryDark = Color(0xFF5A4B8F)
   ```

4. **Dark Mode Parity**
   - Ensure every light mode color has a corresponding dark mode color
   - Test both modes thoroughly

### Accessibility Guidelines

```kotlin
// ✅ Accessible - Good contrast
Text(
    text = "Important",
    color = MaterialTheme.colorScheme.error,
    style = MaterialTheme.typography.bodyMedium
)

// ❌ Not accessible - Insufficient contrast
Text(
    text = "Subtle",
    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
    style = MaterialTheme.typography.bodyMedium
)
```

### Performance Optimization

```kotlin
// ❌ Inefficient - Recomposes and recalculates
@Composable
fun SlowComposable() {
    val colorScheme = MaterialTheme.colorScheme  // Accessed repeatedly
    Text(color = colorScheme.primary)
    Text(color = colorScheme.primary)
    Text(color = colorScheme.primary)
}

// ✅ Efficient - Extract to variable
@Composable
fun FastComposable() {
    val primaryColor = MaterialTheme.colorScheme.primary  // Accessed once
    Text(color = primaryColor)
    Text(color = primaryColor)
    Text(color = primaryColor)
}
```

---

## Troubleshooting

### Common Issues

#### 1. Colors Look Different on Dark Mode

**Problem:** Colors appear incorrect in dark mode.

**Solution:** Verify dark mode colors are properly mapped in `DarkColorScheme`:
```kotlin
private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,  // Must match light mode hue
    // ... all colors mapped
)
```

#### 2. Text Not Readable

**Problem:** Text color has insufficient contrast.

**Solution:** Use semantic colors instead of custom colors:
```kotlin
// ✅ Good - Uses recommended contrast
Text(
    text = "Hello",
    color = MaterialTheme.colorScheme.onBackground
)

// ❌ Bad - May not have sufficient contrast
Text(
    text = "Hello",
    color = Color(0xFF999999)
)
```

#### 3. Theme Not Applying

**Problem:** Colors don't update when theme changes.

**Solution:** Ensure `ErastosTheme` wraps the entire content:
```kotlin
// ✅ Correct - Theme wraps all content
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ErastosTheme {
                // All content here
                MyApp()
            }
        }
    }
}
```

#### 4. Hardcoded Colors in Previews

**Problem:** Preview shows hardcoded colors instead of theme colors.

**Solution:** Always wrap previews with theme:
```kotlin
@Preview
@Composable
fun MyPreview() {
    ErastosTheme {  // Wrap with theme
        MyComposable()
    }
}
```

---

## AI Agent Rules for Colors & Theming

### RULE 1: No Hardcoded Colors Ever
```kotlin
// ✅ CORRECT
Text(
    text = "Hello",
    color = MaterialTheme.colorScheme.onBackground
)

// ❌ REJECT - Hardcoded color
Text(
    text = "Hello",
    color = Color(0xFF1C1B1F)
)

// ❌ REJECT - Custom color constant
Text(
    text = "Hello",
    color = APP_PRIMARY_COLOR
)
```

### RULE 2: Use Semantic Color Names
```kotlin
// ✅ CORRECT - Clear intent
Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
    )
)

Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.error
    )
)

// ❌ REJECT - Vague color usage
Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.surface
    )
)
```

### RULE 3: Maintain Contrast Standards
```kotlin
// ✅ CORRECT - Sufficient contrast
Text(
    text = "Important",
    color = MaterialTheme.colorScheme.onBackground  // 4.5+ contrast ratio
)

// ❌ REJECT - Insufficient contrast
Text(
    text = "Subtle",
    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
)
```

### RULE 4: Dark Mode Support Required
Every light mode color MUST have a dark mode equivalent:
```kotlin
// ✅ CORRECT
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    // ... all colors
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA78BFA),  // Lighter shade for dark mode
    // ... all colors
)
```

### RULE 5: Use Typography System
```kotlin
// ✅ CORRECT
Text(
    text = "Title",
    style = MaterialTheme.typography.headlineLarge,
    color = MaterialTheme.colorScheme.onBackground
)

// ❌ REJECT - Manual size/weight
Text(
    text = "Title",
    fontSize = 32.sp,
    fontWeight = FontWeight.Bold,
    color = Color(0xFF1C1B1F)
)
```

### RULE 6: Use Dimensions System
```kotlin
// ✅ CORRECT
Box(
    modifier = Modifier.padding(
        horizontal = dimension.screenPaddingHorizontal,
        vertical = dimension.screenPaddingVertical
    )
)

// ❌ REJECT - Hardcoded dp values
Box(
    modifier = Modifier.padding(16.dp)
)
```

## Color & Theming Checklist for AI Agents

**USE THIS FOR EVERY UI CODE SUBMISSION**

### Color Usage
- [ ] No hardcoded Color() values anywhere
- [ ] All text colors from colorScheme
- [ ] All background colors from colorScheme
- [ ] Error states use error color
- [ ] Destructive actions use error color
- [ ] Primary actions use primary color
- [ ] Secondary actions use secondary color

### Typography
- [ ] All text uses MaterialTheme.typography
- [ ] Heading text uses headlineLarge/Medium/Small
- [ ] Body text uses bodyLarge/Medium/Small
- [ ] Label text uses labelLarge/Medium/Small
- [ ] No manual font size specifications

### Spacing & Dimensions
- [ ] All padding uses dimension object
- [ ] Screen padding uses screenPaddingHorizontal/Vertical
- [ ] Component spacing uses componentSpacingSmall/Medium/Large
- [ ] Icon sizes use dimension values
- [ ] No arbitrary dp values (all have semantic names)

### Dark Mode Support
- [ ] Light mode previews included
- [ ] Dark mode previews included
- [ ] Colors tested in both modes
- [ ] Contrast verified in both modes
- [ ] No visual breaking in dark mode

### Theme Application
- [ ] All Composables wrapped with ErastosTheme
- [ ] Preview functions include theme
- [ ] Theme colors accessed at composable start
- [ ] Colors change with system theme
- [ ] No theme overrides without explicit reason

### Accessibility
- [ ] Text contrast ≥ 4.5:1 for body
- [ ] Text contrast ≥ 3:1 for large text
- [ ] Not relying on color alone for information
- [ ] Color-blind friendly palette used
- [ ] Focus indicators visible

---

## References

- [Material Design 3 Color System](https://m3.material.io/styles/color/overview)
- [Jetpack Compose Material 3](https://developer.android.com/jetpack/androidx/releases/compose-material3)
- [WCAG Color Contrast Guidelines](https://www.w3.org/WAI/WCAG21/Understanding/contrast-minimum)
- [Compose Color Documentation](https://developer.android.com/jetpack/compose/designsystems/material)


