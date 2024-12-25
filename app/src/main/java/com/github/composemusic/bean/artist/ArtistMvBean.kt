package com.github.composemusic.bean.artist

import com.github.composemusic.bean.mv.MvInfoBean

data class ArtistMvBean(
    val code: Int,
    val hasMore: Boolean,
    val mvs: List<MvInfoBean>,
    val time: Long
)