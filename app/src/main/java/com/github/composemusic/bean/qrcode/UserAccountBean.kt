package com.github.composemusic.bean.qrcode

class UserAccountBean(
    val code: Int,
    val account: AccountBean,
    val profile: ProfileBean
)

data class AccountBean(
    val id: Long,
    val userName: String,
    val type: Int,
    val status: Int,
    val whitelistAuthority: Int,
    val createTime: Long,
    val tokenVersion: Int,
    val ban: Int,
    val baoyueVersion: Int,
    val donateVersion: Int,
    val vipType: Int,
    val anonimousUser: Boolean,
    val paidFee: Boolean
)

data class ProfileBean(
    val userId: Long,
    val userType: Int,
    val nickname: String,
    val avatarImgId: Long,
    val avatarUrl: String,
    val backgroundImgId: Long,
    val backgroundUrl: String?,
    val signature: String?,
    val createTime: Long,
    val userName: String,
    val accountType: Int,
    val shortUserName: String,
    val birthday: Long,
    val authority: Int,
    val gender: Int,
    val accountStatus: Int,
    val province: Int,
    val city: Int,
    val authStatus: Int,
    val description: String?,
    val detailDescription: String?,
    val defaultAvatar: Boolean,
    val expertTags: List<String>?,
    val experts: List<String>?,
    val djStatus: Int,
    val locationStatus: Int,
    val vipType: Int,
    val followed: Boolean,
    val mutual: Boolean,
    val authenticated: Boolean,
    val lastLoginTime: Long,
    val lastLoginIP: String,
    val remarkName: String?,
    val viptypeVersion: Long,
    val authenticationTypes: Int,
    val avatarDetail: String?,
    val anchor: Boolean,
)