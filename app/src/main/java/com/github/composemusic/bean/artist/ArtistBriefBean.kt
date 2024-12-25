package com.github.composemusic.bean.artist

import com.github.composemusic.bean.user.UserProfileBean

data class ArtistBriefBean(
    val videoCount: Int,
    val vipRights: Any,
    val identify: Any,
    val artist: ArtistInfoBean,
    val blacklist: Boolean,
    val preferShow: Int,
    val showPriMsg: Boolean,
    val secondaryExpertIdentiy: List<Any>,
    val eventCount: Int,
    val user: UserProfileBean
)