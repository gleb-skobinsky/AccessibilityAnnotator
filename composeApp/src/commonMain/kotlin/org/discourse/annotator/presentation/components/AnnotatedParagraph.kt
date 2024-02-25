package org.discourse.annotator.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.discourse.annotator.common.uuid
import org.discourse.annotator.domain.AccessibilityLevel
import org.discourse.annotator.domain.BridgingType
import org.discourse.annotator.domain.DiscourseEntity
import org.discourse.annotator.domain.ReferringType
import org.discourse.annotator.domain.SelectionModal
import org.discourse.annotator.domain.SelectionModalSteps
import org.discourse.annotator.domain.SelectionRange

@Composable
fun AnnotatedParagraph(
    paragraphText: AnnotatedString,
    selection: SelectionRange?,
    viewModel: MainViewModel,
    index: Int,
    modal: SelectionModal?
) {
    var editableField by rememberSaveable { mutableStateOf(paragraphText.text) }
    var edited by rememberSaveable { mutableStateOf(false) }

    Column {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val color = MaterialTheme.colorScheme.onPrimaryContainer
            if (edited) {
                BasicTextField(
                    value = editableField,
                    onValueChange = { editableField = it },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = color),
                    cursorBrush = SolidColor(color),
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
                                    var accessiblity by remember { mutableStateOf(AccessibilityLevel.Unknown) }
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
                                    DropdownHeader("Select accessiblity level")
                                    for (acc in AccessibilityLevel.entries) {
                                        if (acc == AccessibilityLevel.Unknown) continue
                                        DropdownMenuItem(
                                            text = { Text(acc.name) },
                                            onClick = {
                                                accessiblity = acc
                                            }
                                        )
                                    }
                                    LaunchedEffect(referringType, accessiblity) {
                                        if (
                                            referringType != ReferringType.Unknown
                                            && accessiblity != AccessibilityLevel.Unknown
                                        ) {
                                            viewModel.selectSubtype(
                                                referringType = referringType,
                                                accessibilityLevel = accessiblity
                                            )
                                            referringType = ReferringType.Unknown
                                            accessiblity = AccessibilityLevel.Unknown
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
        HorizontalDivider()
    }
}
