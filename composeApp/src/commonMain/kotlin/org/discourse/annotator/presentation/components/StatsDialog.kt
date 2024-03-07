package org.discourse.annotator.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.*
import org.discourse.annotator.domain.AccessibilityLevel
import org.discourse.annotator.domain.stats.CoreferenceStats
import org.discourse.annotator.presentation.common.HorizontalSpacer
import org.discourse.annotator.presentation.common.VerticalSpacer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalKoalaPlotApi::class)
@Composable
fun StatsDialog(viewModel: MainViewModel) {
    val presenter by viewModel.statsPresenter.collectAsState()
    var charts by remember { mutableStateOf(false) }
    presenter?.let {
        BasicAlertDialog(
            onDismissRequest = {
                viewModel.closeStats()
            }
        ) {
            val style = MaterialTheme.typography.bodyLarge
            Surface(
                color = Color.White,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(Modifier.padding(26.dp)) {
                    MultiChoiceSegmentedButtonRow(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        SegmentedButton(!charts, { charts = false }, RoundedCornerShape(32.dp)) {
                            Text("Overall")
                        }
                        SegmentedButton(charts, { charts = true }, RoundedCornerShape(32.dp)) {
                            Text("Per parts")
                        }
                    }
                    if (charts) {
                        val parts = List(it.detailedCoreference.size) { index -> "Part ${index + 1}" }
                        Column(Modifier.align(Alignment.CenterHorizontally).padding(top = 56.dp)) {
                            AccessibilityLevel.entries.forEach {
                                if (it != AccessibilityLevel.Unknown) {
                                    Row {
                                        Spacer(Modifier.clip(CircleShape).size(20.dp).background(it.color))
                                        12.dp.HorizontalSpacer()
                                        Text(it.label, style = MaterialTheme.typography.bodyLarge)
                                    }
                                    12.dp.VerticalSpacer()
                                }
                            }
                        }
                        XYGraph(
                            xAxisModel = CategoryAxisModel(parts),
                            yAxisModel = LinearAxisModel(0f..33f),
                            modifier = Modifier.width(1200.dp)
                        ) {
                            chart(
                                Color.Blue,
                                it.detailedCoreference.mapIndexed { index: Int, coref: CoreferenceStats ->
                                    DefaultPoint("Part ${index + 1}", coref.accMentionsFirstType.toFloat())
                                }
                            )
                            chart(
                                Color.Red,
                                it.detailedCoreference.mapIndexed { index: Int, coref: CoreferenceStats ->
                                    DefaultPoint("Part ${index + 1}", coref.accMentionsSecondType.toFloat())
                                }
                            )
                            chart(
                                Color.Black,
                                it.detailedCoreference.mapIndexed { index: Int, coref: CoreferenceStats ->
                                    DefaultPoint("Part ${index + 1}", coref.accMentionsThirdType.toFloat())
                                }
                            )
                            chart(
                                Color.Gray,
                                it.detailedCoreference.mapIndexed { index: Int, coref: CoreferenceStats ->
                                    DefaultPoint("Part ${index + 1}", coref.accMentionsFourthType.toFloat())
                                }
                            )
                            chart(
                                Color.Magenta,
                                it.detailedCoreference.mapIndexed { index: Int, coref: CoreferenceStats ->
                                    DefaultPoint("Part ${index + 1}", coref.accMentionsFifthType.toFloat())
                                }
                            )
                        }

                    } else {
                        SelectionContainer {
                            Column(Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                                presenter?.present()?.forEach {
                                    Row(
                                        Modifier.fillMaxWidth(0.8f),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${it.first}:", style = style)
                                        Text(it.second, style = style)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun XYGraphScope<String, Float>.chart(
    color: Color,
    data: List<DefaultPoint<String, Float>>
) {
    LinePlot(
        data = data,
        lineStyle = LineStyle(
            brush = SolidColor(color),
            strokeWidth = 2.dp
        ),
        symbol = { point ->
            Symbol(
                shape = CircleShape,
                fillBrush = SolidColor(color),
            )
        }
    )
}