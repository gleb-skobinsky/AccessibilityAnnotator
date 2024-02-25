package org.discourse.annotator.presentation.components

import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AnnotatorTopBar(viewModel: MainViewModel) {
    TopAppBar(
        title = { Text("Annotate", color = MaterialTheme.colorScheme.onPrimaryContainer) },
        navigationIcon = {
            VectorIconButton(Icons.Outlined.FolderOpen) {
                viewModel.openProjectSelector()
            }
        },
        actions = {
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
