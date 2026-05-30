package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Brand Core Color Definitions (Sage and Forest Green, Muted Blush Pink, Grey-blue)
val BrandSage = Color(0xFF8FA88B)          // Calming pastel sage green
val BrandForestGreen = Color(0xFF2A593A)   // Rich, restorative deep forest green
val BrandBlushPink = Color(0xFFDCA8A8)     // Gentle, comforting muted blush pink
val BrandGreyBlue = Color(0xFF6F899A)      // Stylized, modern grey-blue

// Static definitions for compile-time Scheme builder reference in Theme.kt
val StaticCream = Color(0xFFF1F5F0)              // Soothing light sage background tint
val StaticSoftCream = Color(0xFFF7FAF6)          // Very soft sage-tinted white surface
val StaticPaleRose = Color(0xFFD6E4D5)           // Pale pastel sage details (matches sage primary)
val StaticWarmBeige = Color(0xFF98AF96)          // Calming sage green border/accents
val StaticDustyRose = Color(0xFFDCA8A8)          // Sage/Pink primary accent: Comforting blush pink
val StaticLightDustyRose = Color(0xFFEDC9C9)     // Softer blush pink highlight tint
val StaticCocoaBrown = Color(0xFF1E3525)         // Deepest Forest Green for high-contrast readable text
val StaticSoftTaupe = Color(0xFF6A808F)          // Highly sophisticated grey-blue for hints and subtitle details
val StaticCocoaBrownContainer = Color(0xFFDFE6EB) // Muted container background in soft grey-blue

// Dark Mode Palette (Rich forest greens, muted grey-blues, and soothing blush pinks)
val DarkBackground = Color(0xFF101913)      // Cozy deep organic forest-green background
val DarkSurface = Color(0xFF1A261D)         // Forest-green container card surface
val DarkSurfaceVariant = Color(0xFF233327)  // Deeper outline / boundary variant
val WarmWhite = Color(0xFFEEF4F0)           // Sage-white soft high-contrast light text
val MutedRose = Color(0xFFE5B5B5)           // Soft glowing blush pink accent in dark mode
val WarmDarkBrown = Color(0xFF0C150E)       // Darkest charcoal-forest green

// Dynamic properties that automatically resolve based on the active Material 3 Theme Color Scheme!
val Cream: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val SoftCream: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val PaleRose: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primaryContainer

val WarmBeige: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

val DustyRose: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primary

val LightDustyRose: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.secondaryContainer

val CocoaBrown: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onBackground

val SoftTaupe: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.tertiary

val CocoaBrownContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant
