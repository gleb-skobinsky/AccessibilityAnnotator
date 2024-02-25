package org.discourse.annotator.domain.stats

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.discourse.annotator.domain.AccessibilityLevel
import org.discourse.annotator.domain.AnnotationProject
import org.discourse.annotator.domain.DiscourseEntity
import org.discourse.annotator.domain.Paragraph
import org.discourse.annotator.domain.ReferringType
import org.discourse.annotator.domain.Segment

class StatisticsCalculator(
    private val project: AnnotationProject
) {
    suspend fun calculate(): StatisticsPresenter? {
        return withContext(Dispatchers.Default) {
            try {
                var coreference = CoreferenceStats()
                val allSegments = mutableListOf<Segment>()
                for (par in project.paragraphs) {
                    par.reportSegments(allSegments)
                }
                val allCoreference = allSegments.filter { it.entity is DiscourseEntity.Coreference }
                coreference = coreference.copy(totalMentions = allCoreference.size)
                val allChains = allCoreference.groupBy { it.entity?.id }
                coreference = coreference.copy(totalChains = allChains.size)
                val avgByChain = allChains.map { it.value.size }.average().toInt()
                coreference = coreference.copy(avgMentionsPerChain = avgByChain)
                val allGrammatical =
                    allCoreference.filter {
                        (it.entity as? DiscourseEntity.Coreference)?.referringType == ReferringType.Grammatical
                    }
                val allLexical =
                    allCoreference.filter {
                        (it.entity as? DiscourseEntity.Coreference)?.referringType == ReferringType.Lexical
                    }
                coreference = coreference.copy(
                    grammaticalMentions = allGrammatical.size,
                    lexicalMentions = allLexical.size
                )
                val allFirst = allCoreference.filter {
                    (it.entity as? DiscourseEntity.Coreference)?.accessibility == AccessibilityLevel.First
                }
                val allSecond = allCoreference.filter {
                    (it.entity as? DiscourseEntity.Coreference)?.accessibility == AccessibilityLevel.Second
                }
                val allThird = allCoreference.filter {
                    (it.entity as? DiscourseEntity.Coreference)?.accessibility == AccessibilityLevel.Third
                }
                val allFourth = allCoreference.filter {
                    (it.entity as? DiscourseEntity.Coreference)?.accessibility == AccessibilityLevel.Fourth
                }
                val allFifth = allCoreference.filter {
                    (it.entity as? DiscourseEntity.Coreference)?.accessibility == AccessibilityLevel.Fifth
                }
                coreference = coreference.copy(
                    accMentionsFirstType = allFirst.size,
                    accMentionsSecondType = allSecond.size,
                    accMentionsThirdType = allThird.size,
                    accMentionsFourthType = allFourth.size,
                    accMentionsFifthType = allFifth.size
                )
                StatisticsPresenter(coreference)
            } catch (e: Exception) {
                println("Error calculating metrics")
                null
            }
        }
    }
}

fun Paragraph.reportSegments(toList: MutableList<Segment>) {
    for (seg in segments) {
        seg.reportSegments(toList)
    }
}

fun Segment.reportSegments(toList: MutableList<Segment>) {
    if (nested.isEmpty() && entity != null) {
        toList += this
    } else {
        for (seg in nested) {
            seg.reportSegments(toList)
        }
    }
}