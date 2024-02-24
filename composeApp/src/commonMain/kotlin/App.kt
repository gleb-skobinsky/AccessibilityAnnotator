import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import org.discourse.annotator.common.uuid
import org.discourse.annotator.domain.*
import org.discourse.annotator.presentation.common.VerticalSpacer
import org.discourse.annotator.presentation.components.MainViewModel
import org.discourse.annotator.presentation.theme.AnnotatorAppTheme

val AnnotatorButtonColors
    @Composable
    get() = IconButtonDefaults.iconButtonColors(
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )

@Composable
fun App(viewModel: MainViewModel) {
    val selection by viewModel.selection.collectAsState()
    val modal by viewModel.selectionModal.collectAsState()
    AnnotatorAppTheme(true) {
        Scaffold(
            topBar = {
                AnnotatorTopBar()
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) { paddings ->
            val top = paddings.calculateTopPadding()
            val bottom = paddings.calculateBottomPadding()
            LazyColumn {
                item { top.VerticalSpacer() }
                items(
                    viewModel.paragraphs.size,
                    key = { viewModel.paragraphs[it].id },
                    contentType = { viewModel.paragraphs[it] }
                ) { index ->
                    val paragraph = viewModel.paragraphs[index]
                    val paragraphText = paragraph.asText(selection, index)
                    AnnotatedParagraph(paragraphText, selection, viewModel, index, modal)
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
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AnnotatedParagraph(
    paragraphText: AnnotatedString,
    selection: SelectionRange?,
    viewModel: MainViewModel,
    index: Int,
    modal: SelectionModal?
) {
    var editableField by rememberSaveable { mutableStateOf(paragraphText.text) }
    var edited by rememberSaveable { mutableStateOf(false) }
    var textLayoutResult: TextLayoutResult? = remember { null }

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
                DragWrapper(textLayoutResult) {
                    ClickableText(
                        text = paragraphText,
                        style = MaterialTheme.typography.bodyLarge.copy(color = color),
                        onTextLayout = {
                            textLayoutResult = it
                        },
                        softWrap = true,
                    ) {
                        if (paragraphText.isEmpty()) return@ClickableText
                        if (selection == null) {
                            viewModel.setSelection(index, it)
                        } else {
                            viewModel.updateSelection(it + 1)
                        }
                    }
                }
            }
            if (edited) {
                VectorIconButton(Icons.Outlined.Done) {
                    edited = false
                    viewModel.saveParagraph(index, editableField)
                }
            } else {
                VectorIconButton(Icons.Outlined.Edit) { edited = true }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.DragWrapper(
    viewModel: MainViewModel,
    textLayoutResult: TextLayoutResult?,
    content: @Composable BoxScope.() -> Unit
) {
    var dragOffset: Offset? by remember { mutableStateOf(null) }
    Box(
        Modifier.weight(1f).onDrag(
            onDragStart = { offset ->
                dragOffset = offset
                textLayoutResult
                    ?.getOffsetForPosition(offset)
                    ?.let { charOffset ->
                        println(charOffset)
                    }
            },
            onDragEnd = {
                dragOffset = null
            },
            onDragCancel = {
                dragOffset = null
            },
            onDrag = { offset ->
                dragOffset = offset
            }
        )
    ) {
        content()
    }
}

@Composable
private fun DropdownHeader(label: String) {
    DropdownMenuItem(
        text = { Text(label) },
        onClick = {},
        enabled = false
    )
}

@Composable
private fun AnnotatorTopBar() {
    TopAppBar(
        title = { Text("Annotate", color = MaterialTheme.colorScheme.onPrimaryContainer) },
        navigationIcon = {
            VectorIconButton(Icons.Outlined.FolderOpen)
        },
        actions = {
            VectorIconButton(Icons.Outlined.ImportExport)
            VectorIconButton(Icons.Outlined.Save)
        },
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
private fun VectorIconButton(imageVector: ImageVector, onClick: () -> Unit = {}) {
    IconButton(
        onClick = onClick,
        colors = AnnotatorButtonColors
    ) {
        Icon(imageVector, null)
    }
}
