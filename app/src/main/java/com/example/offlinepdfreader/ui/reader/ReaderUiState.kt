package com.example.offlinepdfreader.ui.reader

import com.example.offlinepdfreader.model.PdfDocumentInfo
import com.example.offlinepdfreader.model.PdfBookmark
import com.example.offlinepdfreader.model.PdfNote
import com.example.offlinepdfreader.search.PdfSearchResult
import com.example.offlinepdfreader.storage.PdfLibrary

data class ReaderUiState(
    val currentDocument: PdfDocumentInfo? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recentDocuments: List<PdfDocumentInfo> = emptyList(),
    val libraries: List<PdfLibrary> = emptyList(),
    val bookmarkedDocuments: List<PdfDocumentInfo> = emptyList(),
    
    // Eye comfort modes
    val isNightMode: Boolean = false,
    val isSepiaMode: Boolean = false,
    
    // Layout options
    val isSinglePageMode: Boolean = false, // Single tap swipe/paging vs Continuous lazy scroll
    
    // State management
    val currentPageIndex: Int = 0,
    val zoomLevel: Float = 1f,
    
    // Overlays / Sidebars
    val isSidebarOpen: Boolean = false,
    
    // Features Lists
    val bookmarks: List<PdfBookmark> = emptyList(),
    val notes: List<PdfNote> = emptyList(),
    val searchResults: List<PdfSearchResult> = emptyList(),
    val searchQuery: String = "",
    
    // Custom App Icon settings
    val customIconUri: String? = null,
    val customIconShape: String = "hexagon"
)
