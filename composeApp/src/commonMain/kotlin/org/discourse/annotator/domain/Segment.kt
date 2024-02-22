package org.discourse.annotator.domain

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import kotlinx.serialization.Serializable
import org.discourse.annotator.common.uuid


@Serializable
data class Segment(
    val rawString: String,
    val startInParagraph: Int,
    val endInParagraph: Int,
    val id: String = uuid(),
    val entity: DiscourseEntity? = null,
    val words: List<String> = emptyList(),
    val nested: List<Segment> = emptyList()
) {
    fun getAnnotated(): AnnotatedString = buildAnnotatedString {
        if (nested.isEmpty()) {
            pushStyle(SpanStyle(background = entity.toColor()))
            append(rawString.trim())
            pop()
            append(" ")
        } else {
            for (seg in nested) {
                append(seg.getAnnotated())
            }
        }
    }
}