package org.discourse.annotator.presentation.common

import Platform
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import getPlatform


val ParagraphTextStyle: TextStyle
    @Composable
    get() {
        val color = MaterialTheme.colorScheme.onPrimaryContainer
        val style = when (getPlatform()) {
            Platform.Android -> MaterialTheme.typography.titleLarge.copy(color = color)
            Platform.Desktop -> MaterialTheme.typography.bodyLarge.copy(color = color)
        }
        return style
    }

val ParagraphColor: Color
    @Composable
    get() = MaterialTheme.colorScheme.onPrimaryContainer