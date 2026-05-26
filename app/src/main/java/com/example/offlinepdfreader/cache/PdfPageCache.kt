package com.example.offlinepdfreader.cache

import android.graphics.Bitmap
import android.util.LruCache

class PdfPageCache(maxSizeKb: Int = 24 * 1024) {
    private val cache = object : LruCache<String, Bitmap>(maxSizeKb) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
    }

    fun get(documentUri: String, pageIndex: Int, width: Int): Bitmap? {
        return cache.get(key(documentUri, pageIndex, width))
    }

    fun put(documentUri: String, pageIndex: Int, width: Int, bitmap: Bitmap) {
        cache.put(key(documentUri, pageIndex, width), bitmap)
    }

    fun clear() = cache.evictAll()

    private fun key(documentUri: String, pageIndex: Int, width: Int): String {
        return "$documentUri:$pageIndex:$width"
    }
}
