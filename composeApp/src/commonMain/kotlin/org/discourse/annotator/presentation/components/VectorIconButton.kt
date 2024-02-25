package org.discourse.annotator.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

val AnnotatorButtonColors
    @Composable
    get() = IconButtonDefaults.iconButtonColors(
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )

@Composable
fun VectorIconButton(imageVector: ImageVector, onClick: () -> Unit = {}) {
    IconButton(
        onClick = onClick,
        colors = AnnotatorButtonColors
    ) {
        Icon(imageVector, null)
    }
}
