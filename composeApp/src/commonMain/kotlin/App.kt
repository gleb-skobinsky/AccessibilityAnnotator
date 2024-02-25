import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import org.discourse.annotator.presentation.components.AnnotatorTopBar
import org.discourse.annotator.presentation.components.MainViewModel
import org.discourse.annotator.presentation.components.Paragraphs
import org.discourse.annotator.presentation.components.ProjectFileSelector
import org.discourse.annotator.presentation.components.ProjectSaver
import org.discourse.annotator.presentation.components.RawTextFileSelector
import org.discourse.annotator.presentation.theme.AnnotatorAppTheme


@Composable
fun App(viewModel: MainViewModel) {
    AnnotatorAppTheme(true) {
        Scaffold(
            topBar = {
                AnnotatorTopBar(viewModel)
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) { paddings ->
            Paragraphs(
                viewModel = viewModel,
                top = paddings.calculateTopPadding(),
                bottom = paddings.calculateBottomPadding()
            )
        }
    }
    RawTextFileSelector(viewModel)
    ProjectFileSelector(viewModel)
    ProjectSaver(viewModel)
}