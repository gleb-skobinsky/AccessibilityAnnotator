package org.discourse.annotator.domain

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
sealed interface DiscourseEntity {
    val id: String

    data class Coreference(
        override val id: String,
        val accessibility: AccessibilityLevel
    ) : DiscourseEntity

    data class Bridging(
        override val id: String
    ) : DiscourseEntity
}

private val noEntityColor = Color.Gray.copy(alpha = 0.3f)

private fun String.toColor(saturation: Float = 0.5f, lightness: Float = 0.8f): Color {
    val hue = fold(0) { acc, char -> char.code + acc * 37 } % 360
    return Color.hsl(hue.absoluteValue.toFloat(), saturation, lightness)
}

fun DiscourseEntity?.toColor() = when (this) {
    null -> noEntityColor
    else -> id.toColor()
}