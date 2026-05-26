package com.example.offlinepdfreader.storage

import android.content.Context
import com.example.offlinepdfreader.model.PdfNote

class NoteStore(context: Context) {
    private val prefs = context.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)

    fun save(note: PdfNote) {
        val list = listAll().toMutableList()
        list.removeAll { it.documentUri == note.documentUri && it.pageIndex == note.pageIndex }
        if (note.note.trim().isNotEmpty()) {
            list.add(note)
        }
        saveAll(list)
    }

    fun get(documentUri: String, pageIndex: Int): PdfNote? {
        return listAll().firstOrNull { it.documentUri == documentUri && it.pageIndex == pageIndex }
    }

    fun listFor(documentUri: String): List<PdfNote> {
        return listAll().filter { it.documentUri == documentUri }
    }

    private fun listAll(): List<PdfNote> {
        val raw = prefs.getString("notes_list", "") ?: ""
        if (raw.isEmpty()) return emptyList()
        return raw.split(";;;").mapNotNull { item ->
            val parts = item.split("|||")
            if (parts.size >= 3) {
                PdfNote(
                    pageIndex = parts[0].toIntOrNull() ?: 0,
                    documentUri = parts[1],
                    note = parts[2]
                )
            } else null
        }
    }

    private fun saveAll(list: List<PdfNote>) {
        val serialized = list.joinToString(";;;") { 
            "${it.pageIndex}|||${it.documentUri}|||${it.note}"
        }
        prefs.edit().putString("notes_list", serialized).apply()
    }
}
