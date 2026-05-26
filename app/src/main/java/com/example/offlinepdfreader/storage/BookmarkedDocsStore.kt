package com.example.offlinepdfreader.storage

import android.content.Context
import com.example.offlinepdfreader.model.PdfDocumentInfo

class BookmarkedDocsStore(context: Context) {
    private val prefs = context.getSharedPreferences("bookmarked_docs_prefs", Context.MODE_PRIVATE)

    fun list(): List<PdfDocumentInfo> {
        val raw = prefs.getString("bookmarked_docs_list", "") ?: ""
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

    fun isBookmarked(uriString: String): Boolean {
        return list().any { it.uriString == uriString }
    }

    fun toggle(document: PdfDocumentInfo) {
        val list = list().toMutableList()
        val index = list.indexOfFirst { it.uriString == document.uriString }
        if (index != -1) {
            list.removeAt(index)
        } else {
            list.add(0, document)
        }
        saveAll(list)
    }

    fun bookmark(document: PdfDocumentInfo, bookmarked: Boolean) {
        val list = list().toMutableList()
        val index = list.indexOfFirst { it.uriString == document.uriString }
        if (bookmarked) {
            if (index == -1) {
                list.add(0, document)
            }
        } else {
            if (index != -1) {
                list.removeAt(index)
            }
        }
        saveAll(list)
    }

    fun updateProgress(documentUri: String, pageIndex: Int) {
        val list = list().map {
            if (it.uriString == documentUri) {
                it.copy(lastOpenedPage = pageIndex)
            } else {
                it
            }
        }
        saveAll(list)
    }

    fun rename(documentUri: String, newName: String) {
        val list = list().map {
            if (it.uriString == documentUri) {
                it.copy(displayName = newName)
            } else {
                it
            }
        }
        saveAll(list)
    }

    private fun saveAll(list: List<PdfDocumentInfo>) {
        val serialized = list.joinToString(";;;") { 
            "${it.displayName}|||${it.uriString}|||${it.pageCount}|||${it.lastOpenedPage}"
        }
        prefs.edit().putString("bookmarked_docs_list", serialized).apply()
    }
}
