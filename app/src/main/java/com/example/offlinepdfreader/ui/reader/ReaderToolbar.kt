package com.example.offlinepdfreader.ui.reader

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderToolbar(title: String) {
    TopAppBar(title = { Text(title) })
}
