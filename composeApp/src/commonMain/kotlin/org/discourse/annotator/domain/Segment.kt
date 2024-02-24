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
        addStyle(SpanStyle(background = entity.toColor()), startInParagraph, endInParagraph)
        for (seg in nested) {
            with(seg) { addNested() }
        }
    }

    fun tryAcceptSegment(newSegment: Segment): Boolean {
        if (nested.isNotEmpty()) {
            for (seg in nested) {
                val accepted = seg.tryAcceptSegment(newSegment)
                if (accepted) return true
            }
        }
        return directlyAddSegment(newSegment)
    }

    fun overWriteEntityId(segment: Segment, entityId: String): Boolean {
        if (nested.isNotEmpty()) {
            val targetIndex = nested.indexOf(segment)
            if (targetIndex != -1) {
                val oldEntity = segment.entity
                nested[targetIndex] = segment.copy(entity = oldEntity?.copyId(id = entityId))
                return true
            }
        }
        return false
    }

    private fun directlyAddSegment(newSegment: Segment): Boolean {
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
        return false
    }

    fun traverseFind(charIndex: Int): Segment? {
        if (charIndex in startInParagraph..endInParagraph) {
            if (nested.isNotEmpty()) {
                for (seg in nested) {
                    seg.traverseFind(charIndex)?.let { return it }
                }
            }
            return this
        }
        return null
    }

    override fun toString(): String {
        return """
            [
                Segment:
                    rawString: $rawString
                    startInParagraph: $startInParagraph
                    endInParagraph: $endInParagraph
                    id: $id
                    entity: $entity
                    nested: $nested
            ]
        """.trimIndent()
    }
}