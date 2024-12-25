package com.github.composemusic.usecase.download

import com.github.composemusic.bean.download.DownloadMusicBean
import com.github.composemusic.room.download.DownloadRepository

class DeleteDownloadCase(private val repository: DownloadRepository) {
    suspend operator fun invoke(bean: DownloadMusicBean) = repository.deleteSinger(bean)
}