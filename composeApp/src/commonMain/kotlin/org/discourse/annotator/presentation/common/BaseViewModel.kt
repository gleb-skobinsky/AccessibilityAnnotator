package org.discourse.annotator.presentation.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface BaseViewModel {
    val vmScope: CoroutineScope

    fun vmLaunch(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit): Job

    companion object
}

expect operator fun BaseViewModel.Companion.invoke(): BaseViewModel