package com.github.composemusic.route.drawer.download.service

import com.arialyy.aria.core.task.DownloadTask

interface DownloadListener {
    fun onDownloadState(task: DownloadTask, msg: String)
}