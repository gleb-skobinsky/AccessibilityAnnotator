package org.discourse.annotator.presentation.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
actual fun FileSelector(
    isOpen: Boolean,
    onOpen: () -> Unit,
    onTextRead: (String) -> Unit
) {
    val fileReadScope = rememberCoroutineScope()
    val contentResolver = LocalContext.current.contentResolver
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
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