package com.example.offlinepdfreader.render

import android.graphics.Bitmap
import com.example.offlinepdfreader.cache.PdfPageCache
import com.example.offlinepdfreader.PdfRendererEngine

class PdfRenderCoordinator(
    private val cache: PdfPageCache = PdfPageCache(),
) {
    fun getCachedPage(documentUri: String, pageIndex: Int, width: Int): Bitmap? {
        return cache.get(documentUri, pageIndex, width)
    }

    fun renderAndCache(
        documentUri: String,
        pageIndex: Int,
        width: Int,
        engine: PdfRendererEngine,
    ): Bitmap {
        val bitmap = engine.renderPage(pageIndex, width)
        cache.put(documentUri, pageIndex, width, bitmap)
        return bitmap
    }

    fun clear() {
        cache.clear()
    }
}
