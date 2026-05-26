package com.example.offlinepdfreader

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.IOException

class PdfRendererEngine private constructor(
    private val fileDescriptor: ParcelFileDescriptor,
    private val renderer: PdfRenderer,
) : AutoCloseable {

    val pageCount: Int
        get() = renderer.pageCount

    fun renderPage(index: Int, width: Int = 1200): Bitmap {
        val page = renderer.openPage(index)
        return try {
            val ratio = width.toFloat() / page.width.toFloat()
            val height = (page.height * ratio).toInt().coerceAtLeast(1)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmap
        } finally {
            page.close()
        }
    }

    override fun close() {
        renderer.close()
        fileDescriptor.close()
    }

    companion object {
        fun from(resolver: ContentResolver, uri: Uri): PdfRendererEngine {
            val fd = resolver.openFileDescriptor(uri, "r")
                ?: throw IOException("Unable to open PDF")
            return PdfRendererEngine(fd, PdfRenderer(fd))
        }
    }
}
