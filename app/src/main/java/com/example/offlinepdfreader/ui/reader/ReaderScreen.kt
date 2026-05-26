package com.example.offlinepdfreader.ui.reader

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (state.isNightMode) Color(0xFF121212)
                else if (state.isSepiaMode) Color(0xFFFFF9EE)
                else Color(0xFFF1F5F9)
            )
    ) {
        if (doc == null) {
            // STEP 1: OFF-LINE DOCUMENT DASHBOARD & RECENT FILES
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Sleek App Header with Gradient Accent
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "📚 LOCAL PDF READER",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Private Offline Reader • No AI • Buttery Smooth",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Divider(color = Color(0xFFE2E8F0))

                // Action Launcher buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(130.dp)
                            .clickable { picker.launch(arrayOf("application/pdf")) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB)),
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
                                modifier = Modifier.size(32.dp)
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
                                    fontSize = 12.sp,
                                    color = Color(0xFFDBEAFE)
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(130.dp)
                            .clickable {
                                context.startActivity(Intent(context, ReaderActivity::class.java))
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Try MVP Contract Mode",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Column {
                                Text(
                                    text = "Try MVP Contract",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Alternative architecture",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                // Recent Documents History Header
                Text(
                    text = "Recent Documents",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2563EB))
                    }
                } else if (state.recentDocuments.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "No Recents",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your Reading List is Empty",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Perfect for reading PDF books, instruction manuals, or textbooks offline with absolute privacy.",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                } else {
                    // Recent Files Scroll List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.recentDocuments) { document ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.openDocument(
                                            context.contentResolver,
                                            Uri.parse(document.uriString)
                                        )
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.List,
                                            contentDescription = "PDF document icon",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = document.displayName,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                overflow = TextOverflow.Ellipsis,
                                                maxLines = 1,
                                                color = Color(0xFF1E293B)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Progress: Page ${document.lastOpenedPage + 1} of ${document.pageCount}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }

                                    // Direct Play/Open Action
                                    IconButton(onClick = {
                                        viewModel.openDocument(
                                            context.contentResolver,
                                            Uri.parse(document.uriString)
                                        )
                                    }) {
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
            // STEP 2: ACTIVE HIGH-FIDELITY PDF READER VIEW
            val themeTextColor = if (state.isNightMode) Color.White else Color(0xFF0F172A)
            val themeBgSecondary = if (state.isNightMode) Color(0xFF1E1E1E) else if (state.isSepiaMode) Color(0xFFFCF5E5) else Color.White

            Column(modifier = Modifier.fillMaxSize()) {
                // TOP BAR ACTION CONTROLLERS
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

                    // Eye Comfort Modes (In-Fly GPU filters)
                    IconButton(onClick = { viewModel.toggleSepiaMode() }) {
                        Text(
                            text = "Sepia",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.isSepiaMode) Color(0xFFD97706) else themeTextColor,
                            modifier = Modifier.padding(2.dp)
                        )
                    }

                    IconButton(onClick = { viewModel.toggleNightMode() }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Night Mode",
                            tint = if (state.isNightMode) Color(0xFFFBBF24) else themeTextColor
                        )
                    }

                    // Toggle Bookmark current state
                    val isBookmarked = state.bookmarks.any { it.pageIndex == state.currentPageIndex }
                    IconButton(onClick = { viewModel.toggleBookmarkCurrentPage() }) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Bookmark Page",
                            tint = if (isBookmarked) Color(0xFFFBBF24) else themeTextColor
                        )
                    }

                    // Open Outline Index Slide-out
                    IconButton(onClick = { viewModel.toggleSidebar() }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Directory Menu",
                            tint = if (state.isSidebarOpen) Color(0xFF2563EB) else themeTextColor
                        )
                    }
                }

                Divider(color = if (state.isNightMode) Color.DarkGray else Color.LightGray)

                // TEXT SEARCH BAR EMBEDDED
                var searchExpanded by remember { mutableStateOf(false) }
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
                                contentDescription = "Search icon",
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
                                    contentDescription = "Search icon",
                                    tint = themeTextColor
                                )
                            },
                            trailingIcon = {
                                if (state.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.clearSearch() }) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Clear search",
                                            tint = themeTextColor
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = if (state.isNightMode) Color.DarkGray else Color.LightGray,
                                focusedLabelColor = Color(0xFF2563EB)
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

                Divider(color = if (state.isNightMode) Color.DarkGray else Color.LightGray)

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
                                isSepiaMode = state.isSepiaMode
                            )

                            // Overlay arrows
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
                        // CONTINUOUS SCROLLABLE VIEWPORT
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(doc.pageCount) { index ->
                                LazyPageRenderer(
                                    documentUri = doc.uriString,
                                    pageIndex = index,
                                    width = 1200,
                                    engine = viewModel.getEngine(),
                                    isNightMode = state.isNightMode,
                                    isSepiaMode = state.isSepiaMode
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

                Divider(color = if (state.isNightMode) Color.DarkGray else Color.LightGray)

                // STICKY WRITTEN NOTE WRAPPER
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
                            modifier = Modifier.fillMaxWidth().clickable { editingNotes = true },
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
                                    text = if (scribbleNoteText.isNotEmpty()) "Show page note: '${scribbleNoteText.take(20)}...'" else "Tap to write raw annotation for Page ${state.currentPageIndex + 1}...",
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
                            modifier = Modifier.fillMaxWidth().height(80.dp),
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
                                modifier = Modifier.height(32.dp).padding(horizontal = 4.dp)
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
                                modifier = Modifier.height(32.dp).padding(horizontal = 4.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("Save Note", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }

                Divider(color = if (state.isNightMode) Color.DarkGray else Color.LightGray)

                // BOTTOM CONTROLS TRACKBAR / JUMP SLIDER CONTROL
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
