package org.discourse.annotator.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import kotlinx.serialization.Serializable
import org.discourse.annotator.common.uuid

@Serializable
data class Paragraph(
    val id: String = uuid(),
    val segments: List<Segment> = emptyList()
) {
    fun asText(selection: SelectionRange? = null, index: Int = 0) = buildAnnotatedString {
        for (segment in segments) {
            append(segment.getAnnotated())
        }
        selection?.let {
            val start = selection.startChar
            val end = selection.endChar
            if (!it.isEmpty() && it.paragraph == index) {
                when {
                    start != null && end == null -> addStyle(selectionStyle, start, minOf(start + 1, length))
                    start != null && end != null -> addStyle(selectionStyle, start, end)
                    start == null && end == null -> Unit
                }
            }
        }
    }
}

val selectionStyle = SpanStyle(background = Color.Gray.copy(alpha = 0.6f))
