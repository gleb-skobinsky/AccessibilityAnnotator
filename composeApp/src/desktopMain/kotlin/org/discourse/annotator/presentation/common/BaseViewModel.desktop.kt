package org.discourse.annotator.presentation.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DesktopBaseViewModelImpl: BaseViewModel {
    override val vmScope = CoroutineScope(Dispatchers.Main)
    override fun vmLaunch(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ): Job = vmScope.launch(context, block = block)
}

actual operator fun BaseViewModel.Companion.invoke(): BaseViewModel = DesktopBaseViewModelImpl()