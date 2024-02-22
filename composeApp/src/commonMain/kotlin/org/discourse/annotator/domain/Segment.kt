package org.discourse.annotator.domain

import kotlinx.serialization.Serializable
import org.discourse.annotator.common.uuid
import java.util.UUID

@Serializable
data class Segment(
    val id: String = uuid(),
    val entity: DiscourseEntity? = null,
    val rawString: String,
    val words: List<String> = emptyList()
)
