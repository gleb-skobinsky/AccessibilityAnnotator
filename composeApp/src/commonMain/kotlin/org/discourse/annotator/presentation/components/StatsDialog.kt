package org.discourse.annotator.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsDialog(viewModel: MainViewModel) {
    val presenter by viewModel.statsPresenter.collectAsState()
    presenter?.let {
        BasicAlertDialog(
            onDismissRequest = {
                viewModel.closeStats()
            }
        ) {
            val style = MaterialTheme.typography.bodyLarge
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(24.dp)
            ) {
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