package org.discourse.annotator.presentation.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

actual open class BaseViewModel {
    actual val vmScope = CoroutineScope(Dispatchers.Main)
    actual fun vmLaunch(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ): Job = vmScope.launch(context, block = block)
}