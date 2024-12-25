package com.github.composemusic.bean.searchresult

import com.github.composemusic.bean.user.UserProfileBean

data class SearchUserBean(
    val userprofileCount: Int,
    val userprofiles: List<UserProfileBean>
)