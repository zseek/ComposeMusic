package com.github.composemusic.room.search

import com.github.composemusic.bean.search.SearchRecordBean
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {

    fun queryAllHistory(): Flow<List<SearchRecordBean>>

    suspend fun insertHistory(bean: SearchRecordBean)

    suspend fun insertAllHistory(bean: List<SearchRecordBean>)

    suspend fun deleteAllHistory()
}