import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
                    var edited by remember { mutableStateOf(false) }
                    var editableField by remember { mutableStateOf(paragraph.rawText) }
                    Row {
                        if (edited) {
                            BasicTextField(
                                value = editableField,
                                onValueChange = { editableField = it },
                                textStyle = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(paragraph.rawText, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (edited) {
                            VectorIconButton(Icons.Outlined.Done) {
                                edited = false
                                viewModel.saveParagraph(index, paragraph.copy(rawText = editableField))
                            }
                        } else {
                            VectorIconButton(Icons.Outlined.Edit) { edited = true }
                        }
                    }
                }
                item {
                    Row(Modifier.fillParentMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        FilledTonalIconButton(
                            onClick = {},
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
