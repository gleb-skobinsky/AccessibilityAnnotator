package org.discourse.annotator.presentation.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.discourse.annotator.common.json.baseJson
import org.discourse.annotator.domain.AnnotationProject

@Composable
actual fun FileSelector(
    isOpen: Boolean,
    onOpen: () -> Unit,
    onNewPath: (String) -> Unit,
    onTextRead: (String) -> Unit
) {
    val fileReadScope = rememberCoroutineScope()
    val contentResolver = LocalContext.current.contentResolver
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            onNewPath(it.toString())
            fileReadScope.launch(Dispatchers.IO) {
                val inStream = contentResolver.openInputStream(uri)
                val text = inStream?.let {
                    inStream.readBytes().toString(Charsets.UTF_8)
                }
                inStream?.close()
                text?.let(onTextRead)
            }
        }
    }
    LaunchedEffect(isOpen) {
        if (isOpen) {
            onOpen()
            fileLauncher.launch(
                arrayOf("text/plain", "application/txt", "application/json")
            )
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
    val context = LocalContext.current
    val fileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.data?.let { uri ->
                onNewPath(uri.toString())
                writeFileToUri(uri, context, project)
            }
        }
    )
    LaunchedEffect(isOpen) {
        if (isOpen) {
            onOpen()
            withContext(Dispatchers.IO) {
                predefinedPath?.let {
                    writeFileToUri(Uri.parse(it), context, project)
                } ?: run {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        type = "application/json"
                        putExtra(Intent.EXTRA_TITLE, "project.json")
                    }
                    fileLauncher.launch(intent)
                }
            }
        }
    }
}

private fun writeFileToUri(uri: Uri, context: Context, project: AnnotationProject) {
    val outStream = context.contentResolver.openOutputStream(uri)
    val projectAsString = baseJson.encodeToString(AnnotationProject.serializer(), project)
    outStream?.write(projectAsString.toByteArray(Charsets.UTF_8))
    outStream?.close()
}