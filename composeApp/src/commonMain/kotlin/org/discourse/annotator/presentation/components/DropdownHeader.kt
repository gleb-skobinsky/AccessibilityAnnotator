package org.discourse.annotator.presentation.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DropdownHeader(label: String) {
    DropdownMenuItem(
        text = { Text(label) },
        onClick = {},
        enabled = false
    )
}