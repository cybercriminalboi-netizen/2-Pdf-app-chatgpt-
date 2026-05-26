package com.example.offlinepdfreader.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.offlinepdfreader.PdfRendererEngine
import com.example.offlinepdfreader.model.PdfDocumentInfo
import com.example.offlinepdfreader.model.PdfBookmark
import com.example.offlinepdfreader.model.PdfNote
import com.example.offlinepdfreader.storage.BookmarkStore
import com.example.offlinepdfreader.storage.RecentDocumentsStore
import com.example.offlinepdfreader.storage.NoteStore
import com.example.offlinepdfreader.search.PdfSearchIndex

class DocumentRepository(context: Context) {
    private val recentDocumentsStore = RecentDocumentsStore(context)
    private val bookmarkStore = BookmarkStore(context)
    private val noteStore = NoteStore(context)
    private val searchIndex = PdfSearchIndex()

    fun open(resolver: ContentResolver, uri: Uri): LoadedDocument {
        val engine = PdfRendererEngine.from(resolver, uri)
        val document = PdfDocumentInfo(
            displayName = uri.lastPathSegment ?: "PDF",
            uriString = uri.toString(),
            pageCount = engine.pageCount,
        )
        recentDocumentsStore.add(document)
        
        // Index document text streams in background (optimized offline index)
        searchIndex.indexFromStream(resolver, uri, engine.pageCount)
        
        return LoadedDocument(document = document, engine = engine)
    }

    fun recentDocuments() = recentDocumentsStore.list()

    fun updateProgress(documentUri: String, pageIndex: Int) {
        recentDocumentsStore.updateProgress(documentUri, pageIndex)
    }

    fun bookmarksFor(documentUri: String) =
        bookmarkStore.listFor(documentUri)

    fun search(documentUri: String, query: String) =
        searchIndex.search(documentUri, query)

    fun addBookmark(documentUri: String, pageIndex: Int, title: String? = null) {
        bookmarkStore.add(PdfBookmark(documentUri, pageIndex, title))
    }

    fun removeBookmark(documentUri: String, pageIndex: Int) {
        bookmarkStore.remove(documentUri, pageIndex)
    }

    fun indexText(documentUri: String, pages: List<String>) {
        searchIndex.index(documentUri, pages)
    }

    // Notes APIs
    fun saveNote(documentUri: String, pageIndex: Int, text: String) {
        noteStore.save(PdfNote(documentUri = documentUri, pageIndex = pageIndex, note = text))
    }

    fun getNote(documentUri: String, pageIndex: Int): PdfNote? {
        return noteStore.get(documentUri, pageIndex)
    }

    fun listNotes(documentUri: String): List<PdfNote> {
        return noteStore.listFor(documentUri)
    }
}

data class LoadedDocument(
    val document: PdfDocumentInfo,
    val engine: PdfRendererEngine,
)
