import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.discourse.annotator.presentation.common.VerticalSpacer
import org.discourse.annotator.presentation.components.MainViewModel
import org.discourse.annotator.presentation.theme.AnnotatorAppTheme

val AnnotatorButtonColors
    @Composable
    get() = IconButtonDefaults.iconButtonColors(
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )

@Composable
fun App() {
    val viewModel = remember { MainViewModel() }
    val selection by viewModel.selection.collectAsState()
    AnnotatorAppTheme(true) {
        Scaffold(
            topBar = {
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
                    val paragraphText = paragraph.asText()
                    var edited by rememberSaveable { mutableStateOf(false) }
                    var editableField by rememberSaveable { mutableStateOf(paragraphText.text) }
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
                                Text(
                                    text = paragraphText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = color,
                                    modifier = Modifier.weight(1f)
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
                        }
                        HorizontalDivider()
                    }
                }
                item {
                    Row(Modifier.fillParentMaxWidth(), horizontalArrangement = Arrangement.Center) {
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

@Composable
private fun VectorIconButton(imageVector: ImageVector, onClick: () -> Unit = {}) {
    IconButton(
        onClick = onClick,
        colors = AnnotatorButtonColors
    ) {
        Icon(imageVector, null)
    }
}
