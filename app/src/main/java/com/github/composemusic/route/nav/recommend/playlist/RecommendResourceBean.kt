package com.github.composemusic.route.nav.recommend.playlist

data class RecommendResourceBean(
    val code: Int,
    val featureFirst: Boolean,
    val haveRcmdSongs: Boolean,
    val recommend: List<Recommend>
)

data class Recommend(
    val id: Long,
    val type: Int,
    val name: String,
    val copywriter: String,
    val picUrl: String,
    val playcount: Long,
    val createTime: Long,
    val creator: Creator,
    val trackCount: Int,
    val userId: Long,
    val alg: String
)

data class Creator(
    val accountStatus: Int,
    val vipType: Int,
    val province: Int,
    val avatarUrl: String,
    val authStatus: Int,
    val userType: Int,
    val nickname: String,
    val gender: Int,
    val birthday: Long,
    val city: Int,
    val backgroundUrl: String,
    val avatarImgId: Long,
    val backgroundImgId: Long,
    val detailDescription: String,
    val defaultAvatar: Boolean,
    val expertTags: List<String>?,
    val djStatus: Int,
    val followed: Boolean,
    val mutual: Boolean,
    val remarkName: String?,
    val avatarImgIdStr: String,
    val backgroundImgIdStr: String,
    val description: String,
    val userId: Long,
    val signature: String,
    val authority: Int
)