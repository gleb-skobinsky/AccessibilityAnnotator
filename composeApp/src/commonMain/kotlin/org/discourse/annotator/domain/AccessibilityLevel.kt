package org.discourse.annotator.domain

import androidx.compose.ui.graphics.Color

enum class ReferringType {
    Grammatical,
    Lexical,
    Unknown
}

enum class AccessibilityLevel(val label: String, val color: Color) {
    First("Level 1", Color.Blue),
    Second("Level 2", Color.Red),
    Third("Level 3", Color.Black),
    Fourth("Level 4", Color.Gray),
    Fifth("Level 5", Color.Magenta),
    Unknown("", Color.Blue)
}