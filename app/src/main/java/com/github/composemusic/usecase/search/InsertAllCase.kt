package com.github.composemusic.usecase.search

import com.github.composemusic.bean.search.SearchRecordBean
import com.github.composemusic.room.search.SearchHistoryRepository

class InsertAllCase(private val repository: SearchHistoryRepository) {
    suspend operator fun invoke(bean: List<SearchRecordBean>) = repository.insertAllHistory(bean)
}