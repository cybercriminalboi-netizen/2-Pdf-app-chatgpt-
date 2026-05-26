package com.example.offlinepdfreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentResolverCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileDescriptor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PdfReaderApp()
            }
        }
    }
}

@Composable
private fun PdfReaderApp(viewModel: PdfReaderViewModel = viewModel()) {
    val context = LocalContext.current
    val openPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.openPdf(context.contentResolver, it) }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = { openPdfLauncher.launch(arrayOf("application/pdf")) }) {
            Text("Open PDF")
        }

        Text(
            text = if (viewModel.currentDocumentName.isBlank()) "No document opened" else viewModel.currentDocumentName,
            style = MaterialTheme.typography.titleMedium
        )

        if (viewModel.pageCount > 0) {
            Text(text = "Pages: ${viewModel.pageCount}")
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.pageBitmaps.size) { index ->
                val bitmap = viewModel.pageBitmaps[index]
                if (bitmap != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        androidx.compose.foundation.Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Page ${index + 1}",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

private class PdfReaderViewModel : ViewModel() {
    private var renderer: PdfRendererEngine? = null
    var currentDocumentName by mutableStateOf("")
        private set
    var pageCount by mutableStateOf(0)
        private set
    val pageBitmaps = mutableStateListOf<android.graphics.Bitmap?>()

    fun openPdf(resolver: android.content.ContentResolver, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val engine = PdfRendererEngine.from(resolver, uri)
            renderer?.close()
            renderer = engine

            currentDocumentName = uri.lastPathSegment ?: "PDF"
            pageCount = engine.pageCount
            pageBitmaps.clear()
            repeat(engine.pageCount) { pageBitmaps.add(null) }

            for (index in 0 until engine.pageCount) {
                val bitmap = engine.renderPage(index)
                pageBitmaps[index] = bitmap
            }
        }
    }

    override fun onCleared() {
        renderer?.close()
        renderer = null
        super.onCleared()
    }
}
