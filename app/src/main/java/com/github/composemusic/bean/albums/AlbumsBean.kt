package com.github.composemusic.bean.albums

import com.github.composemusic.bean.artist.Artist

data class AlbumsBean(
    val id: Long,
    val name: String,
    val artist: Artist,
    val publishTime: Long,
    val size: Int,
    val copyrightId: Int,
    val status: Int,
    val picId: Long,
    val mark: Long,

    val alias: List<Any>,
    val artists: Any,
    val awardTags: Any,
    val blurPicUrl: String,
    val briefDesc: String,
    val commentThreadId: String,
    val company: String,
    val companyId: Int,
    val description: String,
    val gapless: Int,
    val idStr: String,
    val isSub: Any,
    val info: AlbumInfo,
    val onSale: Boolean,
    val paid: Boolean,
    val pic: Long,
    val picId_str: String,
    val picUrl: String,
    val songs: Any,
    val subType: String,
    val tags: String,
    val type: String,
    val transName: Any,
    val lastSong: Any
)