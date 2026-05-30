package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light Blossom: Primary is Muted Blush Pink (paired with Sage & Grey-Blue accents)
private val BlossomLightScheme = lightColorScheme(
    primary = StaticDustyRose,                    // Muted Blush Pink
    onPrimary = Color.White,
    primaryContainer = StaticPaleRose,            // Soothing Sage highlight
    onPrimaryContainer = StaticCocoaBrown,        // Forest Green text
    secondary = StaticWarmBeige,                  // Soft Sage Green
    onSecondary = StaticCocoaBrown,
    secondaryContainer = StaticCocoaBrownContainer, // Soft Grey-Blue container backing
    onSecondaryContainer = StaticCocoaBrown,
    tertiary = StaticSoftTaupe,                   // Sophisticated Grey-Blue
    onTertiary = Color.White,
    background = StaticCream,                     // Creamy Sage container/background
    onBackground = StaticCocoaBrown,
    surface = StaticSoftCream,
    onSurface = StaticCocoaBrown,
    surfaceVariant = StaticCocoaBrownContainer,
    onSurfaceVariant = StaticCocoaBrown,
    outline = StaticWarmBeige
)

// Light Sand: Redefined as "Grey-Blue theme"
private val SandLightScheme = lightColorScheme(
    primary = StaticSoftTaupe,                    // Highly sophisticated grey-blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2E7EB),   // Tinted grey-blue pale container
    onPrimaryContainer = StaticCocoaBrown,
    secondary = StaticWarmBeige,                  // Soft Sage Green
    onSecondary = StaticCocoaBrown,
    secondaryContainer = StaticPaleRose,
    onSecondaryContainer = StaticCocoaBrown,
    tertiary = StaticDustyRose,                   // Muted Blush Pink
    onTertiary = Color.White,
    background = Color(0xFFEFF3F6),         // Light grey-blue calming background
    onBackground = StaticCocoaBrown,
    surface = Color(0xFFF7FAF9),            // Calming clean surface
    onSurface = StaticCocoaBrown,
    surfaceVariant = Color(0xFFE4E9ED),
    onSurfaceVariant = StaticCocoaBrown,
    outline = StaticSoftTaupe
)

// Light Forest: Redefined as "Sage & Forest Green theme"
private val ForestLightScheme = lightColorScheme(
    primary = BrandForestGreen,             // Pure restorative Forest Green
    onPrimary = Color.White,
    primaryContainer = StaticPaleRose,            // Light Sage highlight
    onPrimaryContainer = StaticCocoaBrown,
    secondary = StaticWarmBeige,                  // Soft Sage Green
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2EDE2), // Pastel Forest tint
    onSecondaryContainer = StaticCocoaBrown,
    tertiary = StaticDustyRose,                   // Muted Blush Pink
    onTertiary = Color.White,
    background = StaticCream,                     // Sage-tinted off-white background
    onBackground = StaticCocoaBrown,
    surface = StaticSoftCream,
    onSurface = StaticCocoaBrown,
    surfaceVariant = StaticCocoaBrownContainer,
    onSurfaceVariant = StaticCocoaBrown,
    outline = StaticWarmBeige
)

// Full Comforting Dark Wellness Theme
private val DarkColorScheme = darkColorScheme(
    primary = MutedRose,                    // Comforting soft blush pink accent
    onPrimary = WarmDarkBrown,              // Deep dark text
    primaryContainer = DarkSurfaceVariant,  // Cozy dark forest-green surface details
    onPrimaryContainer = WarmWhite,
    secondary = BrandSage,                  // Soft, soothing sage green
    onSecondary = WarmDarkBrown,
    secondaryContainer = DarkSurface,
    onSecondaryContainer = WarmWhite,
    tertiary = BrandGreyBlue,               // Highly elegant grey-blue
    onTertiary = WarmDarkBrown,
    background = DarkBackground,             // Cozy deep forest green background
    onBackground = WarmWhite,               // Sage-white readable text
    surface = DarkSurface,                  // Deep forest containers
    onSurface = WarmWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = WarmWhite,
    outline = BrandSage
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Follow system dark theme dynamically by default!
    themeName: String = "Blossom",
    content: @Composable () -> Unit
) {
    // Treat "Dark" theme as an absolute Dark Mode selector from Settings.
    // If they choose specialized themes like "Sand" (Blue) or "Forest" (Sage), respect those light palettes directly.
    val isDark = themeName == "Dark"
    val colors = if (isDark) {
        DarkColorScheme
    } else {
        when (themeName) {
            "Sand" -> SandLightScheme       // Beautiful Grey-Blue Theme
            "Forest" -> ForestLightScheme   // Beautiful Sage & Forest Green Theme
            else -> BlossomLightScheme      // Beautiful Muted Blush Pink Theme
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
