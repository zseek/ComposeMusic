package com.github.composemusic.bean.rank

data class Rankbean(
    val ToplistType: String, // 榜单类型
    val adType: Int,
    val anonimous: Boolean,
    val artists: Any,
    val backgroundCoverId: Long,
    val backgroundCoverUrl: Any,
    val cloudTrackCount: Int,
    val commentThreadId: String,
    val coverImageUrl: Any,
    val coverImgId: Long,
    val coverImgId_str: String,
    val coverImgUrl: String, // 封面图
    val coverText: Any,
    val createTime: Long,
    val creator: Any,
    val description: String, // 描述
    val englishTitle: Any,
    val highQuality: Boolean,
    val iconImageUrl: Any,
    val id: Long, // 歌单 ID
    val name: String, // 歌单名
    val newImported: Boolean,
    val opRecommend: Boolean,
    val ordered: Boolean,
    val playCount: Long,
    val privacy: Int,
    val recommendInfo: Any,
    val socialPlaylistCover: Any,
    val specialType: Int,
    val status: Int,
    val subscribed: Any,
    val subscribedCount: Int,
    val subscribers: List<Any>,
    val tags: List<Any>,
    val titleImage: Long,
    val titleImageUrl: Any,
    val totalDuration: Int,
    val trackCount: Int,
    val trackNumberUpdateTime: Long,
    val trackUpdateTime: Long,
    val tracks: List<Track>,
    val tsSongCount: Int,
    val updateFrequency: String,
    val updateTime: Long,
    val userId: Long
)
