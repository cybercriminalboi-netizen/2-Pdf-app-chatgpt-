package com.example.offlinepdfreader.storage

import com.example.offlinepdfreader.model.PdfBookmark

class BookmarkStore {
    private val bookmarks = mutableListOf<PdfBookmark>()

    fun add(bookmark: PdfBookmark) {
        bookmarks.add(bookmark)
    }

    fun remove(documentUri: String, pageIndex: Int) {
        bookmarks.removeAll { it.documentUri == documentUri && it.pageIndex == pageIndex }
    }

    fun listFor(documentUri: String): List<PdfBookmark> {
        return bookmarks.filter { it.documentUri == documentUri }
    }
}
