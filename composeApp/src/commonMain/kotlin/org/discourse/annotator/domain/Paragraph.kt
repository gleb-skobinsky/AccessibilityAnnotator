package org.discourse.annotator.domain

import androidx.compose.ui.text.buildAnnotatedString
import kotlinx.serialization.Serializable
import org.discourse.annotator.common.uuid

@Serializable
data class Paragraph(
    val id: String = uuid(),
    val segments: List<Segment> = emptyList()
) {
    fun asText() = buildAnnotatedString {
        for (segment in segments) {
            append(segment.getAnnotated())
        }
    }
}
