package org.discourse.annotator.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
expect fun FileSelector(isOpen: Boolean, onOpen: () -> Unit, onTextRead: (String) -> Unit)

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