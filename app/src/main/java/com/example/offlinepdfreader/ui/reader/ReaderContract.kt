package com.example.offlinepdfreader.ui.reader

import android.net.Uri

/**
 * Standard MVP Contract for a local, offline-only PDF viewer application.
 */
interface ReaderContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun displayPdf(uri: Uri)
        fun showError(message: String)
        fun updatePageIndicator(currentPage: Int, totalPages: Int)
        fun navigateToPage(pageNumber: Int)
        fun setNightMode(enabled: Boolean)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadPdf(uri: Uri)
        fun onPageChanged(pageNumber: Int, totalPages: Int)
        fun nextPage()
        fun previousPage()
        fun jumpToPage(pageNumber: Int)
        fun toggleNightMode(enabled: Boolean)
    }
}
