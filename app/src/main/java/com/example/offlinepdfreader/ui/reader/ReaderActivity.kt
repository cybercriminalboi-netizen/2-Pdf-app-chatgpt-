package com.example.offlinepdfreader.ui.reader

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.offlinepdfreader.ui.AppTheme

class ReaderActivity : ComponentActivity(), ReaderContract.View {

    private lateinit var presenter: ReaderContract.Presenter

    private var isLoading by mutableStateOf(false)
    private var pdfUri by mutableStateOf<Uri?>(null)
    private var pageIndicatorText by mutableStateOf("No document opened")
    private var isNightModeEnabled by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = ReaderPresenter(contentResolver, lifecycleScope)
        presenter.attachView(this)

        setContent {
            AppTheme {
                ReaderUiContent(
                    isLoading = isLoading,
                    pdfUri = pdfUri,
                    pageIndicatorText = pageIndicatorText,
                    isNightMode = isNightModeEnabled,
                    onOpenPdf = { uri -> presenter.loadPdf(uri) },
                    onToggleNightMode = { presenter.toggleNightMode(!isNightModeEnabled) }
                )
            }
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    // ReaderContract.View overrides
    override fun showLoading() {
        isLoading = true
    }

    override fun hideLoading() {
        isLoading = false
    }

    override fun displayPdf(uri: Uri) {
        pdfUri = uri
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun updatePageIndicator(currentPage: Int, totalPages: Int) {
        pageIndicatorText = "Page $currentPage of $totalPages"
    }

    override fun navigateToPage(pageNumber: Int) {
        // Implement custom page navigation/handling
    }

    override fun setNightMode(enabled: Boolean) {
        isNightModeEnabled = enabled
    }
}

@Composable
private fun ReaderUiContent(
    isLoading: Boolean,
    pdfUri: Uri?,
    pageIndicatorText: String,
    isNightMode: Boolean,
    onOpenPdf: (Uri) -> Unit,
    onToggleNightMode: () -> Unit,
) {
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { onOpenPdf(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = { picker.launch(arrayOf("application/pdf")) }) {
            Text(text = "Open PDF via MVP Contract")
        }

        Button(onClick = onToggleNightMode) {
            Text(text = if (isNightMode) "Switch to Light Mode" else "Switch to Night Mode")
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        Text(text = pageIndicatorText, style = MaterialTheme.typography.bodyLarge)

        pdfUri?.let {
            Text(text = "Currently displaying: ${it.lastPathSegment ?: "PDF"}")
        }
    }
}
