package org.discourse.annotator.domain

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import kotlinx.serialization.Serializable
import org.discourse.annotator.common.uuid


@Serializable
data class Segment(
    val rawString: String,
    val startInParagraph: Int,
    val endInParagraph: Int,
    val id: String = uuid(),
    val entity: DiscourseEntity? = null,
    val nested: MutableList<Segment> = mutableListOf()
) {
    val words: List<String> = rawString.split(" ")

    fun AnnotatedString.Builder.appendAnnotated() {
        pushStyle(SpanStyle(background = noEntityColor))
        append(rawString)
        addNested()
        pop()
    }

    private fun AnnotatedString.Builder.addNested() {
        if (nested.isEmpty()) {
            addStyle(SpanStyle(background = entity.toColor()), startInParagraph, endInParagraph)
        } else {
            for (seg in nested) {
                with(seg) { addNested() }
            }
        }
    }

    fun acceptSegment(newSegment: Segment): Boolean {
        when {
            startInParagraph < newSegment.startInParagraph && newSegment.endInParagraph < endInParagraph -> {
                nested.add(newSegment)
                return true
            }

            startInParagraph == newSegment.startInParagraph && newSegment.endInParagraph < endInParagraph -> {
                nested.add(newSegment)
                return true
            }

            startInParagraph < newSegment.startInParagraph && newSegment.endInParagraph == endInParagraph -> {
                nested.add(newSegment)
                return true
            }
        }
        for (seg in nested) {
            val accepted = seg.acceptSegment(newSegment)
            if (accepted) break; return true
        }
        return false
    }
}