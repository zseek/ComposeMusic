package com.github.composemusic.usecase.search

import com.github.composemusic.bean.search.SearchRecordBean
import com.github.composemusic.room.search.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QueryAllCase(private val repository: SearchHistoryRepository) {
    operator fun invoke(): Flow<List<SearchRecordBean>> {
        return repository.queryAllHistory().map { bean ->
            //降序
            bean.sortedByDescending { it.createTime }
        }
    }
}