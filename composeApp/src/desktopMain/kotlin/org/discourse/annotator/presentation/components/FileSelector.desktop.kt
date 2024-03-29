package org.discourse.annotator.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.awt.ComposeWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import org.discourse.annotator.common.json.baseJson
import org.discourse.annotator.domain.AnnotationProject
import java.awt.FileDialog
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.pathString

@Composable
actual fun FileSelector(
    isOpen: Boolean,
    onOpen: () -> Unit,
    onNewPath: (String) -> Unit,
    onTextRead: (String) -> Unit
) {
    LaunchedEffect(isOpen) {
        if (isOpen) {
            onOpen()
            val fDialog = FileDialog(ComposeWindow(), "Choose a file", FileDialog.LOAD)
            fDialog.isVisible = true
            fDialog.file?.let { file ->
                fDialog.directory?.let { directory ->
                    launch(Dispatchers.IO) {
                        val path = Paths.get(directory, file)
                        onNewPath(path.pathString)
                        val inStream = path.toFile().inputStream()
                        val text = inStream.readBytes().toString(Charsets.UTF_8)
                        inStream.close()
                        onTextRead(text)
                    }
                }
            }
        }
    }
}

@Composable
actual fun FileSaver(
    isOpen: Boolean,
    predefinedPath: String?,
    onOpen: () -> Unit,
    onNewPath: (String) -> Unit,
    project: AnnotationProject
) {
    LaunchedEffect(isOpen) {
        predefinedPath?.let {
            withContext(Dispatchers.IO) {
                File(it).writeText(baseJson.encodeToString<AnnotationProject>(project))
            }
            onOpen()
        } ?: run {
            if (isOpen) {
                onOpen()
                val fDialog = FileDialog(ComposeWindow(), "Save the project", FileDialog.SAVE)
                fDialog.isVisible = true
                fDialog.file?.let { file ->
                    fDialog.directory?.let { directory ->
                        withContext(Dispatchers.IO) {
                            val path = Paths.get(directory, file)
                            onNewPath(path.pathString)
                            val textToWrite = baseJson.encodeToString(
                                AnnotationProject.serializer(),
                                project
                            )
                            path.toFile().writeText(textToWrite)
                        }
                    }
                }
            }
        }
    }
}