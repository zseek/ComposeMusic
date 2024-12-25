package com.github.composemusic.usecase.download

import com.github.composemusic.bean.download.DownloadMusicBean
import com.github.composemusic.room.download.DownloadRepository

class QueryAllDownloadCase(private val repository: DownloadRepository) {
    suspend operator fun invoke():List<DownloadMusicBean> = repository.queryAll()
}