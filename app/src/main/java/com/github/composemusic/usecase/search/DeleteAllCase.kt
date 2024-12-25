package com.github.composemusic.usecase.search

import com.github.composemusic.room.search.SearchHistoryRepository

class DeleteAllCase(private val repository: SearchHistoryRepository) {
    suspend operator fun invoke() = repository.deleteAllHistory()
}