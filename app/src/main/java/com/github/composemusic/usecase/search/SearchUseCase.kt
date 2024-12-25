package com.github.composemusic.usecase.search

data class SearchUseCase(
    val queryAll: QueryAllCase,
    val insert: InsertCase,
    val deleteAll: DeleteAllCase,
    val insertAll: InsertAllCase
)
