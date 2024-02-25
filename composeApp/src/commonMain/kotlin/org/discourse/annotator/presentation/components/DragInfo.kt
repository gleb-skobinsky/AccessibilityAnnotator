package org.discourse.annotator.presentation.components

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import org.discourse.annotator.domain.Segment

data class ParagraphUnderDrop(
    val index: Int = 0,
    val position: Offset = Offset.Zero
)

class DragInfo {
    var draggedSegment: Segment? by mutableStateOf(null)
    var dragOffset: Offset by mutableStateOf(Offset.Zero)
    var dragged: Boolean by mutableStateOf(false)
    var paragraphUnderDrop: ParagraphUnderDrop by mutableStateOf(ParagraphUnderDrop())

    fun reset() {
        draggedSegment = null
        dragOffset = Offset.Zero
        dragged = false
        paragraphUnderDrop = ParagraphUnderDrop()
    }
}

val LocalDragInfo = compositionLocalOf { DragInfo() }