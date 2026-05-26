package com.example.offlinepdfreader.storage

import com.example.offlinepdfreader.model.PdfDocumentInfo

class RecentDocumentsStore {
    private val recent = mutableListOf<PdfDocumentInfo>()

    fun add(document: PdfDocumentInfo) {
        recent.removeAll { it.uriString == document.uriString }
        recent.add(0, document)
    }

    fun list(): List<PdfDocumentInfo> = recent.toList()

    fun clear() {
        recent.clear()
    }
}
