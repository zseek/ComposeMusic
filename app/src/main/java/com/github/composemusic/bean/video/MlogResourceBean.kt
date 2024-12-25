package com.github.composemusic.bean.video

import com.github.composemusic.bean.user.UserProfileBean

data class MlogResourceBean(
    val alg: Any,
    val audioStatus: Int,
    val circleVO: Any,
    val commentCount: Int,
    val comments: Any,
    val content: MlogContentBean,
    val extSourceInfo: Any,
    val followedShowReason: Any,
    val id: String,
    val ipLocation: Any,
    val liked: Boolean,
    var likedCount: Int,
    val live: Any,
    val maxCommentShowNum: Int,
    val mixInfo: Any,
    val mlogPlaylists: Any,
    val priorShowLive: Boolean,
    val profile: UserProfileBean,
    val pubTime: Long,
    val reason: Any,
    val reasonType: Any,
    val relatedActivity: Any,
    val relatedPubUsers: Any,
    val shareCount: Int,
    val shareUrl: String,
    val source: String,
    val srcId: Any,
    val status: Int,
    val subed: Boolean,
    val tailMark: Any,
    val talk: Any,
    val threadId: String,
    val type: Int,
    val userId: Int
)