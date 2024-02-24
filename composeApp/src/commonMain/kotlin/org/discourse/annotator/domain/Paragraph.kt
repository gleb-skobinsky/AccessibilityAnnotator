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
            with(segment) {
                appendAnnotated()
            }
        }
        selection?.let {
            if (it.isEmpty() && it.paragraph != index) return@buildAnnotatedString
            val start = selection.startChar
            val end = selection.endChar
            when {
                start != null && end == null -> addStyle(selectionStyle, start, minOf(start + 1, length))
                start != null && end != null -> addStyle(selectionStyle, start, end)
                start == null && end == null -> Unit
            }
        }
    }

    fun acceptNewSegment(segment: Segment) {
        for (seg in segments) {
            val accepted = seg.acceptSegment(segment)
            if (accepted) break
        }
    }
}

val selectionStyle = SpanStyle(background = Color.Gray.copy(alpha = 0.6f))
