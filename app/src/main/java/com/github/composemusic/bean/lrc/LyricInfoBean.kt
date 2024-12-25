package com.github.composemusic.bean.lrc

data class LyricInfoBean(
    val code: Int,
    val sfy: Boolean,
    val qfy: Boolean,
    val sgc: Boolean,
    val lrc: Lrc, // 标准 lrc 歌词
    val klyric: Lrc, // 音译歌词
    val tlyric: Lrc, // 中文翻译歌词
    val romalrc: Lrc, // 音译歌词 (例如罗马字<romaji>)
)