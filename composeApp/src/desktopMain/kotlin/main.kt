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
import org.discourse.annotator.presentation.theme.AnnotatorAppTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "AccessibilityAnnotator",
        state = WindowState(WindowPlacement.Maximized)
    ) {
        App()
    }
}
