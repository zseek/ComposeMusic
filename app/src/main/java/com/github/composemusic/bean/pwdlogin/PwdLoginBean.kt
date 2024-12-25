package com.github.composemusic.bean.pwdlogin

import com.github.composemusic.bean.user.Profile


data class PwdLoginBean(
    val account: Account,
    val bindings: List<Binding>,
    val code: Int,
    val cookie: String?,
    val loginType: Int,
    val profile: Profile,
    val token: String?
)