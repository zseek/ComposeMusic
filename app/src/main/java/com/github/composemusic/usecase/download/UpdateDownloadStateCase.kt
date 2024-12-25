package com.github.composemusic.usecase.download

import com.github.composemusic.room.download.DownloadRepository

class UpdateDownloadStateCase(private val repository: DownloadRepository) {
    suspend operator fun invoke(musicID:Long,download:Boolean) = repository.updateDownloadState(musicID, download)
}