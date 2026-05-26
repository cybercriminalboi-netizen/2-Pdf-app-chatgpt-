package com.example.offlinepdfreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.offlinepdfreader.ui.AppTheme
import com.example.offlinepdfreader.ui.reader.ReaderScreen
import com.example.offlinepdfreader.ui.reader.ReaderViewModel

class MainActivity : ComponentActivity() {
    private val readerViewModel by lazy {
        ViewModelProvider(this)[ReaderViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Match action intents
        handleIntent(intent)

        setContent {
            AppTheme {
                ReaderScreen(viewModel = readerViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        val type = intent.type

        try {
            if (Intent.ACTION_SEND == action && type != null) {
                if ("application/pdf" == type) {
                    (intent.getParcelableExtra<android.os.Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { uri ->
                        readerViewModel.openDocument(contentResolver, uri)
                    }
                }
            } else if (Intent.ACTION_VIEW == action && type != null) {
                if ("application/pdf" == type) {
                    intent.data?.let { uri ->
                        readerViewModel.openDocument(contentResolver, uri)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
