package com.example.offlinepdfreader.data

import android.content.ContentResolver
import android.net.Uri
import com.example.offlinepdfreader.PdfRendererEngine
import com.example.offlinepdfreader.model.PdfDocumentInfo
import com.example.offlinepdfreader.storage.BookmarkStore
import com.example.offlinepdfreader.storage.RecentDocumentsStore
import com.example.offlinepdfreader.search.PdfSearchIndex

class DocumentRepository(
    private val recentDocumentsStore: RecentDocumentsStore =
        RecentDocumentsStore(),
    private val bookmarkStore: BookmarkStore = BookmarkStore(),
    private val searchIndex: PdfSearchIndex = PdfSearchIndex(),
) {
    fun open(resolver: ContentResolver, uri: Uri): LoadedDocument {
        val engine = PdfRendererEngine.from(resolver, uri)
        val document = PdfDocumentInfo(
            displayName = uri.lastPathSegment ?: "PDF",
            uriString = uri.toString(),
            pageCount = engine.pageCount,
        )
        recentDocumentsStore.add(document)
        return LoadedDocument(document = document, engine = engine)
    }

    fun recentDocuments() = recentDocumentsStore.list()

    fun bookmarksFor(documentUri: String) =
        bookmarkStore.listFor(documentUri)

    fun search(documentUri: String, query: String) =
        searchIndex.search(documentUri, query)

    fun addBookmark(documentUri: String, pageIndex: Int, title: String? = null) {
        bookmarkStore.add(com.example.offlinepdfreader.model.PdfBookmark(documentUri, pageIndex, title))
    }

    fun removeBookmark(documentUri: String, pageIndex: Int) {
        bookmarkStore.remove(documentUri, pageIndex)
    }

    fun indexText(documentUri: String, pages: List<String>) {
        searchIndex.index(documentUri, pages)
    }
}

data class LoadedDocument(
    val document: PdfDocumentInfo,
    val engine: PdfRendererEngine,
)
