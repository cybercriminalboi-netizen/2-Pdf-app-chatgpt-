package com.example.offlinepdfreader.ui.reader

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinepdfreader.PdfRendererEngine
import com.example.offlinepdfreader.model.PdfDocumentInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReaderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var engine: PdfRendererEngine? = null

    fun openDocument(resolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                withContext(Dispatchers.IO) {
                    val newEngine = PdfRendererEngine.from(resolver, uri)
                    engine?.close()
                    engine = newEngine
                    val count = newEngine.pageCount
                    val document = PdfDocumentInfo(
                        displayName = uri.lastPathSegment ?: "PDF",
                        uriString = uri.toString(),
                        pageCount = count,
                    )
                    val bitmaps = List(count) { index -> newEngine.renderPage(index) }
                    document to bitmaps
                }
            }.onSuccess { (document, bitmaps) ->
                _uiState.value = ReaderUiState(
                    currentDocument = document,
                    pageBitmaps = bitmaps,
                    isLoading = false,
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Unable to open document",
                )
            }
        }
    }

    override fun onCleared() {
        engine?.close()
        engine = null
        super.onCleared()
    }
}
