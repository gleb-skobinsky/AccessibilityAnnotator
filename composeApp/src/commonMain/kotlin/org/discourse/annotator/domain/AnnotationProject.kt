package org.discourse.annotator.domain

import kotlinx.serialization.Serializable

@Serializable
data class AnnotationProject(
    val paragraphs: List<Paragraph> = emptyList()
)