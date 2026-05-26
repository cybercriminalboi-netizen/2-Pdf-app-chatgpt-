package com.example.offlinepdfreader.ui.reader

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.openDocument(context.contentResolver, it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = { picker.launch(arrayOf("application/pdf")) }) {
            Text(text = "Open PDF")
        }

        Button(onClick = {
            context.startActivity(Intent(context, ReaderActivity::class.java))
        }) {
            Text(text = "Try MVP Contract Mode")
        }

        val doc = state.currentDocument
        when {
            state.isLoading -> CircularProgressIndicator()
            state.errorMessage != null -> Text(text = state.errorMessage ?: "Error")
            doc == null -> Text(text = "No document opened")
            else -> {
                Text(text = doc.displayName)
                Text(text = "Pages: ${doc.pageCount}")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(state.pageBitmaps) { index, bitmap ->
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Page ${index + 1}",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
