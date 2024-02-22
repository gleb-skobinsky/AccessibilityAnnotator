package org.discourse.annotator.presentation.components

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.discourse.annotator.domain.AnnotationProject
import org.discourse.annotator.domain.Paragraph
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

    fun saveParagraph(index: Int, paragraph: Paragraph) {
        paragraphs[index] = paragraph
    }
}