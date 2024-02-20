import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import presentation.theme.AnnotatorAppTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "AccessibilityAnnotator",
        state = WindowState(WindowPlacement.Maximized)
    ) {
        AnnotatorAppTheme(false) {
            TopAppBar(
                title = { Text("Annotate") },
                navigationIcon = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(Icons.Outlined.FolderOpen, "Open project")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(Icons.Outlined.Done, "Save project")
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        }
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}