import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.discourse.annotator.presentation.common.VerticalSpacer
import org.discourse.annotator.presentation.components.AnnotatedParagraph
import org.discourse.annotator.presentation.components.AnnotatorTopBar
import org.discourse.annotator.presentation.components.MainViewModel
import org.discourse.annotator.presentation.components.ProjectFileSelector
import org.discourse.annotator.presentation.components.ProjectSaver
import org.discourse.annotator.presentation.components.RawTextFileSelector
import org.discourse.annotator.presentation.theme.AnnotatorAppTheme


@Composable
fun App(viewModel: MainViewModel) {
    val selection by viewModel.selection.collectAsState()
    val modal by viewModel.selectionModal.collectAsState()
    AnnotatorAppTheme(true) {
        Scaffold(
            topBar = {
                AnnotatorTopBar(viewModel)
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
    RawTextFileSelector(viewModel)
    ProjectFileSelector(viewModel)
    ProjectSaver(viewModel)
}