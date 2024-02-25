package org.discourse.annotator.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.discourse.annotator.domain.AnnotationProject

@Composable
expect fun FileSelector(
    isOpen: Boolean,
    onOpen: () -> Unit,
    onTextRead: (String) -> Unit
)

@Composable
expect fun FileSaver(
    isOpen: Boolean,
    predefinedPath: String?,
    onOpen: () -> Unit,
    project: AnnotationProject
)

@Composable
fun RawTextFileSelector(viewModel: MainViewModel) {
    val isOpen by viewModel.rawTextSelectorOpen.collectAsState()
    FileSelector(isOpen, { viewModel.closeRawTextSelector() }) {
        viewModel.acceptParagraphs(it)
    }
}

@Composable
fun ProjectFileSelector(viewModel: MainViewModel) {
    val isOpen by viewModel.projectSelectorOpen.collectAsState()
    FileSelector(isOpen, { viewModel.closeProjectSelector() }) {
        viewModel.acceptProject(it)
    }
}

@Composable
fun ProjectSaver(viewModel: MainViewModel) {
    val saverData by viewModel.projectSaverData.collectAsState()
    FileSaver(
        isOpen = saverData.isOpen,
        predefinedPath = saverData.filePath,
        onOpen = { viewModel.closeProjectSaver() },
        project = viewModel.toProject()
    )
}