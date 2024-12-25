package com.github.composemusic.room.download

import com.github.composemusic.bean.download.DownloadMusicBean
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {

    fun queryAllImmediate(): Flow<List<DownloadMusicBean>>

    suspend fun queryAll(): List<DownloadMusicBean>

    suspend fun insert(bean: DownloadMusicBean)

    suspend fun insertAll(bean: List<DownloadMusicBean>)

    suspend fun deleteAll()

    suspend fun deleteSinger(bean: DownloadMusicBean)

    suspend fun updateTaskID(musicID: Long, taskID: Long)

    suspend fun updateDownloadState(musicID: Long, download: Boolean)
}