package org.discourse.annotator.domain

import kotlinx.serialization.Serializable

@Serializable
sealed interface DiscourseEntity {
    val id: String

    data class Coreference(
        override val id: String,
        val accessibility: AccessibilityLevel
    ): DiscourseEntity

    data class Bridging(
        override val id: String
    ): DiscourseEntity


}