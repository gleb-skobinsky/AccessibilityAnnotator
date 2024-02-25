package org.discourse.annotator.presentation.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import org.discourse.annotator.domain.Segment
import org.discourse.annotator.domain.SelectionRange

@Composable
fun RowScope.DragClickableText(
    viewModel: MainViewModel,
    paragraphIndex: Int,
    paragraphText: AnnotatedString,
    selection: SelectionRange?
) {
    var dragOffset: Offset by remember { mutableStateOf(Offset.Unspecified) }
    var draggedSegment: Segment? by remember { mutableStateOf(null) }
    var textLayoutResult: TextLayoutResult? = remember { null }
    with(LocalDensity.current) {
        val onEnd = {
            draggedSegment?.let { dragged ->
                if (dragOffset.x < 0 && 0f < dragOffset.y && dragOffset.y < 24.dp.toPx()) {
                    viewModel.deleteSegment(dragged, paragraphIndex)
                } else {
                    textLayoutResult
                        ?.getOffsetForPosition(dragOffset)
                        ?.let { charOffset ->
                            viewModel.findSegmentByIndex(paragraphIndex, charOffset)?.let {
                                viewModel.combineSegmentsIntoChain(paragraphIndex, dragged, it)
                            }
                        }
                }
            }
            dragOffset = Offset.Unspecified
            draggedSegment = null
        }
        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = "Span deletion area",
            modifier = Modifier.padding(end = 16.dp).size(24.dp)
        )
        Box(
            Modifier.weight(1f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            dragOffset = offset
                            textLayoutResult
                                ?.getOffsetForPosition(offset)
                                ?.let { charOffset ->
                                    draggedSegment = viewModel.findSegmentByIndex(paragraphIndex, charOffset)
                                }
                        },
                        onDrag = { _, offset ->
                            dragOffset += offset
                        },
                        onDragCancel = {
                            onEnd()
                        },
                        onDragEnd = {
                            onEnd()
                        }
                    )
                }
        ) {
            ClickableText(
                text = paragraphText,
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                onTextLayout = {
                    textLayoutResult = it
                },
                softWrap = true,
            ) {
                if (paragraphText.isEmpty()) return@ClickableText
                if (selection == null) {
                    viewModel.setSelection(paragraphIndex, it)
                } else {
                    viewModel.updateSelection(it + 1)
                }
            }
            draggedSegment?.let { segment ->
                if (dragOffset != Offset.Unspecified) {
                    Text(
                        text = segment.rawString,
                        modifier = Modifier.offset(dragOffset.x.toDp(), dragOffset.y.toDp())
                    )
                }
            }
        }
    }
}
