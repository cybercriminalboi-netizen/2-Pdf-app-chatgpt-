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

    init {
        loadRecentDocuments()
    }

    fun getEngine(): PdfRendererEngine? = engine

    fun loadRecentDocuments() {
        val list = repository.recentDocuments()
        _uiState.value = _uiState.value.copy(recentDocuments = list)
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
        _uiState.value = ReaderUiState(recentDocuments = repository.recentDocuments())
    }

    fun updateReadingProgress(pageIndex: Int) {
        val doc = _uiState.value.currentDocument ?: return
        val clamped = pageIndex.coerceIn(0, doc.pageCount - 1)
        _uiState.value = _uiState.value.copy(currentPageIndex = clamped)
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProgress(doc.uriString, clamped)
            _uiState.value = _uiState.value.copy(recentDocuments = repository.recentDocuments())
        }
    }

    fun toggleNightMode() {
        _uiState.value = _uiState.value.copy(
            isNightMode = !_uiState.value.isNightMode,
            isSepiaMode = false // mutually exclusive eyecare Comfort modes
        )
    }

    fun toggleSepiaMode() {
        _uiState.value = _uiState.value.copy(
            isSepiaMode = !_uiState.value.isSepiaMode,
            isNightMode = false // mutually exclusive eyecare Comfort modes
        )
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
