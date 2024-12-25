package com.github.composemusic.route.playlist

// 播放列表状态
sealed class PlaylistStatus {
    data class TransformResult(val msg: String) : PlaylistStatus() // 保存到本地状态(成功/失败)
    data class NetworkFailed(val msg: String) : PlaylistStatus() // 网络请求失败
    data class Without(val msg: String) : PlaylistStatus() // 没有结果
    data class CommentResult(val msg: String) : PlaylistStatus() // 评论结果
    object OpenComment : PlaylistStatus() // 打开评论
}

// 网络请求状态
sealed class NetworkStatus {
    object Waiting : NetworkStatus() // 等待
    object Successful : NetworkStatus() // 成功
    data class Failed(val error: String) : NetworkStatus() // 失败
}

// 底部弹出的Sheet的显示内容
sealed class BottomSheetScreen {
    object PlaylistComments : BottomSheetScreen() // 播放列表评论
    object FloorComments : BottomSheetScreen() // 楼层评论
    object Playlist : BottomSheetScreen() // 播放列表
}