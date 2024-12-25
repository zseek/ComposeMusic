package com.github.composemusic.usecase.download

import com.github.composemusic.room.download.DownloadRepository

class UpdateDownloadTaskIDCase(private val repository: DownloadRepository) {
    suspend operator fun invoke(musicID:Long,taskID:Long) = repository.updateTaskID(musicID, taskID)
}