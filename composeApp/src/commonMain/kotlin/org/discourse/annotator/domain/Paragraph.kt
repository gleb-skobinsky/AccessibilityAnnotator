package org.discourse.annotator.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import kotlinx.serialization.Serializable
import org.discourse.annotator.common.uuid

@Serializable
data class Paragraph(
    val id: String = uuid(),
    val segments: MutableList<Segment> = mutableListOf()
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
            val accepted = seg.tryAcceptSegment(segment)
            if (accepted) break
        }
    }

    fun findSegmentByIndex(charIndex: Int): Segment? {
        for (seg in segments) {
            seg.traverseFind(charIndex)?.let { return it }
        }
        return null
    }

    private fun sendEntityIdToSegment(segment: Segment, entityId: String) {
        val target = segments.indexOf(segment)
        if (target != 1) {
            val oldEntity = segment.entity
            segments[target] = segment.copy(entity = oldEntity?.copyId(id = entityId))
        } else {
            for (seg in segments) {
                val accepted = seg.overWriteEntityId(segment, entityId)
                if (accepted) break; return
            }
        }
    }

    fun combineTwoSegments(segment1: Segment, segment2: Segment) {
        val (forcing, forced) = listOf(segment1, segment2).sortedBy { it.startInParagraph }
        val forcingId = forcing.entity?.id
        forcingId?.let {
            sendEntityIdToSegment(forced, forcingId)
        }
    }
}

val selectionStyle = SpanStyle(background = Color.Gray.copy(alpha = 0.6f))
