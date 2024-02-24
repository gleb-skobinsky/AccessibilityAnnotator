package org.discourse.annotator.domain

sealed interface SelectionModalSteps {
    val label: String

    data class TypeSelection(
        override val label: String = "Select entity type",
        val selectedType: DiscourseEntity? = null
    ) : SelectionModalSteps

    data class SubtypeSelection(
        override val label: String = "Select entity subtype",
        val parentType: DiscourseEntity
    ) : SelectionModalSteps
}

data class SelectionModal(
    val step: SelectionModalSteps = SelectionModalSteps.TypeSelection(),
)
