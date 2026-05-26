package com.example.offlinepdfreader.ui.reader

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ReaderToolbar(title: String) {
    TopAppBar(title = { Text(title) })
}
