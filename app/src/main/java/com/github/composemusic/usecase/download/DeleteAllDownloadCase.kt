package com.github.composemusic.usecase.download

import com.github.composemusic.room.download.DownloadRepository

class DeleteAllDownloadCase(private val repository: DownloadRepository) {
    suspend operator fun invoke() = repository.deleteAll()
}