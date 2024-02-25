package org.discourse.annotator.presentation.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import org.discourse.annotator.domain.SelectionRange
import org.discourse.annotator.presentation.common.ParagraphTextStyle

@Composable
fun RowScope.DragClickableText(
    viewModel: MainViewModel,
    paragraphIndex: Int,
    paragraphText: AnnotatedString,
    selection: SelectionRange?
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    var currentHeight by remember { mutableStateOf(0) }
    var textLayoutResult: TextLayoutResult? = remember { null }
    val di = LocalDragInfo.current
    val dragOffset = di.dragOffset
    Box(
        Modifier
            .weight(1f)
            .onGloballyPositioned {
                val offset = it.localToWindow(Offset.Zero)
                currentPosition = offset
                currentHeight = it.size.height
                if (it.boundsInWindow().contains(dragOffset)) {
                    di.paragraphUnderDrop = ParagraphUnderDrop(paragraphIndex, offset)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        di.dragged = true
                        di.dragOffset = currentPosition + offset
                        textLayoutResult
                            ?.getOffsetForPosition(offset)
                            ?.let { charOffset ->
                                di.draggedSegment =
                                    viewModel.findSegmentByIndex(paragraphIndex, charOffset)
                            }
                    },
                    onDrag = { _, offset ->
                        di.dragOffset += offset
                    },
                    onDragCancel = {
                        handleDragEnd(
                            di = di,
                            textLayoutResult = textLayoutResult,
                            viewModel = viewModel,
                            paragraphIndex = paragraphIndex
                        )
                    },
                    onDragEnd = {
                        handleDragEnd(
                            di = di,
                            textLayoutResult = textLayoutResult,
                            viewModel = viewModel,
                            paragraphIndex = paragraphIndex
                        )
                    }
                )
            }
    ) {
        ClickableText(
            text = paragraphText,
            style = ParagraphTextStyle,
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
    }
}

private fun handleDragEnd(
    di: DragInfo,
    paragraphIndex: Int,
    textLayoutResult: TextLayoutResult?,
    viewModel: MainViewModel,
) {
    val dropPar = di.paragraphUnderDrop
    di.draggedSegment?.let { dragged ->
        textLayoutResult
            ?.getOffsetForPosition(di.dragOffset - dropPar.position)
            ?.let { charOffset ->
                val segmentUnderDrag =
                    viewModel.findSegmentByIndex(dropPar.index, charOffset)
                segmentUnderDrag?.let { underDrag ->
                    when {
                        dropPar.index < paragraphIndex -> {
                            viewModel.combineSegmentsIntoChain(
                                paragraph = paragraphIndex,
                                sourceSegment = underDrag,
                                targetSegment = dragged
                            )
                        }

                        dropPar.index > paragraphIndex -> {
                            viewModel.combineSegmentsIntoChain(
                                paragraph = dropPar.index,
                                sourceSegment = dragged,
                                targetSegment = underDrag
                            )
                        }

                        else -> {
                            val (source, target) = listOf(
                                underDrag,
                                dragged
                            ).sortedBy { it.startInParagraph }
                            viewModel.combineSegmentsIntoChain(
                                paragraph = paragraphIndex,
                                sourceSegment = source,
                                targetSegment = target
                            )
                        }
                    }
                }
            }
    }
    di.reset()
}

