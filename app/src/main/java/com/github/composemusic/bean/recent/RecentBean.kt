package com.github.composemusic.bean.recent

data class RecentBean<T>(
    val total:Int,
    val list:List<RecentPlayBean<T>>
)
