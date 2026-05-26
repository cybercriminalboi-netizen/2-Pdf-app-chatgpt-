package com.example.offlinepdfreader.ui.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReaderControls(
    onOpenPdf: () -> Unit,
    onGoToFirstPage: () -> Unit,
    onClearDocument: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = onOpenPdf) { Text("Open") }
        Button(onClick = onGoToFirstPage) { Text("Top") }
        Button(onClick = onClearDocument) { Text("Clear") }
    }
}
