package com.example.offlinepdfreader.render

import android.graphics.Bitmap
import com.example.offlinepdfreader.cache.PdfPageCache
import com.example.offlinepdfreader.PdfRendererEngine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PdfRenderCoordinator(
    private val cache: PdfPageCache = PdfPageCache(),
) {
    private val mutex = Mutex()

    fun getCachedPage(documentUri: String, pageIndex: Int, width: Int): Bitmap? {
        return cache.get(documentUri, pageIndex, width)
    }

    suspend fun renderAndCache(
        documentUri: String,
        pageIndex: Int,
        width: Int,
        engine: PdfRendererEngine,
    ): Bitmap = mutex.withLock {
        // Double check cache under lock to ensure we don't duplicate render if multiple requests waited
        val cached = cache.get(documentUri, pageIndex, width)
        if (cached != null) return@withLock cached
        val bitmap = engine.renderPage(pageIndex, width)
        cache.put(documentUri, pageIndex, width, bitmap)
        bitmap
    }

    fun clear() {
        cache.clear()
    }
}
