package com.example.offlinepdfreader.model

data class PdfDocumentInfo(
    val displayName: String,
    val uriString: String,
    val pageCount: Int,
    val lastOpenedPage: Int = 0,
)

data class PdfBookmark(
    val documentUri: String,
    val pageIndex: Int,
    val title: String? = null,
)

data class PdfNote(
    val documentUri: String,
    val pageIndex: Int,
    val note: String,
)
