package com.github.composemusic.usecase.download

import com.github.composemusic.bean.download.DownloadMusicBean
import com.github.composemusic.room.download.DownloadRepository
import kotlinx.coroutines.flow.Flow

class QueryAllDownloadImmediateCase(private val repository: DownloadRepository) {
    operator fun invoke():Flow<List<DownloadMusicBean>> = repository.queryAllImmediate()
}