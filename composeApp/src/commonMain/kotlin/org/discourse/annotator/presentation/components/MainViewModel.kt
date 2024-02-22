package org.discourse.annotator.presentation.components

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.discourse.annotator.domain.AnnotationProject
import org.discourse.annotator.domain.Paragraph
import org.discourse.annotator.domain.Segment
import org.discourse.annotator.domain.SelectionRange
import org.discourse.annotator.presentation.common.BaseViewModel
import org.discourse.annotator.presentation.common.invoke

class MainViewModel : BaseViewModel by BaseViewModel() {
    val paragraphs = mutableStateListOf<Paragraph>()

    private val _selection = MutableStateFlow<SelectionRange?>(null)
    val selection = _selection.asStateFlow()

    fun setSelection(paragraph: Int, char: Int) {
        _selection.value = SelectionRange(paragraph, char)
    }

    fun updateSelection(endChar: Int) {
        _selection.update {
            it?.copy(endChar = endChar)
        }
    }

    fun toProject(): AnnotationProject = AnnotationProject(paragraphs = paragraphs)

    fun addParagraph() {
        paragraphs.add(Paragraph())
    }

    fun saveParagraph(index: Int, newText: String) {
        val oldParagraph = paragraphs.getOrNull(index)
        oldParagraph?.let {
            val oldText = oldParagraph.asText().text
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

                oldText > newText -> {
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

                else -> Unit
            }
        }
    }
}

fun String.getEnd() = if (isEmpty()) 0 else length