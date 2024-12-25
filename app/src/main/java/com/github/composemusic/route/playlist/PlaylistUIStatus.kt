package com.github.composemusic.route.playlist

import com.github.composemusic.bean.comment.CommentBean
import com.github.composemusic.bean.song.Track

data class PlaylistUIStatus(
    val id: Long = 0L, // 歌单ID
    val songs: List<Track> = emptyList(), // 歌曲列表
    val cover: String = "", // 封面
    val name: String = "Unknown", // 歌单名
    val description: String = "Unknown", // 歌单描述
    val artist: String = "Unknown", // 歌单创建者
    val shareCount: Long = 0L, // 分享总数
    val favoriteCount: Long = 0L, // 收藏总数
    val commentCount: Long = 0L, // 评论总数
    val isPlaylist: Boolean = false, // 是否为歌单
    val isShowDialog: Boolean = false, // 是否显示对话框
    val isFollow: Boolean = false, // 是否已关注
    val tags: List<String> = emptyList(), // 标签
    val company: String = "Unknown", // 发行公司
    val type: String = "Unknown", // 歌单类型
)

data class CommentUIStatus(
    val commentCount: Long = 0L, // 评论总数
    val floorCommentCount: Long = 0L, // 楼层评论总数
    val commentStatus: NetworkStatus = NetworkStatus.Waiting, // 评论网络请求状态
    val floorCommentStatus: NetworkStatus = NetworkStatus.Waiting, // 楼层评论网络请求状态
    val ownFloorComment: CommentBean? = null, // 自己的楼层评论
    val floorComments: MutableList<CommentBean> = mutableListOf(), // 楼层评论
    val comments: MutableList<CommentBean> = mutableListOf(), // 评论
    val commentText: String = "", // 评论文本
    val floorCommentText: String = "" // 楼层评论文本
)
