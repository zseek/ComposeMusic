package com.github.composemusic.bean.search

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SearchRecordTable")
data class SearchRecordBean(
    val createTime: Long,
    @PrimaryKey val keyword: String
)
