package org.discourse.annotator.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AndroidBaseViewModelImpl : ViewModel(), BaseViewModel {
    override val vmScope = viewModelScope
    override fun vmLaunch(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ): Job = vmScope.launch(context, block = block)
}

actual operator fun BaseViewModel.Companion.invoke(): BaseViewModel = AndroidBaseViewModelImpl()