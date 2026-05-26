package com.example.offlinepdfreader.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DocumentHeader(title: String, subtitle: String? = null) {
    Text(text = title)
    if (subtitle != null) {
        Text(text = subtitle)
    }
}
