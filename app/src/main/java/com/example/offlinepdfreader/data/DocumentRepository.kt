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
import com.example.offlinepdfreader.storage.LibraryStore
import com.example.offlinepdfreader.storage.BookmarkedDocsStore
import com.example.offlinepdfreader.search.PdfSearchIndex

class DocumentRepository(context: Context) {
    private val recentDocumentsStore = RecentDocumentsStore(context)
    private val bookmarkStore = BookmarkStore(context)
    private val noteStore = NoteStore(context)
    private val libraryStore = LibraryStore(context)
    private val bookmarkedDocsStore = BookmarkedDocsStore(context)
    private val searchIndex = PdfSearchIndex()

    fun open(resolver: ContentResolver, uri: Uri): LoadedDocument {
        val engine = PdfRendererEngine.from(resolver, uri)
        // Check if we already have it in recents to preserve the display name if the user renamed it!
        val existing = recentDocumentsStore.list().firstOrNull { it.uriString == uri.toString() }
        val document = PdfDocumentInfo(
            displayName = existing?.displayName ?: uri.lastPathSegment ?: "PDF",
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
        libraryStore.updateProgressInAllLibraries(documentUri, pageIndex)
        bookmarkedDocsStore.updateProgress(documentUri, pageIndex)
    }

    fun renameDoc(documentUri: String, newName: String) {
        recentDocumentsStore.rename(documentUri, newName)
        libraryStore.renameInAllLibraries(documentUri, newName)
        bookmarkedDocsStore.rename(documentUri, newName)
    }

    fun addDocumentDirectly(document: PdfDocumentInfo) {
        recentDocumentsStore.addDirectly(document)
    }

    // Bookmarked documents APIs
    fun listBookmarkedDocs() = bookmarkedDocsStore.list()
    fun isDocBookmarked(uriString: String) = bookmarkedDocsStore.isBookmarked(uriString)
    fun toggleDocBookmark(document: PdfDocumentInfo) = bookmarkedDocsStore.toggle(document)
    fun setDocBookmark(document: PdfDocumentInfo, isBookmarked: Boolean) = bookmarkedDocsStore.bookmark(document, isBookmarked)

    // Libraries APIs
    fun listLibraries() = libraryStore.list()
    fun createLibrary(name: String) = libraryStore.createLibrary(name)
    fun deleteLibrary(id: String) = libraryStore.deleteLibrary(id)
    fun addDocumentToLibrary(libraryId: String, document: PdfDocumentInfo) = libraryStore.addDocumentToLibrary(libraryId, document)
    fun removeDocumentFromLibrary(libraryId: String, documentUri: String) = libraryStore.removeDocumentFromLibrary(libraryId, documentUri)

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
