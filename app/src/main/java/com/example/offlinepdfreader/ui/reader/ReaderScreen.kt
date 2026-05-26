package com.example.offlinepdfreader.ui.reader

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.offlinepdfreader.model.PdfDocumentInfo

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush

// Custom Standout App Icon Shapes in Jetpack Compose
class HexagonShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            val radius = minOf(width, height) / 2f
            val centerX = width / 2f
            val centerY = height / 2f
            moveTo(centerX, centerY - radius)
            lineTo(centerX + radius * 0.866f, centerY - radius * 0.5f)
            lineTo(centerX + radius * 0.866f, centerY + radius * 0.5f)
            lineTo(centerX, centerY + radius)
            lineTo(centerX - radius * 0.866f, centerY + radius * 0.5f)
            lineTo(centerX - radius * 0.866f, centerY - radius * 0.5f)
            close()
        }
        return Outline.Generic(path)
    }
}

class FlowerShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f
            val maxRadius = minOf(width, height) / 2f
            
            val numPetals = 8
            for (i in 0 until 360 step 3) {
                val angleRad = Math.toRadians(i.toDouble())
                val r = maxRadius * (0.82f + 0.18f * kotlin.math.cos(numPetals * angleRad).toFloat())
                val x = centerX + r * kotlin.math.cos(angleRad).toFloat()
                val y = centerY + r * kotlin.math.sin(angleRad).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        return Outline.Generic(path)
    }
}

class SquircleShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            val radius = minOf(width, height) / 2f
            val centerX = width / 2f
            val centerY = height / 2f
            
            moveTo(centerX, centerY - radius)
            quadraticBezierTo(centerX + radius * 0.88f, centerY - radius * 0.88f, centerX + radius, centerY)
            quadraticBezierTo(centerX + radius * 0.88f, centerY + radius * 0.88f, centerX, centerY + radius)
            quadraticBezierTo(centerX - radius * 0.88f, centerY + radius * 0.88f, centerX - radius, centerY)
            quadraticBezierTo(centerX - radius * 0.88f, centerY - radius * 0.88f, centerX, centerY - radius)
            close()
        }
        return Outline.Generic(path)
    }
}

fun getAppIconShape(shapeName: String): Shape {
    return when (shapeName) {
        "flower" -> FlowerShape()
        "squircle" -> SquircleShape()
        else -> HexagonShape()
    }
}

