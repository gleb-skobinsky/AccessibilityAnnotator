package org.discourse.annotator.presentation.components

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.discourse.annotator.domain.*
import org.discourse.annotator.presentation.common.BaseViewModel

class MainViewModel : BaseViewModel() {
    val paragraphs = mutableStateListOf<Paragraph>()

    private val _selection = MutableStateFlow<SelectionRange?>(null)
    val selection = _selection.asStateFlow()

    private val _selectionModal = MutableStateFlow<SelectionModal?>(null)
    val selectionModal = _selectionModal.asStateFlow()

    private var currentSegment: Segment? = null

    fun setSelection(paragraph: Int, char: Int) {
        _selection.value = SelectionRange(paragraph, char)
    }

    fun updateSelection(endChar: Int) {
        _selection.update {
            it?.copy(endChar = endChar)
        }
        _selectionModal.value = SelectionModal()
    }

    fun cancelSelection() {
        _selection.value = null
        _selectionModal.value = null
    }

    fun selectType(entity: DiscourseEntity) {
        _selection.value?.let { curSelection ->
            paragraphs.getOrNull(curSelection.paragraph)?.let { curParagraph ->
                val start = curSelection.startChar ?: return
                val end = curSelection.endChar ?: return
                currentSegment = Segment(
                    rawString = curParagraph.asText().text.substring(start, end),
                    startInParagraph = start,
                    endInParagraph = end,
                    entity = entity
                )
                _selectionModal.update {
                    it?.copy(step = SelectionModalSteps.SubtypeSelection(parentType = entity))
                }
            }
        }
    }



    fun selectSubtype(referringType: ReferringType, accessibilityLevel: AccessibilityLevel) {
        val oldSegment = currentSegment
        currentSegment = oldSegment?.copy(
            entity = (oldSegment.entity as? DiscourseEntity.Coreference)?.copy(
                referringType = referringType,
                accessibility = accessibilityLevel
            )
        )
        insertSegment()
    }

    fun selectSubtype(bridgingType: BridgingType) {
        val oldSegment = currentSegment
        currentSegment = oldSegment?.copy(
            entity = (oldSegment.entity as? DiscourseEntity.Bridging)?.copy(
                type = bridgingType
            )
        )
        insertSegment()
    }

    private fun insertSegment() {
        _selectionModal.value = null
        _selection.value?.let { curSelection ->
            paragraphs.getOrNull(curSelection.paragraph)?.let { curParagraph ->
                currentSegment?.let {
                    curParagraph.acceptNewSegment(it)
                }
            }
        }
        _selection.value = null
    }

    fun toProject(): AnnotationProject = AnnotationProject(paragraphs = paragraphs)

    fun addParagraph() {
        paragraphs.add(Paragraph())
    }

    fun saveParagraph(index: Int, newText: String) {
        val oldParagraph = paragraphs.getOrNull(index)
        oldParagraph?.let {
            val oldText = oldParagraph.asText(index = index).text
            if (oldText == newText) return
            val newTextEnd = newText.getEnd()
            val oldTextEnd = oldText.getEnd()
            when {
                oldText < newText -> {
                    val oldSegments = oldParagraph.segments
                    val textDiff = newText.substring(oldTextEnd)
                    paragraphs[index] = oldParagraph.copy(
                        segments = oldSegments + Segment(textDiff, oldTextEnd, newTextEnd)
                    )
                }

                else -> {
                    oldParagraph.segments
                        .withIndex()
                        .firstOrNull { newTextEnd in it.value.startInParagraph..it.value.endInParagraph }
                        ?.let { (segmentIndex, lastSurvivedFragment) ->
                            val lastCharInLastSegment = newTextEnd - lastSurvivedFragment.startInParagraph
                            val newSegment = lastSurvivedFragment.copy(
                                rawString = lastSurvivedFragment.rawString.substring(
                                    startIndex = 0,
                                    endIndex = lastCharInLastSegment
                                )
                            )
                            val newSegments = oldParagraph.segments.subList(0, segmentIndex) + newSegment
                            paragraphs[index] = oldParagraph.copy(segments = newSegments)
                        }
                }
            }
        }
    }
}

fun String.getEnd() = if (isEmpty()) 0 else length