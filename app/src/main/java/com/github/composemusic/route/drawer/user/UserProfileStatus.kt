package com.github.composemusic.route.drawer.user

sealed class UserProfileStatus(val msg: String) {
    data class NetworkFailed(val error: String) : UserProfileStatus(msg = error)
}
