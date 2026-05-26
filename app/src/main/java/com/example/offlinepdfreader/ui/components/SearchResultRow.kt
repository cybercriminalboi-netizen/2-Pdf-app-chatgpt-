package com.example.offlinepdfreader.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.offlinepdfreader.search.PdfSearchResult

@Composable
fun SearchResultRow(result: PdfSearchResult) {
    Text(text = "${result.pageLabel}: ${result.snippet}")
}
