package org.discourse.annotator.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.awt.ComposeWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.nio.file.Paths

@Composable
actual fun FileSelector(
    isOpen: Boolean,
    onOpen: () -> Unit,
    onTextRead: (String) -> Unit
) {
    val fileReadScope = rememberCoroutineScope()
    LaunchedEffect(isOpen) {
        if (isOpen) {
            onOpen()
            val fDialog = FileDialog(ComposeWindow(), "Choose a file", FileDialog.LOAD)
            fDialog.isVisible = true
            fDialog.file?.let { file ->
                fDialog.directory?.let { directory ->
                    fileReadScope.launch(Dispatchers.IO) {
                        val inStream = Paths.get(directory, file).toFile().inputStream()
                        val text = inStream.readBytes().toString(Charsets.UTF_8)
                        inStream.close()
                        onTextRead(text)
                    }
                }
            }
        }
    }
}