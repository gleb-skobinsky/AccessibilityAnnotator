package org.discourse.annotator.domain.stats

interface Presentable {
    fun present(): List<Pair<String, String>>
}

data class CoreferenceStats(
    val totalMentions: Int = 0,
    val totalChains: Int = 0,
    val avgMentionsPerChain: Int = 0,
    val grammaticalMentions: Int = 0,
    val lexicalMentions: Int = 0,
    val accMentionsFirstType: Int = 0,
    val accMentionsSecondType: Int = 0,
    val accMentionsThirdType: Int = 0,
    val accMentionsFourthType: Int = 0,
    val accMentionsFifthType: Int = 0,
    val mentionsPerPart: List<CoreferenceStats> = emptyList()
): Presentable {
    override fun present(): List<Pair<String, String>> {
        val simpleStats = listOf(
            "Total mentions" to totalMentions.toString(),
            "Total chains" to totalChains.toString(),
            "Average mentions per chain" to avgMentionsPerChain.toString(),
            "Grammatical mentions" to grammaticalMentions.toString(),
            "Lexical mentions" to lexicalMentions.toString(),
            "Accessibility level 1" to accMentionsFirstType.toString(),
            "Accessibility level 2" to accMentionsSecondType.toString(),
            "Accessibility level 3" to accMentionsThirdType.toString(),
            "Accessibility level 4" to accMentionsFourthType.toString(),
            "Accessibility level 5" to accMentionsFifthType.toString()
        )
        val combinedStats =
            mentionsPerPart.flatMapIndexed { index: Int, coreferenceStats: CoreferenceStats ->
                listOf("Coreference data with part ${index + 1}" to "") + coreferenceStats.present()
            }
        return simpleStats + combinedStats
    }
}

data class BridgingStats(
    val totalMentions: Int = 0
): Presentable {
    override fun present(): List<Pair<String, String>> {
        return listOf(
            "Total bridging mentions" to totalMentions.toString()
        )
    }
}

data class StatisticsPresenter(
    val coreference: CoreferenceStats = CoreferenceStats(),
    val bridging: BridgingStats = BridgingStats()
): Presentable {
    override fun present(): List<Pair<String, String>> {
        return coreference.present() + bridging.present()
    }
}