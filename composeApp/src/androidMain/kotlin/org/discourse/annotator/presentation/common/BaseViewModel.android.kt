package org.discourse.annotator.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

actual open class BaseViewModel actual constructor() : ViewModel() {
    actual val vmScope: CoroutineScope = viewModelScope
    actual fun vmLaunch(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(context = context, block = block)

}
