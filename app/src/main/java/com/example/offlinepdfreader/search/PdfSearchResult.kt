package com.example.offlinepdfreader.search

data class PdfSearchResult(
    val pageIndex: Int,
    val pageLabel: String,
    val snippet: String,
)
