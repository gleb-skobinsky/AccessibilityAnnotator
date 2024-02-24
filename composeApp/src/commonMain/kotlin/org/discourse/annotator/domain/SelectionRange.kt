package org.discourse.annotator.domain

data class SelectionRange(
    val paragraph: Int,
    val startChar: Int? = null,
    val endChar: Int? = null
) {
    fun isEmpty() = startChar == null && endChar == null
}
