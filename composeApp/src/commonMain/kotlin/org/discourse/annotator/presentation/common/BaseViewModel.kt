package org.discourse.annotator.presentation.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

expect open class BaseViewModel() {
    val vmScope: CoroutineScope

    fun vmLaunch(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit): Job
}
