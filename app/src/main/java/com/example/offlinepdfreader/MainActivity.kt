package com.example.offlinepdfreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.offlinepdfreader.ui.AppTheme
import com.example.offlinepdfreader.ui.reader.ReaderScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ReaderScreen()
            }
        }
    }
}
