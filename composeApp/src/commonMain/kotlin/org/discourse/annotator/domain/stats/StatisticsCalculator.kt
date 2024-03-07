package org.discourse.annotator.domain.stats

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.discourse.annotator.domain.*
import kotlin.math.roundToInt

class StatisticsCalculator(
    private val project: AnnotationProject
) {
    suspend fun calculate() = project.paragraphs.calculate()
}

suspend fun List<Paragraph>.calculate(): StatisticsPresenter? {
    return withContext(Dispatchers.Default) {
        try {
            val allSegments = segments()
            val coreference = coreferenceStats(allSegments)
            val allBridging = allSegments.filter { it.entity is DiscourseEntity.Bridging }
            val brStats = BridgingStats(allBridging.size).populate(allBridging)
            val parts = extractParts()
            val partStats = parts.map { coreferenceStats(it.segments()) }
            StatisticsPresenter(coreference, partStats, brStats)
        } catch (e: Exception) {
            println("Error calculating metrics")
            e.printStackTrace()
            null
        }
    }
}

private fun coreferenceStats(allSegments: MutableList<Segment>): CoreferenceStats {
    var coreference = CoreferenceStats()
    val allCoreference = allSegments.filter { it.entity is DiscourseEntity.Coreference }
    coreference = coreference.copy(totalMentions = allCoreference.size)
    val allChains = allCoreference.groupBy { it.entity?.id }
    coreference = coreference.copy(totalChains = allChains.size)
    val chainSizes = allChains.map { it.value.size }.toIntArray()
    val avgByChain = chainSizes.average().takeIf { !it.isNaN() }?.roundToInt() ?: 0
    val maxInChain = chainSizes.maxOrNull() ?: 0
    coreference = coreference.copy(avgMentionsPerChain = avgByChain, maxMentionsPerChain = maxInChain)
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
    return coreference
}

private fun List<Paragraph>.segments(): MutableList<Segment> {
    val allSegments = mutableListOf<Segment>()
    for (par in this) {
        par.reportSegments(allSegments)
    }
    return allSegments
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

private fun List<Paragraph>.extractParts(): List<List<Paragraph>> {
    val result = mutableListOf<List<Paragraph>>()
    var counter = 0
    var aggregator = emptyList<Paragraph>()

    for (paragraph in this) {
        counter += paragraph.segments.sumOf { it.words.size }
        if (counter > 90) {
            counter = 0
            aggregator = aggregator + listOf(paragraph)
            result.add(aggregator)
            aggregator = emptyList()
        } else {
            aggregator = aggregator + listOf(paragraph)
        }
    }
    for (part in result.map { it.joinToString { it.segments.joinToString { it.rawString } } }) {
        println(part)
    }
    return result
}