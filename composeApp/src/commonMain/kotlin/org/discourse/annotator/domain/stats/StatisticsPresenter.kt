package org.discourse.annotator.domain.stats

import org.discourse.annotator.domain.BridgingType
import org.discourse.annotator.domain.DiscourseEntity
import org.discourse.annotator.domain.Segment

interface Presentable {
    fun present(): List<Pair<String, String>>
}

data class CoreferenceStats(
    val totalMentions: Int = 0,
    val totalChains: Int = 0,
    val avgMentionsPerChain: Int = 0,
    val maxMentionsPerChain: Int = 0,
    val grammaticalMentions: Int = 0,
    val lexicalMentions: Int = 0,
    val accMentionsFirstType: Int = 0,
    val accMentionsSecondType: Int = 0,
    val accMentionsThirdType: Int = 0,
    val accMentionsFourthType: Int = 0,
    val accMentionsFifthType: Int = 0
) : Presentable {
    override fun present(): List<Pair<String, String>> {
        val simpleStats = listOf(
            "Total mentions" to totalMentions.toString(),
            "Total chains" to totalChains.toString(),
            "Average mentions per chain" to avgMentionsPerChain.toString(),
            "Maximum mentions per chain" to maxMentionsPerChain.toString(),
            "Grammatical mentions" to grammaticalMentions.toString(),
            "Lexical mentions" to lexicalMentions.toString(),
            "Accessibility level 1" to accMentionsFirstType.toString(),
            "Accessibility level 2" to accMentionsSecondType.toString(),
            "Accessibility level 3" to accMentionsThirdType.toString(),
            "Accessibility level 4" to accMentionsFourthType.toString(),
            "Accessibility level 5" to accMentionsFifthType.toString()
        )
        return simpleStats
    }
}

data class BridgingStats(
    val totalMentions: Int = 0,
    val meronymyOrHolonymy: Int = 0,
    val hyponymyAndHyperonymy: Int = 0,
    val coHyponymy: Int = 0,
    val metalinguisticAnaphora: Int = 0,
    val entityRole: Int = 0,
    val eventTime: Int = 0,
    val sameSemanticField: Int = 0,
    val antonymicPair: Int = 0,
) : Presentable {
    override fun present(): List<Pair<String, String>> {
        return listOf(
            "Total bridging mentions" to totalMentions.toString(),
            "Meronymy/Holonymy" to meronymyOrHolonymy.toString(),
            "Hyponymy/Hyperonymy" to hyponymyAndHyperonymy.toString(),
            "Cohyponymy" to coHyponymy.toString(),
            "Metalinguistic anaphora" to metalinguisticAnaphora.toString(),
            "Entity role" to entityRole.toString(),
            "Event time" to eventTime.toString(),
            "Antonymic pair" to antonymicPair.toString(),
            "Same semantic field" to sameSemanticField.toString()
        )
    }

    fun populate(segments: List<Segment>): BridgingStats {
        return this.copy(
            meronymyOrHolonymy = segments.takeCountForType(BridgingType.MeronymyOrHolonymy),
            hyponymyAndHyperonymy = segments.takeCountForType(BridgingType.HyponymyAndHyperonymy),
            coHyponymy = segments.takeCountForType(BridgingType.CoHyponymy),
            metalinguisticAnaphora = segments.takeCountForType(BridgingType.MetalinguisticAnaphora),
            entityRole = segments.takeCountForType(BridgingType.EntityRole),
            eventTime = segments.takeCountForType(BridgingType.EventTime),
            sameSemanticField = segments.takeCountForType(BridgingType.SameSemanticField),
            antonymicPair = segments.takeCountForType(BridgingType.AntonymicPair)
        )
    }

    private fun List<Segment>.takeCountForType(type: BridgingType) =
        filter { it.br().bridgingType == type }.size

    private fun Segment.br() = this.entity as DiscourseEntity.Bridging
}

data class StatisticsPresenter(
    val coreference: CoreferenceStats = CoreferenceStats(),
    val detailedCoreference: List<CoreferenceStats> = emptyList(),
    val bridging: BridgingStats = BridgingStats()
) : Presentable {
    override fun present(): List<Pair<String, String>> {
        return coreference.present() + bridging.present()
    }
}