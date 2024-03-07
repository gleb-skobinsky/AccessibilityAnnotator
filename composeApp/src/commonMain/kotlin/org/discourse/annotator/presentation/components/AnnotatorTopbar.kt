package org.discourse.annotator.presentation.components

import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.discourse.annotator.presentation.common.HorizontalSpacer

@Composable
fun AnnotatorTopBar(viewModel: MainViewModel, darkTheme: Boolean) {
    TopAppBar(
        title = { Text("Annotate", color = MaterialTheme.colorScheme.onPrimaryContainer) },
        navigationIcon = {
            VectorIconButton(Icons.Outlined.FolderOpen) {
                viewModel.openProjectSelector()
            }
        },
        actions = {
            Switch(darkTheme, { viewModel.toggleTheme() })
            12.dp.HorizontalSpacer()
            VectorIconButton(Icons.Outlined.Info) {
                viewModel.showStats()
            }
            VectorIconButton(Icons.Outlined.ImportExport) {
                viewModel.openRawTextSelector()
            }
            VectorIconButton(Icons.Outlined.Save) {
                viewModel.openProjectSaver()
            }
        },
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
}
