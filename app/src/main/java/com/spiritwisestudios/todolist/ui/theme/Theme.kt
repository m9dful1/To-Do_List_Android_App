package com.spiritwisestudios.todolist.ui.theme

import android.app.Activity // Needed to access the current Activity for setting status bar color
import android.os.Build // To check device SDK version for dynamic color support
import androidx.compose.foundation.isSystemInDarkTheme // Determines if the system is in dark mode
import androidx.compose.material3.MaterialTheme // Provides Material3 theming for Compose
import androidx.compose.material3.darkColorScheme // Creates a color scheme for dark mode
import androidx.compose.material3.dynamicDarkColorScheme // Generates dynamic dark color scheme based on system wallpaper (Android S+)
import androidx.compose.material3.dynamicLightColorScheme // Generates dynamic light color scheme based on system wallpaper (Android S+)
import androidx.compose.material3.lightColorScheme // Creates a color scheme for light mode
import androidx.compose.runtime.Composable // Annotation for composable functions
import androidx.compose.runtime.SideEffect // Runs side effects that need to happen after composition
import androidx.compose.ui.graphics.toArgb // Converts Compose Color to ARGB int for window status bar color
import androidx.compose.ui.platform.LocalContext // Provides the current context in Compose
import androidx.compose.ui.platform.LocalView // Provides the current View in Compose
import androidx.core.view.WindowCompat // Helps control window insets and appearance

// Define the dark color scheme using our predefined colors from Color.kt
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,      // Primary color for dark mode (e.g., accents, buttons)
    secondary = PurpleGrey80, // Secondary color for dark mode (e.g., backgrounds, surfaces)
    tertiary = Pink80         // Tertiary color for dark mode (e.g., highlights)
)

// Define the light color scheme using our predefined colors from Color.kt
private val LightColorScheme = lightColorScheme(
    primary = Purple40,       // Primary color for light mode (e.g., accents, buttons)
    secondary = PurpleGrey40, // Secondary color for light mode (e.g., backgrounds, surfaces)
    tertiary = Pink40         // Tertiary color for light mode (e.g., highlights)
)

/**
 * ToDoListTheme is a custom composable theme for the To-Do List app.
 *
 * @param darkTheme Indicates whether the app should use the dark theme; defaults to system dark mode.
 * @param dynamicColor Enables dynamic color support on Android 12+ (SDK S and above); uses wallpaper-based colors.
 * @param content The composable content that will be themed.
 */
@Composable
fun ToDoListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),  // Defaults to system setting for dark mode
    dynamicColor: Boolean = true,                  // Enable dynamic color if supported by the device
    content: @Composable () -> Unit                // Composable lambda representing the UI content
) {
    // Determine the appropriate color scheme based on dynamic color support and the darkTheme flag.
    val colorScheme = when {
        // If dynamic colors are enabled and the device supports them (Android S and above)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // Use dynamic dark or light color scheme based on darkTheme flag
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // If not using dynamic colors, select our predefined dark or light color scheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Access the current View from Compose
    val view = LocalView.current
    if (!view.isInEditMode) { // Only apply window changes if not in preview mode
        SideEffect {
            // Get the Activity's window to update the status bar color
            val window = (view.context as Activity).window
            // Set the status bar color to match the primary color of our selected color scheme
            window.statusBarColor = colorScheme.primary.toArgb()
            // Adjust the appearance of the status bar content (light/dark icons) based on the theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    // Apply the MaterialTheme with our computed colorScheme, typography, and content
    MaterialTheme(
        colorScheme = colorScheme, // Use the color scheme determined above
        typography = Typography,   // Typography definitions from Type.kt
        content = content          // The UI content to be styled under this theme
    )
}