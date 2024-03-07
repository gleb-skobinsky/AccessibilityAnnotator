import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.discourse.annotator.presentation.components.*
import org.discourse.annotator.presentation.theme.AnnotatorAppTheme


@Composable
fun App(viewModel: MainViewModel) {
    val sysDark = isSystemInDarkTheme()
    LaunchedEffect(Unit) {
        viewModel.initTheme(sysDark)
    }
    val theme by viewModel.darkTheme.collectAsState()
    AnnotatorAppTheme(theme) {
        Scaffold(
            topBar = {
                AnnotatorTopBar(viewModel, theme)
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
    StatsDialog(viewModel)
}