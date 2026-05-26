package com.example.offlinepdfreader.ui.reader

sealed interface DocumentAction {
    data object OpenPicker : DocumentAction
    data object ClearDocument : DocumentAction
    data object GoToStart : DocumentAction
}
