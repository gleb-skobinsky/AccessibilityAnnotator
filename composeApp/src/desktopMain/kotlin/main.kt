import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.discourse.annotator.presentation.components.MainViewModel

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Discourse Annotator",
        state = WindowState(WindowPlacement.Maximized)
    ) {
        App(remember { MainViewModel() })
    }
}
