package org.discourse.annotator.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.discourse.annotator.common.uuid
import org.discourse.annotator.domain.AccessibilityLevel
import org.discourse.annotator.domain.BridgingType
import org.discourse.annotator.domain.DiscourseEntity
import org.discourse.annotator.domain.Paragraph
import org.discourse.annotator.domain.ReferringType
import org.discourse.annotator.domain.SelectionModal
import org.discourse.annotator.domain.SelectionModalSteps
import org.discourse.annotator.domain.SelectionRange
import org.discourse.annotator.presentation.common.ParagraphColor
import org.discourse.annotator.presentation.common.ParagraphTextStyle
import org.discourse.annotator.presentation.common.VerticalSpacer


@Composable
fun Paragraphs(
    viewModel: MainViewModel,
    top: Dp,
    bottom: Dp
) {
    val di = LocalDragInfo.current
    val selection by viewModel.selection.collectAsState()
    val modal by viewModel.selectionModal.collectAsState()
    Box {
        LazyColumn {
            item { top.VerticalSpacer() }
            items(
                viewModel.paragraphs.size,
                key = { viewModel.paragraphs[it].id },
                contentType = { viewModel.paragraphs[it] }
            ) { index ->
                val paragraph = viewModel.paragraphs[index]
                AnnotatedParagraph(
                    paragraph = paragraph,
                    selection = selection,
                    viewModel = viewModel,
                    index = index,
                    modal = modal
                )
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    FilledTonalIconButton(
                        onClick = {
                            viewModel.addParagraph()
                        },
                        shape = CircleShape
                    ) {
                        Icon(Icons.Outlined.Add, null)
                    }
                }
            }
            item { bottom.VerticalSpacer() }
        }
        di.draggedSegment?.let { segment ->
            if (di.dragged) {
                with(LocalDensity.current) {
                    Text(
                        text = segment.rawString,
                        modifier = Modifier.offset(
                            x = di.dragOffset.x.toDp(),
                            y = di.dragOffset.y.toDp()
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun AnnotatedParagraph(
    paragraph: Paragraph,
    selection: SelectionRange?,
    viewModel: MainViewModel,
    index: Int,
    modal: SelectionModal?
) {
    val paragraphText = paragraph.asText(selection, index)
    var editableField by rememberSaveable { mutableStateOf(paragraphText.text) }
    var edited by rememberSaveable { mutableStateOf(false) }

    Column {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (edited) {
                BasicTextField(
                    value = editableField,
                    onValueChange = { editableField = it },
                    textStyle = ParagraphTextStyle,
                    cursorBrush = SolidColor(ParagraphColor),
                    modifier = Modifier.weight(1f)
                )
            } else {
                DragClickableText(
                    viewModel = viewModel,
                    paragraphIndex = index,
                    paragraphText = paragraphText,
                    selection = selection
                )
            }
            if (edited) {
                VectorIconButton(Icons.Outlined.Done) {
                    edited = false
                    viewModel.saveParagraph(index, editableField)
                }
            } else {
                VectorIconButton(Icons.Outlined.Edit) { edited = true }
            }
            VectorIconButton(Icons.Filled.Delete) {
                viewModel.deleteParagraph(index)
            }
            modal?.let {
                if (index == selection?.paragraph) {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = {
                            viewModel.cancelSelection()
                        }
                    ) {
                        DropdownHeader(it.step.label)
                        when (val step = it.step) {
                            is SelectionModalSteps.TypeSelection -> {
                                DropdownMenuItem(
                                    text = { Text("Coreference") },
                                    onClick = {
                                        viewModel.selectType(
                                            DiscourseEntity.Coreference(id = uuid())
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Bridging") },
                                    onClick = {
                                        viewModel.selectType(
                                            DiscourseEntity.Bridging(id = uuid())
                                        )
                                    }
                                )
                            }

                            is SelectionModalSteps.SubtypeSelection -> {
                                when (step.parentType) {
                                    is DiscourseEntity.Coreference -> {
                                        var referringType by remember { mutableStateOf(ReferringType.Unknown) }
                                        var accessibility by remember {
                                            mutableStateOf(
                                                AccessibilityLevel.Unknown
                                            )
                                        }
                                        DropdownHeader("Select referring type")
                                        for (ref in ReferringType.entries) {
                                            if (ref == ReferringType.Unknown) continue
                                            DropdownMenuItem(
                                                text = { Text(ref.name) },
                                                onClick = {
                                                    referringType = ref
                                                }
                                            )
                                        }
                                        DropdownHeader("Select accessibility level")
                                        for (acc in AccessibilityLevel.entries) {
                                            if (acc == AccessibilityLevel.Unknown) continue
                                            DropdownMenuItem(
                                                text = { Text(acc.name) },
                                                onClick = {
                                                    accessibility = acc
                                                }
                                            )
                                        }
                                        LaunchedEffect(referringType, accessibility) {
                                            if (
                                                referringType != ReferringType.Unknown
                                                && accessibility != AccessibilityLevel.Unknown
                                            ) {
                                                viewModel.selectSubtype(
                                                    referringType = referringType,
                                                    accessibilityLevel = accessibility
                                                )
                                                referringType = ReferringType.Unknown
                                                accessibility = AccessibilityLevel.Unknown
                                            }
                                        }
                                    }

                                    is DiscourseEntity.Bridging -> {
                                        DropdownHeader("Select bridging type")
                                        for (br in BridgingType.entries) {
                                            if (br == BridgingType.Unknown) continue
                                            DropdownMenuItem(
                                                text = { Text(br.name) },
                                                onClick = {
                                                    viewModel.selectSubtype(br)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider()
    }
}
