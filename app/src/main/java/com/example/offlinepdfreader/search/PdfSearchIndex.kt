package com.example.offlinepdfreader.search

class PdfSearchIndex {
    private val inMemoryPages = mutableMapOf<String, List<String>>()

    fun index(documentUri: String, pages: List<String>) {
        inMemoryPages[documentUri] = pages
    }

    fun search(documentUri: String, query: String): List<PdfSearchResult> {
        val normalized = query.trim().lowercase()
        if (normalized.isEmpty()) return emptyList()

        val pages = inMemoryPages[documentUri].orEmpty()
        return pages.mapIndexedNotNull { index, text ->
            if (text.lowercase().contains(normalized)) {
                PdfSearchResult(
                    pageIndex = index,
                    pageLabel = "Page ${index + 1}",
                    snippet = text.take(160),
                )
            } else {
                null
            }
        }
    }
}
