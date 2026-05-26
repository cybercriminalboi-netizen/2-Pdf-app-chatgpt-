package com.example.offlinepdfreader

data class RecentDocument(
    val name: String,
    val uri: String,
    val lastPage: Int = 0,
)
