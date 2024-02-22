package org.discourse.annotator.domain

import androidx.compose.ui.util.fastJoinToString
import kotlinx.serialization.Serializable
import org.discourse.annotator.common.uuid

@Serializable
data class Paragraph(
    val id: String = uuid(),
    val segments: List<Segment> = emptyList(),
    val rawText: String = segments.joinToString(" ") { it.rawString }
)
