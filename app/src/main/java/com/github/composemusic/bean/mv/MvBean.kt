package com.github.composemusic.bean.mv

import com.github.composemusic.bean.artist.Artist

data class MvBean(
    val alg: String,
    val alias: Any,
    val artistId: Int,
    val artistName: String,
    val artists: List<Artist>,
    val briefDesc: Any,
    val brs: Any,
    val commentThreadId: String,
    val cover: String,
    val coverUrl: String,
    val coverId_str: String,
    val coverId: Long,
    val desc: Any,
    val duration: Int,
    val id: Long,
    val mark: Int,
    val name: String,
    val nType: Int,
    val publishTime: String,
    val price: Any,
    val playCount: Long,
    var subCount: Long,
    val shareCount: Long,
    val commentCount: Long,
    val status: Int,
    val transNames: Any,
    val videoGroup: Any
)