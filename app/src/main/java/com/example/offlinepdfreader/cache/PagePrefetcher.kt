package com.example.offlinepdfreader.cache

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PagePrefetcher {
    private var job: Job? = null

    fun prefetch(scope: CoroutineScope, block: suspend () -> Unit) {
        job?.cancel()
        job = scope.launch(Dispatchers.IO) { block() }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}
