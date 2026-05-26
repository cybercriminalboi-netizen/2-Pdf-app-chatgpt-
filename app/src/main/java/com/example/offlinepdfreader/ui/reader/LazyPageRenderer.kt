package com.example.offlinepdfreader.ui.reader

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import kotlin.math.abs
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.offlinepdfreader.PdfRendererEngine
import com.example.offlinepdfreader.cache.PdfPageCache
import com.example.offlinepdfreader.render.PdfRenderCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LazyPageRenderer(
    documentUri: String,
    pageIndex: Int,
    width: Int,
    engine: PdfRendererEngine?,
    isNightMode: Boolean,
    isSepiaMode: Boolean,
    coordinator: PdfRenderCoordinator = remember { PdfRenderCoordinator(PdfPageCache()) },
) {
    if (engine == null) return

    var bitmap by remember(documentUri, pageIndex, width) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(documentUri, pageIndex, width) { mutableStateOf(true) }

    // Multi-touch gestures variables
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // Load page bitmap asynchronously
    LaunchedEffect(documentUri, pageIndex, width) {
        isLoading = true
        val cached = coordinator.getCachedPage(documentUri, pageIndex, width)
        if (cached != null) {
            bitmap = cached
            isLoading = false
        } else {
            val loaded = withContext(Dispatchers.IO) {
                try {
                    coordinator.renderAndCache(documentUri, pageIndex, width, engine)
                } catch (e: Exception) {
                    null
                }
            }
            bitmap = loaded
            isLoading = false
        }
    }

    // High performance eye-comfort GPU matrix filters
    val colorFilter = remember(isNightMode, isSepiaMode) {
        when {
            isNightMode -> {
                // Invert colors matrix (black background, white text)
                ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                    -1f,  0f,  0f,  0f, 255f,
                     0f, -1f,  0f,  0f, 255f,
                     0f,  0f, -1f,  0f, 255f,
                     0f,  0f,  0f,  1f,   0f
                )))
            }
            isSepiaMode -> {
                // Warm, paper-like Sepia color matrix filter
                ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                    0.393f, 0.769f, 0.189f, 0f, 0f,
                    0.349f, 0.686f, 0.168f, 0f, 0f,
                    0.272f, 0.534f, 0.131f, 0f, 0f,
                    0f,     0f,     0f,     1f, 0f
                )))
            }
            else -> null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp)
            .background(if (isNightMode) Color(0xFF1E1E1E) else if (isSepiaMode) Color(0xFFFAF0E6) else Color(0xFFE2E8F0))
            .pointerInput(Unit) {
                awaitEachGesture {
                    var pastTouchSlop = false
                    val touchSlop = viewConfiguration.touchSlop
                    var zoom = 1f
                    var pan = androidx.compose.ui.geometry.Offset.Zero

                    do {
                        val event = awaitPointerEvent()
                        val canceled = event.changes.any { it.isConsumed }
                        if (!canceled) {
                            val pointerCount = event.changes.size
                            val zoomChange = event.calculateZoom()
                            val panChange = event.calculatePan()

                            if (!pastTouchSlop) {
                                zoom *= zoomChange
                                pan += panChange
                                val centroidSize = event.calculateCentroidSize(useCurrent = false)
                                val zoomMotion = abs(1f - zoom) * centroidSize
                                val panMotion = pan.getDistance()

                                if (zoomMotion > touchSlop || panMotion > touchSlop) {
                                    pastTouchSlop = true
                                }
                            }

                            if (pastTouchSlop) {
                                if (zoomChange != 1f) {
                                    scale = (scale * zoomChange).coerceIn(1f, 4f)
                                }

                                if (panChange != androidx.compose.ui.geometry.Offset.Zero) {
                                    if (scale > 1f) {
                                        offsetX += panChange.x
                                        offsetY += panChange.y
                                    }
                                }

                                if (scale <= 1f) {
                                    scale = 1f
                                    offsetX = 0f
                                    offsetY = 0f
                                }

                                if (scale > 1f || pointerCount > 1) {
                                    event.changes.forEach { it.consume() }
                                }
                            }
                        }
                    } while (event.changes.any { it.pressed })
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = if (isNightMode) Color.White else Color.Black)
        } else {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Page ${pageIndex + 1}",
                    colorFilter = colorFilter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                )
            }
        }
    }
}
