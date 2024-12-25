package com.github.composemusic.usecase.download

import com.github.composemusic.bean.download.DownloadMusicBean
import com.github.composemusic.room.download.DownloadRepository

class InsertAllDownloadCase(private val repository: DownloadRepository) {
    suspend operator fun invoke(bean: List<DownloadMusicBean>) = repository.insertAll(bean)
}