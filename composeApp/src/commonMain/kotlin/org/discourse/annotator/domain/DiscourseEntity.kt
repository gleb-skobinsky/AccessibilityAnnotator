package org.discourse.annotator.domain

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
enum class BridgingType {
    Meronymy,
    MetalinguisticAnaphora,
    SetSubset,
    EntityRole,
    Unknown
}

@Serializable
sealed interface DiscourseEntity {
    val id: String

    @Serializable
    data class Coreference(
        override val id: String,
        val accessibility: AccessibilityLevel = AccessibilityLevel.Unknown,
        val referringType: ReferringType = ReferringType.Unknown
    ) : DiscourseEntity

    @Serializable
    data class Bridging(
        override val id: String,
        val bridgingType: BridgingType = BridgingType.Unknown
    ) : DiscourseEntity

    fun copyId(id: String): DiscourseEntity {
        return when (this) {
            is Coreference -> copy(id = id)
            is Bridging -> copy(id = id)
        }
    }
}

val noEntityColor = Color.Gray.copy(alpha = 0.3f)

private fun String.toColor(saturation: Float = 0.5f, lightness: Float = 0.5f): Color {
    val hue = fold(0) { acc, char -> char.code + acc * 37 } % 360
    return Color.hsl(hue.absoluteValue.toFloat(), saturation, lightness)
}

fun DiscourseEntity?.toColor() = when (this) {
    null -> noEntityColor
    else -> id.toColor()
}