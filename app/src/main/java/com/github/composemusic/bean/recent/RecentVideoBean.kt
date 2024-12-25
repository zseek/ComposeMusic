package com.github.composemusic.bean.recent

data class RecentVideoBean(
    val id:Long,
    val idStr:String,
    val name:String,
    val artist: String,
    val cover:String,
    val duration:Int,
    val tag:String
)
