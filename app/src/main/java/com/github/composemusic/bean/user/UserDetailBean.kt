package com.github.composemusic.bean.user

data class UserDetailBean(
    val level: Int,
    val listenSongs: Int,
    val userPoint: Any,
    val mobileSign: Boolean,
    val pcSign: Boolean,
    val profile: Profile, // 关键部分
    val peopleCanSeeMyPlayRecord: Boolean,
    val bindings: List<Any>,
    val adValid: Boolean,
    val code: Int,
    val newUser: Boolean,
    val recallUser: Boolean,
    val createTime: Long,
    val createDays: Int,
    val profileVillageInfo: Any,
)