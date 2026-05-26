package com.example.offlinepdfreader.ui.reader

import android.graphics.Bitmap
import com.example.offlinepdfreader.model.PdfDocumentInfo

data class ReaderUiState(
    val currentDocument: PdfDocumentInfo? = null,
    val pageBitmaps: List<Bitmap?> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
