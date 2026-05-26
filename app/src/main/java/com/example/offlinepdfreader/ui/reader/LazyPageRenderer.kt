package com.example.offlinepdfreader.ui.reader

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.example.offlinepdfreader.cache.PdfPageCache
import com.example.offlinepdfreader.render.PdfRenderCoordinator
import com.example.offlinepdfreader.PdfRendererEngine

@Composable
fun LazyPageRenderer(
    documentUri: String,
    pageIndex: Int,
    width: Int,
    engine: PdfRendererEngine,
    coordinator: PdfRenderCoordinator = remember {
        PdfRenderCoordinator(PdfPageCache()) },
) {
    val cached = coordinator.getCachedPage(documentUri, pageIndex, width)
    LaunchedEffect(documentUri, pageIndex, width) {
        if (cached == null) {
            coordinator.renderAndCache(documentUri, pageIndex, width, engine)
        }
    }

    cached?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Page ${pageIndex + 1}",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
