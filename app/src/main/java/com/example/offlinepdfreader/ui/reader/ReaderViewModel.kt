package com.example.offlinepdfreader.ui.reader

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinepdfreader.PdfRendererEngine
import com.example.offlinepdfreader.data.DocumentRepository
import com.example.offlinepdfreader.model.PdfBookmark
import com.example.offlinepdfreader.model.PdfDocumentInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReaderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DocumentRepository(application)
    
    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var engine: PdfRendererEngine? = null

    // Single application-wide high performance rendering coordinator and LRU cache
    val coordinator = com.example.offlinepdfreader.render.PdfRenderCoordinator()

    init {
        loadRecentDocuments()
    }

    fun getEngine(): PdfRendererEngine? = engine

    fun loadRecentDocuments() {
        val list = repository.recentDocuments()
        val librariesList = repository.listLibraries()
        val bookmarkedList = repository.listBookmarkedDocs()
        _uiState.value = _uiState.value.copy(
            recentDocuments = list,
            libraries = librariesList,
            bookmarkedDocuments = bookmarkedList
        )
    }

    fun openDocument(resolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                withContext(Dispatchers.IO) {
                    val loaded = repository.open(resolver, uri)
                    engine?.close()
                    engine = loaded.engine
                    
                    // Recover last opened page progress
                    val recents = repository.recentDocuments()
                    val matchFile = recents.firstOrNull { it.uriString == uri.toString() }
                    val lastPage = matchFile?.lastOpenedPage ?: 0
                    
                    val bookmarks = repository.bookmarksFor(uri.toString())
                    val notes = repository.listNotes(uri.toString())
                    
                    Triple(loaded.document, lastPage, Pair(bookmarks, notes))
                }
            }.onSuccess { (document, lastPage, userStuff) ->
                val (bookmarks, notes) = userStuff
                _uiState.value = _uiState.value.copy(
                    currentDocument = document,
                    currentPageIndex = lastPage.coerceIn(0, (document.pageCount - 1).coerceAtLeast(0)),
                    bookmarks = bookmarks,
                    notes = notes,
                    recentDocuments = repository.recentDocuments(),
                    isLoading = false,
                    searchResults = emptyList(),
                    searchQuery = ""
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Unable to open document",
                )
            }
        }
    }

    fun closeDocument() {
        engine?.close()
        engine = null
        val state = _uiState.value
        _uiState.value = ReaderUiState(
            recentDocuments = repository.recentDocuments(),
            libraries = repository.listLibraries(),
            bookmarkedDocuments = repository.listBookmarkedDocs(),
            isNightMode = state.isNightMode // preserve theme
        )
    }

    fun updateReadingProgress(pageIndex: Int) {
        val doc = _uiState.value.currentDocument ?: return
        val clamped = pageIndex.coerceIn(0, doc.pageCount - 1)
        _uiState.value = _uiState.value.copy(currentPageIndex = clamped)
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProgress(doc.uriString, clamped)
            val list = repository.recentDocuments()
            val librariesList = repository.listLibraries()
            val bookmarkedList = repository.listBookmarkedDocs()
            _uiState.value = _uiState.value.copy(
                recentDocuments = list,
                libraries = librariesList,
                bookmarkedDocuments = bookmarkedList
            )
        }
    }

    fun toggleNightMode() {
        _uiState.value = _uiState.value.copy(
            isNightMode = !_uiState.value.isNightMode,
            isSepiaMode = false // mutually exclusive eyecare Comfort modes
        )
    }

    fun setNightMode(act: Boolean) {
        _uiState.value = _uiState.value.copy(
            isNightMode = act,
            isSepiaMode = if (act) false else _uiState.value.isSepiaMode
        )
    }

    fun toggleSepiaMode() {
        _uiState.value = _uiState.value.copy(
            isSepiaMode = !_uiState.value.isSepiaMode,
            isNightMode = false // mutually exclusive eyecare Comfort modes
        )
    }

    fun setSepiaMode(act: Boolean) {
        _uiState.value = _uiState.value.copy(
            isSepiaMode = act,
            isNightMode = if (act) false else _uiState.value.isNightMode
        )
    }

    // Rename a document across all screens/libraries cleanly
    fun renameDocument(documentUri: String, newName: String) {
        repository.renameDoc(documentUri, newName)
        loadRecentDocuments()
        val current = _uiState.value.currentDocument
        if (current?.uriString == documentUri) {
            _uiState.value = _uiState.value.copy(
                currentDocument = current.copy(displayName = newName)
            )
        }
    }

    // Doc-level general star/bookmark
    fun toggleDocumentBookmarkFromList(document: PdfDocumentInfo) {
        repository.toggleDocBookmark(document)
        loadRecentDocuments()
    }

    fun isDocumentBookmarked(uriString: String): Boolean {
        return repository.isDocBookmarked(uriString)
    }

    // Save as / app-internal copies builder (without overwriting original!)
    fun saveDocumentAsCopy(context: android.content.Context, document: PdfDocumentInfo, newBaseName: String) {
        viewModelScope.launch {
            try {
                val srcUri = Uri.parse(document.uriString)
                val extension = if (document.displayName.contains(".")) document.displayName.substringAfterLast(".") else "pdf"
                var targetName = "$newBaseName.$extension"
                var copiesCount = 0
                
                var targetFile = java.io.File(context.filesDir, targetName)
                while (targetFile.exists()) {
                    copiesCount++
                    targetName = "$newBaseName ($copiesCount).$extension"
                    targetFile = java.io.File(context.filesDir, targetName)
                }

                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(srcUri)?.use { input ->
                        targetFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }

                val newDoc = PdfDocumentInfo(
                    displayName = targetName,
                    uriString = Uri.fromFile(targetFile).toString(),
                    pageCount = document.pageCount,
                    lastOpenedPage = 0
                )
                repository.addDocumentDirectly(newDoc)
                
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Saved as $targetName in Recents", android.widget.Toast.LENGTH_SHORT).show()
                    loadRecentDocuments()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Error saving copy: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Custom Library Creators / Modifiers
    fun createLibrary(name: String) {
        repository.createLibrary(name)
        loadRecentDocuments()
    }

    fun deleteLibrary(id: String) {
        repository.deleteLibrary(id)
        loadRecentDocuments()
    }

    fun addDocumentToLibrary(libraryId: String, document: PdfDocumentInfo) {
        repository.addDocumentToLibrary(libraryId, document)
        loadRecentDocuments()
    }

    fun removeDocumentFromLibrary(libraryId: String, documentUri: String) {
        repository.removeDocumentFromLibrary(libraryId, documentUri)
        loadRecentDocuments()
    }

    fun toggleLayoutMode() {
        _uiState.value = _uiState.value.copy(isSinglePageMode = !_uiState.value.isSinglePageMode)
    }

    fun toggleSidebar() {
        _uiState.value = _uiState.value.copy(isSidebarOpen = !_uiState.value.isSidebarOpen)
    }

    // Bookmarks management
    fun toggleBookmarkCurrentPage() {
        val doc = _uiState.value.currentDocument ?: return
        val currentIdx = _uiState.value.currentPageIndex
        val hasBookmark = _uiState.value.bookmarks.any { it.pageIndex == currentIdx }
        
        viewModelScope.launch(Dispatchers.IO) {
            if (hasBookmark) {
                repository.removeBookmark(doc.uriString, currentIdx)
            } else {
                repository.addBookmark(doc.uriString, currentIdx, "Page ${currentIdx + 1}")
            }
            val updated = repository.bookmarksFor(doc.uriString)
            _uiState.value = _uiState.value.copy(bookmarks = updated)
        }
    }

    // Annotations management
    fun saveNoteForCurrentPage(text: String) {
        val doc = _uiState.value.currentDocument ?: return
        val currentIdx = _uiState.value.currentPageIndex
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveNote(doc.uriString, currentIdx, text)
            val updatedNotes = repository.listNotes(doc.uriString)
            _uiState.value = _uiState.value.copy(notes = updatedNotes)
        }
    }

    fun deleteNoteForCurrentPage() {
        saveNoteForCurrentPage("")
    }

    // Full-Text Offline Local Search
    fun search(query: String) {
        val doc = _uiState.value.currentDocument ?: return
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch(Dispatchers.IO) {
            val results = repository.search(doc.uriString, query)
            _uiState.value = _uiState.value.copy(searchResults = results)
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(searchQuery = "", searchResults = emptyList())
    }

    override fun onCleared() {
        engine?.close()
        engine = null
        super.onCleared()
    }
}
