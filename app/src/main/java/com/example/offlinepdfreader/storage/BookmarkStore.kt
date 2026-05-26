package com.example.offlinepdfreader.storage

import android.content.Context
import com.example.offlinepdfreader.model.PdfBookmark

class BookmarkStore(context: Context) {
    private val prefs = context.getSharedPreferences("bookmarks_prefs", Context.MODE_PRIVATE)

    fun add(bookmark: PdfBookmark) {
        val list = listAll().toMutableList()
        list.removeAll { it.documentUri == bookmark.documentUri && it.pageIndex == bookmark.pageIndex }
        list.add(bookmark)
        saveAll(list)
    }

    fun remove(documentUri: String, pageIndex: Int) {
        val list = listAll().toMutableList()
        list.removeAll { it.documentUri == documentUri && it.pageIndex == pageIndex }
        saveAll(list)
    }

    fun listFor(documentUri: String): List<PdfBookmark> {
        return listAll().filter { it.documentUri == documentUri }.sortedBy { it.pageIndex }
    }

    private fun listAll(): List<PdfBookmark> {
        val raw = prefs.getString("bookmark_list", "") ?: ""
        if (raw.isEmpty()) return emptyList()
        return raw.split(";;;").mapNotNull { item ->
            val parts = item.split("|||")
            if (parts.size >= 2) {
                PdfBookmark(
                    pageIndex = parts[0].toIntOrNull() ?: 0,
                    documentUri = parts[1],
                    title = parts.getOrNull(2)
                )
            } else null
        }
    }

    private fun saveAll(list: List<PdfBookmark>) {
        val serialized = list.joinToString(";;;") { 
            "${it.pageIndex}|||${it.documentUri}|||${it.title ?: ""}"
        }
        prefs.edit().putString("bookmark_list", serialized).apply()
    }
}
