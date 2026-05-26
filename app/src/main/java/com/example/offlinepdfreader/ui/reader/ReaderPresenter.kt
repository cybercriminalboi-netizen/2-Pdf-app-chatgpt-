package com.example.offlinepdfreader.ui.reader

import android.content.ContentResolver
import android.net.Uri
import com.example.offlinepdfreader.PdfRendererEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReaderPresenter(
    private val contentResolver: ContentResolver,
    private val scope: CoroutineScope
) : ReaderContract.Presenter {

    private var view: ReaderContract.View? = null
    private var engine: PdfRendererEngine? = null
    private var isNightMode = false
    private var currentPage = 1

    override fun attachView(view: ReaderContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
        engine?.close()
        engine = null
    }

    override fun loadPdf(uri: Uri) {
        view?.showLoading()
        scope.launch(Dispatchers.IO) {
            try {
                val newEngine = PdfRendererEngine.from(contentResolver, uri)
                engine?.close()
                engine = newEngine
                currentPage = 1
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.displayPdf(uri)
                    view?.updatePageIndicator(currentPage, newEngine.pageCount)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError(e.message ?: "Failed to load PDF")
                }
            }
        }
    }

    override fun onPageChanged(pageNumber: Int, totalPages: Int) {
        currentPage = pageNumber
        view?.updatePageIndicator(pageNumber, totalPages)
    }

    override fun nextPage() {
        val currentEngine = engine ?: return
        if (currentPage < currentEngine.pageCount) {
            currentPage++
            view?.navigateToPage(currentPage)
            view?.updatePageIndicator(currentPage, currentEngine.pageCount)
        }
    }

    override fun previousPage() {
        val currentEngine = engine ?: return
        if (currentPage > 1) {
            currentPage--
            view?.navigateToPage(currentPage)
            view?.updatePageIndicator(currentPage, currentEngine.pageCount)
        }
    }

    override fun jumpToPage(pageNumber: Int) {
        val currentEngine = engine ?: return
        if (pageNumber in 1..currentEngine.pageCount) {
            currentPage = pageNumber
            view?.navigateToPage(pageNumber)
            view?.updatePageIndicator(pageNumber, currentEngine.pageCount)
        }
    }

    override fun toggleNightMode(enabled: Boolean) {
        isNightMode = enabled
        view?.setNightMode(enabled)
    }
}
