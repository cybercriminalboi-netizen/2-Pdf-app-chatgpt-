package com.example.offlinepdfreader.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.offlinepdfreader.model.PdfBookmark

@Composable
fun BookmarkRow(bookmark: PdfBookmark) {
    Text(text = bookmark.title ?: "Page ${bookmark.pageIndex + 1}")
}
