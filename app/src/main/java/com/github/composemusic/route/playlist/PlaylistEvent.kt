package com.github.composemusic.route.playlist

sealed class PlaylistEvent {
    // 显示对话框事件
    object IsShowDialog : PlaylistEvent()
    // 保存照片事件
    object SavePhoto : PlaylistEvent()
    // 获取下一页评论事件
    object NextCommentPage : PlaylistEvent()
    // 打开播放列表评论事件
    object OpenPlaylistComment : PlaylistEvent()
    // 发送评论事件
    object SendComment : PlaylistEvent()
    // 获取下一页楼层评论事件
    object NextFloorCommentPage : PlaylistEvent()
    // 播放音乐项事件，包含音乐项的索引和ID
    data class PlayMusicItem(val index: Int, val id: Long) : PlaylistEvent()
    // 发送楼层评论事件，包含评论ID
    data class SendFloorComment(val commentID: Long) : PlaylistEvent()
    // 点赞评论事件，包含评论ID、索引和是否为楼层评论的标志
    data class AgreeComment(val id: Long, val index: Int, val isFloor: Boolean) : PlaylistEvent()
    // 打开楼层评论事件，包含评论ID和索引
    data class OpenFloorComment(val id: Long, val index: Int) : PlaylistEvent()
    // 更改评论事件，包含消息内容
    data class ChangeComment(val msg: String) : PlaylistEvent()
    // 更改楼层评论事件，包含消息内容
    data class ChangeFloorComment(val msg: String) : PlaylistEvent()
}