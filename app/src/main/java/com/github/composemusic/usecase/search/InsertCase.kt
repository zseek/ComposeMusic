package com.github.composemusic.usecase.search

import com.github.composemusic.bean.search.SearchRecordBean
import com.github.composemusic.room.search.SearchHistoryRepository

class InsertCase(private val repository: SearchHistoryRepository) {
    suspend operator fun invoke(bean: SearchRecordBean) = repository.insertHistory(bean)

}