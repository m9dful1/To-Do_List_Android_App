package com.spiritwisestudios.todolist.ui.theme  // Package declaration: defines the namespace for theme-related files

import androidx.compose.material3.Typography          // Import Material3's Typography class for defining text styles
import androidx.compose.ui.text.TextStyle             // Import TextStyle to specify text appearance (e.g., font size, weight)
import androidx.compose.ui.text.font.FontFamily       // Import FontFamily to set the font family (e.g., default, serif, sans-serif)
import androidx.compose.ui.text.font.FontWeight       // Import FontWeight to set the weight of the font (e.g., Normal, Bold)
import androidx.compose.ui.unit.sp                    // Import 'sp' unit for scalable pixel measurements for text

// Define a set of Material typography styles to be used throughout the app.
// This Typography instance configures various text styles, which can be referenced by the app's UI.
val Typography = Typography(
    // Define the style for bodyLarge text, which is used for prominent text elements.
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,             // Use the default system font family
        fontWeight = FontWeight.Normal,              // Set the font weight to Normal (not bold)
        fontSize = 16.sp,                            // Set the font size to 16 scalable pixels
        lineHeight = 24.sp,                          // Set the line height to 24 scalable pixels to make sure there is enough spacing between lines
        letterSpacing = 0.5.sp                       // Set letter spacing to 0.5 scalable pixels for improved readability
    )
)