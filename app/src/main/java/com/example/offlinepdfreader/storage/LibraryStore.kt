package com.example.offlinepdfreader.storage

import android.content.Context
import com.example.offlinepdfreader.model.PdfDocumentInfo

data class PdfLibrary(
    val id: String,
    val name: String,
    val documents: List<PdfDocumentInfo> = emptyList()
)

class LibraryStore(context: Context) {
    private val prefs = context.getSharedPreferences("libraries_prefs", Context.MODE_PRIVATE)

    fun list(): List<PdfLibrary> {
        val raw = prefs.getString("library_list", "") ?: ""
        if (raw.isEmpty()) return emptyList()
        return raw.split(";;;").mapNotNull { item ->
            val parts = item.split("|||")
            if (parts.size >= 2) {
                val id = parts[0]
                val name = parts[1]
                val docRawList = parts.getOrNull(2) ?: ""
                val docs = if (docRawList.isEmpty()) emptyList() else {
                    docRawList.split("&&&").mapNotNull { docRaw ->
                        val dParts = docRaw.split(":::")
                        if (dParts.size >= 3) {
                            PdfDocumentInfo(
                                displayName = dParts[0],
                                uriString = dParts[1],
                                pageCount = dParts[2].toIntOrNull() ?: 0,
                                lastOpenedPage = dParts.getOrNull(3)?.toIntOrNull() ?: 0
                            )
                        } else null
                    }
                }
                PdfLibrary(id, name, docs)
            } else null
        }
    }

    fun createLibrary(name: String): PdfLibrary {
        val list = list().toMutableList()
        val newLib = PdfLibrary(
            id = System.currentTimeMillis().toString(),
            name = name,
            documents = emptyList()
        )
        list.add(newLib)
        save(list)
        return newLib
    }

    fun deleteLibrary(id: String) {
        val list = list().toMutableList()
        list.removeAll { it.id == id }
        save(list)
    }

    fun addDocumentToLibrary(libraryId: String, document: PdfDocumentInfo) {
        val list = list().map { lib ->
            if (lib.id == libraryId) {
                val newDocs = lib.documents.toMutableList()
                newDocs.removeAll { it.uriString == document.uriString }
                newDocs.add(0, document)
                lib.copy(documents = newDocs)
            } else {
                lib
            }
        }
        save(list)
    }

    fun removeDocumentFromLibrary(libraryId: String, documentUri: String) {
        val list = list().map { lib ->
            if (lib.id == libraryId) {
                val newDocs = lib.documents.filter { it.uriString != documentUri }
                lib.copy(documents = newDocs)
            } else {
                lib
            }
        }
        save(list)
    }

    fun updateProgressInAllLibraries(documentUri: String, pageIndex: Int) {
        val list = list().map { lib ->
            val match = lib.documents.any { it.uriString == documentUri }
            if (match) {
                val newDocs = lib.documents.map { doc ->
                    if (doc.uriString == documentUri) {
                        doc.copy(lastOpenedPage = pageIndex)
                    } else doc
                }
                lib.copy(documents = newDocs)
            } else lib
        }
        save(list)
    }

    fun renameInAllLibraries(documentUri: String, newName: String) {
        val list = list().map { lib ->
            val match = lib.documents.any { it.uriString == documentUri }
            if (match) {
                val newDocs = lib.documents.map { doc ->
                    if (doc.uriString == documentUri) {
                        doc.copy(displayName = newName)
                    } else doc
                }
                lib.copy(documents = newDocs)
            } else lib
        }
        save(list)
    }

    private fun save(list: List<PdfLibrary>) {
        val serialized = list.joinToString(";;;") { lib ->
            val docsSec = lib.documents.joinToString("&&&") { doc ->
                "${doc.displayName}:::${doc.uriString}:::${doc.pageCount}:::${doc.lastOpenedPage}"
            }
            "${lib.id}|||${lib.name}|||${docsSec}"
        }
        prefs.edit().putString("library_list", serialized).apply()
    }
}
