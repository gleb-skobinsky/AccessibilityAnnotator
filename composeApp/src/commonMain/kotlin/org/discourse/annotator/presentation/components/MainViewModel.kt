package org.discourse.annotator.presentation.components

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerializationException
import org.discourse.annotator.common.json.baseJson
import org.discourse.annotator.common.uuid
import org.discourse.annotator.domain.*
import org.discourse.annotator.domain.stats.StatisticsCalculator
import org.discourse.annotator.domain.stats.StatisticsPresenter
import org.discourse.annotator.presentation.common.BaseViewModel

data class ProjectSavingData(
    val isOpen: Boolean = false,
    val filePath: String? = null
)

class MainViewModel : BaseViewModel() {
    private val _darkTheme = MutableStateFlow(false)
    val darkTheme = _darkTheme.asStateFlow()

    fun initTheme(value: Boolean) {
        _darkTheme.value = value
    }

    fun toggleTheme() {
        _darkTheme.update { !it }
    }

    private val _rawTextSelectorOpen = MutableStateFlow(false)
    val rawTextSelectorOpen = _rawTextSelectorOpen.asStateFlow()

    fun openRawTextSelector() {
        _rawTextSelectorOpen.value = true
    }

    fun closeRawTextSelector() {
        _rawTextSelectorOpen.value = false
    }

    private val _projectSelectorOpen = MutableStateFlow(false)
    val projectSelectorOpen = _projectSelectorOpen.asStateFlow()

    fun openProjectSelector() {
        _projectSelectorOpen.value = true
    }

    fun closeProjectSelector() {
        _projectSelectorOpen.value = false
    }

    private val _projectSaverData = MutableStateFlow(ProjectSavingData())
    val projectSaverData = _projectSaverData.asStateFlow()

    fun openProjectSaver() {
        _projectSaverData.update { it.copy(isOpen = true) }
    }

    fun receiveNewProjectPath(path: String) {
        _projectSaverData.update { it.copy(filePath = path) }
    }

    fun closeProjectSaver() {
        _projectSaverData.update { it.copy(isOpen = false) }
    }

    val paragraphs = mutableStateListOf<Paragraph>()

    private val _selection = MutableStateFlow<SelectionRange?>(null)
    val selection = _selection.asStateFlow()

    private val _selectionModal = MutableStateFlow<SelectionModal?>(null)
    val selectionModal = _selectionModal.asStateFlow()

    private var currentSegment: Segment? = null

    fun setSelection(paragraph: Int, char: Int) {
        _selection.value = SelectionRange(paragraph, char)
    }

    fun acceptParagraphs(content: String) {
        vmLaunch {
            val pars = content
                .split("\n")
                .filter { it.isNotBlank() }
                .map { it.toParagraph() }
            paragraphs.clear()
            paragraphs.addAll(pars)
        }
    }

    fun acceptProject(content: String) {
        vmLaunch {
            try {
                val project = baseJson.decodeFromString(AnnotationProject.serializer(), content)
                paragraphs.clear()
                paragraphs.addAll(project.paragraphs)
            } catch (e: SerializationException) {
                println("Failed to read project")
            }
        }
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
            withParagraph(curSelection.paragraph) { curParagraph ->
                val start = curSelection.startChar ?: return@withParagraph
                val end = curSelection.endChar ?: return@withParagraph
                val (actualStart, actualEnd) = listOf(start, end).sorted()
                currentSegment = Segment(
                    rawString = curParagraph.asText().text.substring(actualStart, actualEnd),
                    startInParagraph = actualStart,
                    endInParagraph = actualEnd,
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
                bridgingType = bridgingType
            )
        )
        insertSegment()
    }

    private fun insertSegment() {
        _selectionModal.value = null
        withParagraph { curParagraph ->
            currentSegment?.let {
                curParagraph.acceptNewSegment(it)
            }
        }
        currentSegment = null
        _selection.value = null
    }

    private fun withParagraph(
        paragraph: Int = _selection.value?.paragraph ?: 0,
        block: (Paragraph) -> Unit
    ) {
        paragraphs.getOrNull(paragraph)?.let(block)
    }

    fun toProject(): AnnotationProject = AnnotationProject(
        paragraphs = paragraphs
    )

    fun addParagraph() {
        paragraphs.add(Paragraph())
    }

    fun saveParagraph(index: Int, newText: String) {
        withParagraph(index) { oldParagraph ->
            val oldText = oldParagraph.asText(index = index).text
            if (oldText == newText) return@withParagraph
            val newTextEnd = newText.getEnd()
            val oldTextEnd = oldText.getEnd()
            when {
                oldText < newText -> {
                    val oldSegments = oldParagraph.segments
                    val textDiff = newText.substring(oldTextEnd)
                    paragraphs[index] = oldParagraph.copy(
                        segments = (oldSegments + Segment(
                            textDiff,
                            oldTextEnd,
                            newTextEnd
                        )).toMutableList()
                    )
                }

                else -> {
                    oldParagraph.segments
                        .withIndex()
                        .firstOrNull { newTextEnd in it.value.startInParagraph..it.value.endInParagraph }
                        ?.let { (segmentIndex, lastSurvivedFragment) ->
                            val lastCharInLastSegment =
                                newTextEnd - lastSurvivedFragment.startInParagraph
                            val newSegment = lastSurvivedFragment.copy(
                                rawString = lastSurvivedFragment.rawString.substring(
                                    startIndex = 0,
                                    endIndex = lastCharInLastSegment
                                )
                            )
                            val newSegments =
                                oldParagraph.segments.subList(0, segmentIndex) + newSegment
                            paragraphs[index] =
                                oldParagraph.copy(segments = newSegments.toMutableList())
                        }
                }
            }
        }
    }

    fun findSegmentByIndex(paragraphIndex: Int, charIndex: Int): Segment? {
        val paragraph = paragraphs.getOrNull(paragraphIndex)
        return paragraph?.findSegmentByIndex(charIndex)
    }

    fun combineSegmentsIntoChain(paragraph: Int, sourceSegment: Segment, targetSegment: Segment) {
        withParagraph(paragraph) { par ->
            par.combineTwoSegments(sourceSegment, targetSegment)
            paragraphs[paragraph] = par.copy(id = uuid())
        }
    }

    fun deleteSegment(segment: Segment, paragraph: Int) {
        withParagraph(paragraph) { par ->
            par.deleteSegment(segment)
            paragraphs[paragraph] = par.copy(id = uuid())
        }
    }

    fun deleteParagraph(atIndex: Int) {
        paragraphs.removeAt(atIndex)
    }

    private val _statsPresenter = MutableStateFlow<StatisticsPresenter?>(null)
    val statsPresenter = _statsPresenter.asStateFlow()

    fun showStats() {
        vmLaunch {
            _statsPresenter.value = StatisticsCalculator(toProject()).calculate()
        }
    }

    fun closeStats() {
        _statsPresenter.value = null
    }
}

fun String.getEnd() = if (isEmpty()) 0 else length

fun String.toParagraph() = Paragraph(uuid(), mutableListOf(Segment(rawString = this)))