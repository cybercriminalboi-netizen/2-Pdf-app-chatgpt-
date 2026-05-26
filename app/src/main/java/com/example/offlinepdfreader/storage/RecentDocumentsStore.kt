package com.example.offlinepdfreader.storage

import android.content.Context
import com.example.offlinepdfreader.model.PdfDocumentInfo

class RecentDocumentsStore(context: Context) {
    private val prefs = context.getSharedPreferences("recent_documents_prefs", Context.MODE_PRIVATE)

    fun add(document: PdfDocumentInfo) {
        val list = list().toMutableList()
        list.removeAll { it.uriString == document.uriString }
        list.add(0, document)
        save(list) // Help users maintain unlimited history
    }

    fun addDirectly(document: PdfDocumentInfo) {
        val list = list().toMutableList()
        list.removeAll { it.uriString == document.uriString }
        list.add(0, document)
        save(list)
    }

    fun rename(documentUri: String, newName: String) {
        val list = list().map {
            if (it.uriString == documentUri) {
                it.copy(displayName = newName)
            } else {
                it
            }
        }
        save(list)
    }

    fun updateProgress(documentUri: String, pageIndex: Int) {
        val list = list().map {
            if (it.uriString == documentUri) {
                it.copy(lastOpenedPage = pageIndex)
            } else {
                it
            }
        }
        save(list)
    }

    fun list(): List<PdfDocumentInfo> {
        val raw = prefs.getString("recent_list", "") ?: ""
        if (raw.isEmpty()) return emptyList()
        return raw.split(";;;").mapNotNull { item ->
            val parts = item.split("|||")
            if (parts.size >= 3) {
                PdfDocumentInfo(
                    displayName = parts[0],
                    uriString = parts[1],
                    pageCount = parts[2].toIntOrNull() ?: 0,
                    lastOpenedPage = parts.getOrNull(3)?.toIntOrNull() ?: 0
                )
            } else null
        }
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private fun save(list: List<PdfDocumentInfo>) {
        val serialized = list.joinToString(";;;") { 
            "${it.displayName}|||${it.uriString}|||${it.pageCount}|||${it.lastOpenedPage}"
        }
        prefs.edit().putString("recent_list", serialized).apply()
    }
}