@Composable
fun AppIconView(
    modifier: Modifier = Modifier,
    uriString: String? = null,
    shapeName: String = "hexagon"
) {
    val context = LocalContext.current
    var bitmapImage by remember(uriString) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uriString) {
        if (uriString != null) {
            try {
                val uri = Uri.parse(uriString)
                val isStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(isStream)
                bitmapImage = bitmap?.asImageBitmap()
                isStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
                bitmapImage = null
            }
        } else {
            bitmapImage = null
        }
    }

    Box(
        modifier = modifier
            .clip(getAppIconShape(shapeName))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF2563EB), Color(0xFF7C3AED))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (bitmapImage != null) {
            Image(
                bitmap = bitmapImage!!,
                contentDescription = "User App Icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "PDF",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.openDocument(context.contentResolver, it) }
    }

    // Capture scrolling state to update current page status in continuous mode
    val firstVisibleItemIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }

    LaunchedEffect(firstVisibleItemIndex, state.isSinglePageMode) {
        if (!state.isSinglePageMode && state.currentDocument != null) {
            viewModel.updateReadingProgress(firstVisibleItemIndex)
        }
    }

    // Auto-scroll list if programmatically jump-navigated
    LaunchedEffect(state.currentPageIndex) {
        if (!state.isSinglePageMode && state.currentDocument != null && firstVisibleItemIndex != state.currentPageIndex) {
            listState.animateScrollToItem(state.currentPageIndex)
        }
    }

    val doc = state.currentDocument

    // 1. Back button navigation in Active Document view
    BackHandler(enabled = doc != null) {
        viewModel.closeDocument()
    }

    // Double tap back to exit on Home Screen
    var lastBackPressTime by remember { mutableStateOf(0L) }
    BackHandler(enabled = doc == null) {
        val now = System.currentTimeMillis()
        if (now - lastBackPressTime < 2000) {
            (context as? android.app.Activity)?.finish()
        } else {
            lastBackPressTime = now
            android.widget.Toast.makeText(context, "Press back again to exit", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    // Core Screen Dialogs State
    var showRenameDialog by remember { mutableStateOf<PdfDocumentInfo?>(null) }
    var showSaveAsDialog by remember { mutableStateOf<PdfDocumentInfo?>(null) }
    var showAddToLibraryDialog by remember { mutableStateOf<List<PdfDocumentInfo>?>(null) }
    var showCreateLibraryDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val iconPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setCustomIconUri(it.toString())
        }
    }

    // Multi-Selection State for Home dashboard tabs
    val selectedUris = remember { mutableStateListOf<String>() }
    var activeLibraryToShow by remember { mutableStateOf<com.example.offlinepdfreader.storage.PdfLibrary?>(null) }
    var activeTab by remember { mutableStateOf(0) } // 0: Recent, 1: Libraries, 2: Bookmarks

    // Clear selection whenever changing tabs or opening libraries
    LaunchedEffect(activeTab, activeLibraryToShow) {
        selectedUris.clear()
    }

    // High performance theme parameters
    val surfaceColor = if (state.isNightMode) Color(0xFF121212) else Color(0xFFF8FAFC)
    val cardBg = if (state.isNightMode) Color(0xFF1E1E1E) else Color.White
    val textColorPrimary = if (state.isNightMode) Color.White else Color(0xFF0F172A)
    val textColorSecondary = if (state.isNightMode) Color(0xFF94A3B8) else Color(0xFF475569)

    // Handle Rename Dialog
    if (showRenameDialog != null) {
        val docToRename = showRenameDialog!!
        var tempName by remember { mutableStateOf(docToRename.displayName.substringBeforeLast(".")) }
        val extension = if (docToRename.displayName.contains(".")) docToRename.displayName.substringAfterLast(".") else "pdf"
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text("Rename PDF Document", fontWeight = FontWeight.Bold, color = textColorPrimary) },
            text = {
                Column {
                    Text("Enter a new display name:", fontSize = 13.sp, color = textColorSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.trim().isNotEmpty()) {
                            viewModel.renameDocument(docToRename.uriString, "$tempName.$extension")
                        }
                        showRenameDialog = null
                        selectedUris.clear()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Rename")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showRenameDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = cardBg
        )
    }

    // Handle Save As Copy
    if (showSaveAsDialog != null) {
        val docToCopy = showSaveAsDialog!!
        var tempCopyName by remember { mutableStateOf(docToCopy.displayName.substringBeforeLast(".") + "_copy") }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showSaveAsDialog = null },
            title = { Text("Save Document As Copy", fontWeight = FontWeight.Bold, color = textColorPrimary) },
            text = {
                Column {
                    Text("Enter name for the new copy:", fontSize = 13.sp, color = textColorSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempCopyName,
                        onValueChange = { tempCopyName = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempCopyName.trim().isNotEmpty()) {
                            viewModel.saveDocumentAsCopy(context, docToCopy, tempCopyName.trim())
                        }
                        showSaveAsDialog = null
                        selectedUris.clear()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Save Copy")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showSaveAsDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = cardBg
        )
    }

    // Handle Add to Library
    if (showAddToLibraryDialog != null) {
        val docsToAdd = showAddToLibraryDialog!!
        var newLibName by remember { mutableStateOf("") }
        var isCreatingNew by remember { mutableStateOf(false) }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showAddToLibraryDialog = null },
            title = { Text("Add Selected to Library", fontWeight = FontWeight.Bold, color = textColorPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!isCreatingNew) {
                        Text("Choose target library folder:", fontSize = 13.sp, color = textColorSecondary)
                        LazyColumn(modifier = Modifier.height(150.dp)) {
                            items(state.libraries) { lib ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            docsToAdd.forEach { doc ->
                                                viewModel.addDocumentToLibrary(lib.id, doc)
                                            }
                                            showAddToLibraryDialog = null
                                            selectedUris.clear()
                                            android.widget.Toast.makeText(context, "Added to library '${lib.name}'", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.List,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(lib.name, fontSize = 14.sp, color = textColorPrimary)
                                }
                                Divider(color = if (state.isNightMode) Color.DarkGray else Color(0xFFF1F5F9))
                            }
                            if (state.libraries.isEmpty()) {
                                item {
                                    Text("No libraries found. Click below to make one.", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(vertical = 12.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = { isCreatingNew = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFF2563EB)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("+ Create New Library")
                        }
                    } else {
                        Text("Enter name of new Library:", fontSize = 13.sp, color = textColorSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = newLibName,
                            onValueChange = { newLibName = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { isCreatingNew = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray)
                            ) {
                                Text("Back")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (newLibName.trim().isNotEmpty()) {
                                        viewModel.createLibrary(newLibName.trim())
                                        // Auto-reload to verify then fetch the created one to add docs instantly
                                        val listLibs = viewModel.uiState.value.libraries
                                        val matched = listLibs.firstOrNull { it.name.equals(newLibName.trim(), true) }
                                        val libId = matched?.id ?: System.currentTimeMillis().toString()
                                        docsToAdd.forEach { doc ->
                                            viewModel.addDocumentToLibrary(libId, doc)
                                        }
                                        showAddToLibraryDialog = null
                                        selectedUris.clear()
                                        android.widget.Toast.makeText(context, "Created & Saved to '${newLibName.trim()}'", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Text("Create & Add")
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(
                    onClick = { showAddToLibraryDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = cardBg
        )
    }

    // Handle Create Library from Tab directly
    if (showCreateLibraryDialog) {
        var customLibName by remember { mutableStateOf("") }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showCreateLibraryDialog = false },
            title = { Text("Create Custom Library", fontWeight = FontWeight.Bold, color = textColorPrimary) },
            text = {
                Column {
                    Text("Enter Library folder name:", fontSize = 13.sp, color = textColorSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customLibName,
                        onValueChange = { customLibName = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customLibName.trim().isNotEmpty()) {
                            viewModel.createLibrary(customLibName.trim())
                        }
                        showCreateLibraryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showCreateLibraryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = cardBg
        )
    }

    // App Settings Customizer Dialog
    if (showSettingsDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    text = "🎨 Customize App Icon",
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Customize the look and shape of the app icon on this device.",
                        fontSize = 13.sp,
                        color = textColorSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    // LIVE ICON PREVIEW WITH SHADOW CARD
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            AppIconView(
                                modifier = Modifier.size(90.dp),
                                uriString = state.customIconUri,
                                shapeName = state.customIconShape
                            )
                        }
                    }

                    // CHOOSE CUSTOM IMAGE BUTTON
                    Button(
                        onClick = { iconPicker.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Custom Icon", fontWeight = FontWeight.Bold)
                    }

                    Divider(color = if (state.isNightMode) Color.DarkGray else Color(0xFFF1F5F9))

                    // APP ICON SHAPE SELECTOR
                    Text(
                        text = "SELECT ADVANCED NOT-SQUARE SHAPE:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorSecondary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val shapeOptions = listOf(
                            "hexagon" to "Hexagon",
                            "squircle" to "Squircle",
                            "flower" to "Flower"
                        )
                        shapeOptions.forEach { (shapeKey, label) ->
                            val isSelected = state.customIconShape == shapeKey
                            val buttonBg = if (isSelected) {
                                if (state.isNightMode) Color(0xFF1E3A8A) else Color(0xFF2563EB)
                            } else {
                                if (state.isNightMode) Color(0xFF2D2D2D) else Color(0xFFF1F5F9)
                            }
                            val buttonTxt = if (isSelected) Color.White else textColorPrimary

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(buttonBg)
                                    .clickable { viewModel.setCustomIconShape(shapeKey) }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = buttonTxt
                                )
                            }
                        }
                    }

                    // RESTORE TO DEFAULT APP ICON
                    if (state.customIconUri != null) {
                        Button(
                            onClick = { viewModel.setCustomIconUri(null) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Red),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reset to Default App Icon", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = if (state.isNightMode) Color(0xFF2563EB) else Color(0xFF0F172A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Done")
                }
            },
            containerColor = cardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
    ) {
        if (doc == null) {
            // STEP 1: OFF-LINE DOCUMENT DASHBOARD & LIBRARIES
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Sleek App Header with Custom Shape Dynamic App Icon and Settings trigger
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconView(
                            modifier = Modifier.size(44.dp),
                            uriString = state.customIconUri,
                            shapeName = state.customIconShape
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "PDF Viewer",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Theme Switcher Button
                        IconButton(onClick = { viewModel.toggleNightMode() }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Theme Toggle",
                                tint = if (state.isNightMode) Color(0xFFFBBF24) else Color(0xFF475569)
                            )
                        }

                        // App Icon & Settings Button
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "App Settings",
                                tint = if (state.isNightMode) Color.White else Color(0xFF475569)
                            )
                        }
                    }
                }

                Divider(color = if (state.isNightMode) Color.DarkGray else Color(0xFFE2E8F0))

                // Action Launcher buttons
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .clickable { picker.launch(arrayOf("application/pdf")) },
                    colors = CardDefaults.cardColors(containerColor = if (state.isNightMode) Color(0xFF1E3A8A) else Color(0xFF2563EB)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Open file",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "Open PDF Document",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Select from local storage",
                                fontSize = 11.sp,
                                color = Color(0xFFDBEAFE)
                            )
                        }
                    }
                }

                if (activeLibraryToShow != null) {
                    // Render specific Library Details view
                    val currentLib = activeLibraryToShow!!
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { activeLibraryToShow = null }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = textColorPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Library: ${currentLib.name}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorPrimary
                            )
                        }
                        IconButton(onClick = {
                            viewModel.deleteLibrary(currentLib.id)
                            activeLibraryToShow = null
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Library Group",
                                tint = Color.Red
                            )
                        }
                    }

                    // Retrieve updated library from store directly
                    val refreshedLib = state.libraries.firstOrNull { it.id == currentLib.id }
                    val libDocuments = refreshedLib?.documents ?: emptyList()

                    if (libDocuments.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No documents in this library yet.", color = textColorSecondary, fontSize = 14.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(libDocuments) { document ->
                                val isSelected = selectedUris.contains(document.uriString)
                                val cardBgColor = if (isSelected) {
                                    if (state.isNightMode) Color(0xFF0F2D5C) else Color(0xFFEFF6FF)
                                } else cardBg

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                if (selectedUris.isNotEmpty()) {
                                                    if (isSelected) selectedUris.remove(document.uriString)
                                                    else selectedUris.add(document.uriString)
                                                } else {
                                                    viewModel.openDocument(context.contentResolver, Uri.parse(document.uriString))
                                                }
                                            },
                                            onLongClick = {
                                                if (!isSelected) selectedUris.add(document.uriString)
                                            }
                                        ),
                                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.List,
                                                contentDescription = null,
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column {
                                                Text(
                                                    text = document.displayName,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    overflow = TextOverflow.Ellipsis,
                                                    maxLines = 1,
                                                    color = textColorPrimary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Page ${document.lastOpenedPage + 1} of ${document.pageCount}",
                                                    fontSize = 12.sp,
                                                    color = textColorSecondary
                                                )
                                            }
                                        }

                                        if (selectedUris.isNotEmpty()) {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Filled.Check else Icons.Filled.Add,
                                                contentDescription = null,
                                                tint = if (isSelected) Color(0xFF2563EB) else Color.Gray
                                            )
                                        } else {
                                            IconButton(onClick = { viewModel.openDocument(context.contentResolver, Uri.parse(document.uriString)) }) {
                                                Icon(
                                                    imageVector = Icons.Filled.ArrowForward,
                                                    contentDescription = "Open",
                                                    tint = Color(0xFF2563EB)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Standard Home Tabs Section: Recent Documents, Libraries, Starred
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val activeColorButton = @Composable { tab: Int, text: String ->
                            val active = activeTab == tab
                            Button(
                                onClick = { activeTab = tab },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (active) (if (state.isNightMode) Color(0xFF1E3A8A) else Color(0xFF2563EB)) else Color.Transparent,
                                    contentColor = if (active) Color.White else textColorPrimary
                                ),
                                shape = RoundedCornerShape(12),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        activeColorButton(0, "History")
                        activeColorButton(1, "Folders")
                        activeColorButton(2, "Starred")
                    }

                    // Render selected Tab content
                    when (activeTab) {
                        0 -> {
                            // HISTORY TAB (Unlimited Recents)
                            if (state.recentDocuments.isEmpty()) {
                                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                    Text("Reading list is empty. Select a PDF file above to read.", color = textColorSecondary, fontSize = 14.sp)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(state.recentDocuments) { document ->
                                        val isSelected = selectedUris.contains(document.uriString)
                                        val cardBgColor = if (isSelected) {
                                            if (state.isNightMode) Color(0xFF0F2D5C) else Color(0xFFEFF6FF)
                                        } else cardBg

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .combinedClickable(
                                                    onClick = {
                                                        if (selectedUris.isNotEmpty()) {
                                                            if (isSelected) selectedUris.remove(document.uriString)
                                                            else selectedUris.add(document.uriString)
                                                        } else {
                                                            viewModel.openDocument(context.contentResolver, Uri.parse(document.uriString))
                                                        }
                                                    },
                                                    onLongClick = {
                                                        if (!isSelected) selectedUris.add(document.uriString)
                                                    }
                                                ),
                                            colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    modifier = Modifier.weight(1f),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.List,
                                                        contentDescription = null,
                                                        tint = Color(0xFFEF4444),
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(16.dp))
                                                    Column {
                                                        Text(
                                                            text = document.displayName,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            overflow = TextOverflow.Ellipsis,
                                                            maxLines = 1,
                                                            color = textColorPrimary
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = "Page ${document.lastOpenedPage + 1} of ${document.pageCount}",
                                                            fontSize = 12.sp,
                                                            color = textColorSecondary
                                                        )
                                                    }
                                                }

                                                if (selectedUris.isNotEmpty()) {
                                                    Icon(
                                                        imageVector = if (isSelected) Icons.Filled.Check else Icons.Filled.Add,
                                                        contentDescription = null,
                                                        tint = if (isSelected) Color(0xFF2563EB) else Color.Gray
                                                    )
                                                } else {
                                                    IconButton(onClick = { viewModel.openDocument(context.contentResolver, Uri.parse(document.uriString)) }) {
                                                        Icon(
                                                            imageVector = Icons.Filled.ArrowForward,
                                                            contentDescription = "Open",
                                                            tint = Color(0xFF2563EB)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            // CUSTOM LIBRARIES TAB
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Grouping Collections", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColorPrimary)
                                Button(
                                    onClick = { showCreateLibraryDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFF2563EB))
                                ) {
                                    Text("+ Folder")
                                }
                            }

                            if (state.libraries.isEmpty()) {
                                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                    Text("No folders created. Group papers into libraries.", color = textColorSecondary, fontSize = 14.sp)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(state.libraries) { library ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { activeLibraryToShow = library },
                                            colors = CardDefaults.cardColors(containerColor = cardBg),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Menu,
                                                        contentDescription = null,
                                                        tint = Color(0xFF2563EB),
                                                        modifier = Modifier.size(36.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(16.dp))
                                                    Column {
                                                        Text(
                                                            text = library.name,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = textColorPrimary
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = "${library.documents.size} PDFs",
                                                            fontSize = 12.sp,
                                                            color = textColorSecondary
                                                        )
                                                    }
                                                }
                                                IconButton(onClick = { viewModel.deleteLibrary(library.id) }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Close,
                                                        contentDescription = "Delete folder",
                                                        tint = Color.Gray
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            // STARRED PDFS TAB
                            if (state.bookmarkedDocuments.isEmpty()) {
                                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                    Text("No bookmarked PDF files yet.", color = textColorSecondary, fontSize = 14.sp)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(state.bookmarkedDocuments) { document ->
                                        val isSelected = selectedUris.contains(document.uriString)
                                        val cardBgColor = if (isSelected) {
                                            if (state.isNightMode) Color(0xFF0F2D5C) else Color(0xFFEFF6FF)
                                        } else cardBg

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .combinedClickable(
                                                    onClick = {
                                                        if (selectedUris.isNotEmpty()) {
                                                            if (isSelected) selectedUris.remove(document.uriString)
                                                            else selectedUris.add(document.uriString)
                                                        } else {
                                                            viewModel.openDocument(context.contentResolver, Uri.parse(document.uriString))
                                                        }
                                                    },
                                                    onLongClick = {
                                                        if (!isSelected) selectedUris.add(document.uriString)
                                                    }
                                                ),
                                            colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    modifier = Modifier.weight(1f),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Star,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFBBF24),
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(16.dp))
                                                    Column {
                                                        Text(
                                                            text = document.displayName,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            overflow = TextOverflow.Ellipsis,
                                                            maxLines = 1,
                                                            color = textColorPrimary
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = "Page ${document.lastOpenedPage + 1} of ${document.pageCount}",
                                                            fontSize = 12.sp,
                                                            color = textColorSecondary
                                                        )
                                                    }
                                                }

                                                if (selectedUris.isNotEmpty()) {
                                                    Icon(
                                                        imageVector = if (isSelected) Icons.Filled.Check else Icons.Filled.Add,
                                                        contentDescription = null,
                                                        tint = if (isSelected) Color(0xFF2563EB) else Color.Gray
                                                    )
                                                } else {
                                                    IconButton(onClick = { viewModel.openDocument(context.contentResolver, Uri.parse(document.uriString)) }) {
                                                        Icon(
                                                            imageVector = Icons.Filled.ArrowForward,
                                                            contentDescription = "Open",
                                                            tint = Color(0xFF2563EB)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Beautiful Sticky Bottom FLOATING Actions bar in Multi-selection mode!
            if (selectedUris.isNotEmpty()) {
                val itemsListSnapshot = remember(state, activeLibraryToShow, activeTab) {
                    val pool = mutableListOf<PdfDocumentInfo>()
                    pool.addAll(state.recentDocuments)
                    pool.addAll(state.bookmarkedDocuments)
                    state.libraries.forEach { pool.addAll(it.documents) }
                    pool.distinctBy { it.uriString }
                }
                val selectedDocuments = itemsListSnapshot.filter { selectedUris.contains(it.uriString) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        colors = CardDefaults.cardColors(containerColor = if (state.isNightMode) Color(0xFF262626) else Color(0xFF0F172A)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${selectedUris.size} Selected",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. Clear button
                                IconButton(onClick = { selectedUris.clear() }) {
                                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear selection", tint = Color.White)
                                }

                                // 2. Share option (Can share multiple documents at once!)
                                IconButton(onClick = {
                                    if (selectedDocuments.isNotEmpty()) {
                                        try {
                                            val uris = ArrayList<Uri>()
                                            selectedDocuments.forEach { uris.add(Uri.parse(it.uriString)) }
                                            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                                                type = "application/pdf"
                                                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(Intent.createChooser(intent, "Share PDFs"))
                                        } catch (e: Exception) {
                                            android.widget.Toast.makeText(context, "Cannot share documents", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }) {
                                    Icon(imageVector = Icons.Filled.Share, contentDescription = "Share selected", tint = Color.White)
                                }

                                // 3. Bookmark/Star document lists toggle
                                IconButton(onClick = {
                                    selectedDocuments.forEach { viewModel.toggleDocumentBookmarkFromList(it) }
                                    selectedUris.clear()
                                    android.widget.Toast.makeText(context, "Bookmarks updated", android.widget.Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(imageVector = Icons.Filled.Star, contentDescription = "Star selected", tint = Color(0xFFFBBF24))
                                }

                                // 4. Add to Library button
                                IconButton(onClick = {
                                    showAddToLibraryDialog = selectedDocuments
                                }) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add selected to library group", tint = Color.White)
                                }

                                // 5. Rename (Only visible if exactly 1 is selected)
                                if (selectedUris.size == 1) {
                                    IconButton(onClick = {
                                        showRenameDialog = selectedDocuments.firstOrNull()
                                    }) {
                                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Rename selected display label", tint = Color.White)
                                    }
                                }

                                // 6. Save As (Duplicator tool, always available!)
                                if (selectedUris.size == 1) {
                                    IconButton(onClick = {
                                        showSaveAsDialog = selectedDocuments.firstOrNull()
                                    }) {
                                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Save copy as", tint = Color.White)
                                    }
                                }

                                // 7. Remove item from Current Library if viewing library details
                                if (activeLibraryToShow != null) {
                                    IconButton(onClick = {
                                        selectedUris.forEach { uri ->
                                            viewModel.removeDocumentFromLibrary(activeLibraryToShow!!.id, uri)
                                        }
                                        selectedUris.clear()
                                        android.widget.Toast.makeText(context, "Removed from library", android.widget.Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove from library folder", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // STEP 2: ACTIVE HIGH-FIDELITY PDF READER VIEW
            val themeTextColor = if (state.isNightMode) Color.White else Color(0xFF0F172A)
            val themeBgSecondary = if (state.isNightMode) Color(0xFF1E1E1E) else if (state.isSepiaMode) Color(0xFFFCF5E5) else Color.White

            // State to control visibility of all top/bottom bars (Request 10 single-tap to toggle)
            var showControlOverlays by remember { mutableStateOf(true) }

            // Continuous scroll zoom variables
            var scrollScale by remember(doc.uriString) { mutableStateOf(1f) }
            var scrollOffsetX by remember(doc.uriString) { mutableStateOf(0f) }
            var scrollOffsetY by remember(doc.uriString) { mutableStateOf(0f) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showControlOverlays = !showControlOverlays
                    }
            ) {
                // TOP BAR ACTION CONTROLLERS
                AnimatedVisibility(
                    visible = showControlOverlays,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(themeBgSecondary)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.closeDocument() }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close PDF",
                                tint = themeTextColor
                            )
                        }

                        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                            Text(
                                text = doc.displayName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeTextColor,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Text(
                                text = "Page ${state.currentPageIndex + 1} of ${doc.pageCount}",
                                fontSize = 11.sp,
                                color = if (state.isNightMode) Color.LightGray else Color.Gray
                            )
                        }

                        // Toggle Continuous Scroll vs Single Page swiping layout
                        IconButton(onClick = { viewModel.toggleLayoutMode() }) {
                            Icon(
                                imageVector = Icons.Filled.List,
                                contentDescription = "Layout mode",
                                tint = if (state.isSinglePageMode) Color(0xFF2563EB) else themeTextColor
                            )
                        }

                        // Eye Comfort Modes (Single visibility toggle cycle - Normal -> Sepia -> Night -> Normal)
                        IconButton(onClick = {
                            if (!state.isNightMode && !state.isSepiaMode) {
                                viewModel.setSepiaMode(true)
                                viewModel.setNightMode(false)
                            } else if (state.isSepiaMode) {
                                viewModel.setSepiaMode(false)
                                viewModel.setNightMode(true)
                            } else {
                                viewModel.setSepiaMode(false)
                                viewModel.setNightMode(false)
                            }
                        }) {
                            val iconColor = when {
                                state.isNightMode -> Color(0xFFFBBF24) // Gold
                                state.isSepiaMode -> Color(0xFFD97706) // Orange
                                else -> themeTextColor
                            }
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Cycling eye confort filter",
                                tint = iconColor
                            )
                        }

                        // Star/Bookmark Page action
                        val isPageBookmarked = state.bookmarks.any { it.pageIndex == state.currentPageIndex }
                        IconButton(onClick = { viewModel.toggleBookmarkCurrentPage() }) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Bookmark Page",
                                tint = if (isPageBookmarked) Color(0xFFFBBF24) else themeTextColor
                            )
                        }

                        // Directory Menu sliding sidebar
                        IconButton(onClick = { viewModel.toggleSidebar() }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Directory Menu",
                                tint = if (state.isSidebarOpen) Color(0xFF2563EB) else themeTextColor
                            )
                        }

                        // THREE DOT MENU OPTIONS (RENAMING AND DUPLICATION AS COPYS)
                        var showThreeDotExpanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { showThreeDotExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "Options dropdown",
                                    tint = themeTextColor
                                )
                            }
                            DropdownMenu(
                                expanded = showThreeDotExpanded,
                                onDismissRequest = { showThreeDotExpanded = false },
                                modifier = Modifier.background(themeBgSecondary)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Rename PDF Display Title", color = themeTextColor) },
                                    onClick = {
                                        showThreeDotExpanded = false
                                        showRenameDialog = doc
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Save Copy As (No Overwriting)", color = themeTextColor) },
                                    onClick = {
                                        showThreeDotExpanded = false
                                        showSaveAsDialog = doc
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Add To Collection Folder", color = themeTextColor) },
                                    onClick = {
                                        showThreeDotExpanded = false
                                        showAddToLibraryDialog = listOf(doc)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Share Document", color = themeTextColor) },
                                    onClick = {
                                        showThreeDotExpanded = false
                                        sharePdfFile(context, doc.uriString, doc.displayName)
                                    }
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showControlOverlays,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    Divider(color = if (state.isNightMode) Color.DarkGray else Color.LightGray)
                }

                // TEXT SEARCH BAR EMBEDDED
                var searchExpanded by remember { mutableStateOf(false) }
                AnimatedVisibility(
                    visible = showControlOverlays,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(themeBgSecondary)
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!searchExpanded) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { searchExpanded = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = if (state.isNightMode) Color.LightGray else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Search inside this document...",
                                    fontSize = 13.sp,
                                    color = if (state.isNightMode) Color.LightGray else Color.Gray
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = { viewModel.search(it) },
                                placeholder = { Text("Type keyword to find...", fontSize = 13.sp) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                textStyle = TextStyle(fontSize = 13.sp, color = themeTextColor),
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = null,
                                        tint = themeTextColor
                                    )
                                },
                                trailingIcon = {
                                    if (state.searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.clearSearch() }) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = null,
                                                tint = themeTextColor
                                            )
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2563EB),
                                    unfocusedBorderColor = if (state.isNightMode) Color.DarkGray else Color.LightGray
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = {
                                searchExpanded = false
                                viewModel.clearSearch()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Collapse Search",
                                    tint = themeTextColor
                                )
                            }
                        }
                    }
                }

                // SEARCH RESULTS BOX COZY POP-UP
                if (state.searchResults.isNotEmpty() && searchExpanded) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = themeBgSecondary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            items(state.searchResults) { result ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.updateReadingProgress(result.pageIndex)
                                        }
                                        .padding(vertical = 6.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = result.pageLabel,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF2563EB),
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = result.snippet,
                                        fontSize = 12.sp,
                                        color = themeTextColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Divider(color = if (state.isNightMode) Color.DarkGray else Color(0xFFF1F5F9))
                            }
                        }
                    }
                }

                if (showControlOverlays) {
                    Divider(color = if (state.isNightMode) Color.DarkGray else Color.LightGray)
                }

                // CORE PDF CANVAS DRAWER (Continuous vs Swipe)
                Box(modifier = Modifier.weight(1.0f)) {
                    if (state.isSinglePageMode) {
                        // SINGLE-PAGE SWIPE / PAGING GESTURE PANEL
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LazyPageRenderer(
                                documentUri = doc.uriString,
                                pageIndex = state.currentPageIndex,
                                width = 1200,
                                engine = viewModel.getEngine(),
                                isNightMode = state.isNightMode,
                                isSepiaMode = state.isSepiaMode,
                                coordinator = viewModel.coordinator
                            )

                            // Overlay navigation arrows
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .align(Alignment.Center),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (state.currentPageIndex > 0) {
                                    IconButton(
                                        onClick = { viewModel.updateReadingProgress(state.currentPageIndex - 1) },
                                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowBack,
                                            contentDescription = "Previous",
                                            tint = Color.White
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(36.dp))
                                }

                                if (state.currentPageIndex < doc.pageCount - 1) {
                                    IconButton(
                                        onClick = { viewModel.updateReadingProgress(state.currentPageIndex + 1) },
                                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowForward,
                                            contentDescription = "Next",
                                            tint = Color.White
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(36.dp))
                                }
                            }
                        }
                    } else {
                        // CONTINUOUS SCROLLABLE VIEWPORT avec ZOOM unifié (Request 3 & 4)
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(0.dp) // Supprime le grand espace blanc
                        ) {
                            items(doc.pageCount) { index ->
                                LazyPageRenderer(
                                    documentUri = doc.uriString,
                                    pageIndex = index,
                                    width = 1200,
                                    engine = viewModel.getEngine(),
                                    isNightMode = state.isNightMode,
                                    isSepiaMode = state.isSepiaMode,
                                    coordinator = viewModel.coordinator,
                                    zoomScale = scrollScale,
                                    zoomOffsetX = scrollOffsetX,
                                    zoomOffsetY = scrollOffsetY,
                                    onZoomChanged = { scaleVal, ox, oy ->
                                        scrollScale = scaleVal
                                        scrollOffsetX = ox
                                        scrollOffsetY = oy
                                    }
                                )

                                // Ligne séparatrice noire/blanche très fine selon Request 3
                                val ruleColor = if (state.isNightMode) Color.White.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.25f)
                                Divider(
                                    color = ruleColor,
                                    thickness = 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // SIDE SHEET / SLIDE-IN OVERLAY DRAWER
                    androidx.compose.animation.AnimatedVisibility(
                        visible = state.isSidebarOpen,
                        enter = slideInHorizontally(animationSpec = tween(300)) { -it },
                        exit = slideOutHorizontally(animationSpec = tween(300)) { -it }
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(280.dp)
                                .background(themeBgSecondary),
                            tonalElevation = 8.dp,
                            shadowElevation = 8.dp
                        ) {
                            var tabSelected by remember { mutableStateOf(0) } // 0: Bookmarks, 1: Notes, 2: Pages Grid
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(themeBgSecondary)
                            ) {
                                // Side sheet header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Document Directory",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = themeTextColor
                                    )
                                    IconButton(onClick = { viewModel.toggleSidebar() }) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Close Sidebar",
                                            tint = themeTextColor
                                        )
                                    }
                                }

                                // Interactive Tab switcher
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    val tabColors = @Composable { isChosen: Boolean ->
                                        if (isChosen) Color(0xFF2563EB) else (if (state.isNightMode) Color.LightGray else Color.Gray)
                                    }
                                    Text(
                                        text = "Stars",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = tabColors(tabSelected == 0),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { tabSelected = 0 }
                                            .padding(vertical = 8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Notes",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = tabColors(tabSelected == 1),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { tabSelected = 1 }
                                            .padding(vertical = 8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Pages",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = tabColors(tabSelected == 2),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { tabSelected = 2 }
                                            .padding(vertical = 8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Divider(color = if (state.isNightMode) Color.DarkGray else Color.LightGray)

                                // Tab View Containers
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(12.dp)
                                ) {
                                    when (tabSelected) {
                                        0 -> {
                                            // USER BOOKMARKS TAB
                                            if (state.bookmarks.isEmpty()) {
                                                Text(
                                                    text = "No bookmarks added. Tap the star icon above during reading to save important page coordinates.",
                                                    fontSize = 12.sp,
                                                    color = Color.Gray,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            } else {
                                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                                    items(state.bookmarks) { bkm ->
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                    viewModel.updateReadingProgress(bkm.pageIndex)
                                                                    viewModel.toggleSidebar()
                                                                }
                                                                .padding(vertical = 10.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Text(
                                                                text = bkm.title ?: "Page ${bkm.pageIndex + 1}",
                                                                fontSize = 14.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = themeTextColor
                                                            )
                                                            Icon(
                                                                imageVector = Icons.Filled.Star,
                                                                contentDescription = "Starred",
                                                                tint = Color(0xFFFBBF24),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                        Divider(color = if (state.isNightMode) Color.DarkGray else Color(0xFFF1F5F9))
                                                    }
                                                }
                                            }
                                        }
                                        1 -> {
                                            // USER NOTES ANNOTATIONS TAB
                                            if (state.notes.isEmpty()) {
                                                Text(
                                                    text = "No personal annotations made. Write annotations for pages in the sticky editor below.",
                                                    fontSize = 12.sp,
                                                    color = Color.Gray,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            } else {
                                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                                    items(state.notes) { nt ->
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                    viewModel.updateReadingProgress(nt.pageIndex)
                                                                    viewModel.toggleSidebar()
                                                                }
                                                                .padding(vertical = 10.dp)
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text(
                                                                    text = "Page ${nt.pageIndex + 1}",
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 13.sp,
                                                                    color = Color(0xFF2563EB)
                                                                )
                                                                Icon(
                                                                    imageVector = Icons.Filled.Edit,
                                                                    contentDescription = "Edit",
                                                                    tint = Color.Gray,
                                                                    modifier = Modifier.size(14.dp)
                                                                )
                                                            }
                                                            Spacer(modifier = Modifier.height(4.dp))
                                                            Text(
                                                                text = nt.note,
                                                                fontSize = 13.sp,
                                                                color = themeTextColor,
                                                                maxLines = 2,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                        Divider(color = if (state.isNightMode) Color.DarkGray else Color(0xFFF1F5F9))
                                                    }
                                                }
                                            }
                                        }
                                        2 -> {
                                            // PAGE GRID DIRECTORY CHAPTERS TO JUMP
                                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                                items(doc.pageCount) { idx ->
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                viewModel.updateReadingProgress(idx)
                                                                viewModel.toggleSidebar()
                                                            }
                                                                .padding(vertical = 11.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = "Page ${idx + 1}",
                                                            fontSize = 14.sp,
                                                            color = themeTextColor,
                                                            fontWeight = if (idx == state.currentPageIndex) FontWeight.Bold else FontWeight.Normal
                                                        )
                                                        if (idx == state.currentPageIndex) {
                                                            Icon(
                                                                imageVector = Icons.Filled.Check,
                                                                contentDescription = "Active",
                                                                tint = Color(0xFF2563EB),
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                        }
                                                    }
                                                    Divider(color = if (state.isNightMode) Color.DarkGray else Color(0xFFF1F5F9))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showControlOverlays,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    Divider(color = if (state.isNightMode) Color.DarkGray else Color.LightGray)
                }

                // STICKY WRITTEN NOTE WRAPPER (controlled by showControlOverlays)
                AnimatedVisibility(
                    visible = showControlOverlays,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(themeBgSecondary)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        val currentNotesMatch = state.notes.firstOrNull { it.pageIndex == state.currentPageIndex }
                        var scribbleNoteText by remember(state.currentPageIndex) {
                            mutableStateOf(currentNotesMatch?.note ?: "")
                        }
                        var editingNotes by remember(state.currentPageIndex) { mutableStateOf(false) }

                        if (!editingNotes) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { editingNotes = true },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Annotations notes",
                                        tint = if (scribbleNoteText.isNotEmpty()) Color(0xFF2563EB) else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (scribbleNoteText.isNotEmpty()) "Show page note: '${scribbleNoteText.take(20)}...'" else "Tap to write annotation for Page ${state.currentPageIndex + 1}...",
                                        fontSize = 13.sp,
                                        color = if (scribbleNoteText.isNotEmpty()) themeTextColor else (if (state.isNightMode) Color.LightGray else Color.Gray),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (scribbleNoteText.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.deleteNoteForCurrentPage() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Delete Annotation",
                                            tint = Color.Red,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            // Editable text sheet annotation field
                            OutlinedTextField(
                                value = scribbleNoteText,
                                onValueChange = { scribbleNoteText = it },
                                placeholder = { Text("Write personal page notes...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                textStyle = TextStyle(fontSize = 12.sp, color = themeTextColor),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2563EB),
                                    unfocusedBorderColor = if (state.isNightMode) Color.DarkGray else Color.LightGray
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { editingNotes = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Text("Cancel", fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.saveNoteForCurrentPage(scribbleNoteText)
                                        editingNotes = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .padding(horizontal = 4.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("Save Note", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }

                if (showControlOverlays) {
                    Divider(color = if (state.isNightMode) Color.DarkGray else Color.LightGray)
                }

                // BOTTOM CONTROLS TRACKBAR / JUMP SLIDER CONTROL
                AnimatedVisibility(
                    visible = showControlOverlays,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(themeBgSecondary)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Page 1",
                            fontSize = 11.sp,
                            color = if (state.isNightMode) Color.LightGray else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )

                        Slider(
                            value = state.currentPageIndex.toFloat(),
                            onValueChange = { val valueIndex = it.toInt(); viewModel.updateReadingProgress(valueIndex) },
                            valueRange = 0f..(doc.pageCount - 1).toFloat().coerceAtLeast(1f),
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF2563EB),
                                activeTrackColor = Color(0xFF2563EB),
                                inactiveTrackColor = if (state.isNightMode) Color.DarkGray else Color(0xFFE2E8F0)
                            )
                        )

                        Text(
                            text = "Page ${doc.pageCount}",
                            fontSize = 11.sp,
                            color = if (state.isNightMode) Color.LightGray else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun sharePdfFile(context: android.content.Context, uriString: String, displayName: String) {
    try {
        val uri = Uri.parse(uriString)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, displayName)
            putExtra(Intent.EXTRA_TEXT, "Sharing PDF: $displayName")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share PDF Document"))
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "Cannot share document", android.widget.Toast.LENGTH_SHORT).show()
    }
}
