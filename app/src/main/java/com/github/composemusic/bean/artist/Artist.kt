package com.github.composemusic.bean.artist

data class Artist(
    val id: Long,
    val name: String,
    val picUrl: String,
    val alias: List<String>,
    val albumSize: Int,
    val picId: Long,
    val fansGroup: Any,
    val img1v1Url: String,
    val accountId: Long,
    val img1v1: Long,
    val trans: String,

    val alg: String,
    val briefDesc: String,
    val fansCount: Int,
    val followed: Boolean,
    val info: String,
    val img1v1Id: Long,
    val img1v1Id_str: String,
    val isSubed: Any,
    val identifyTag: Any,
    val musicSize: Int,
    val mvSize: Int,
    val picId_str: String,
    val publishTime: Long,
    val showPrivateMsg: Boolean,
    val topicPerson: Int,
    val transNames: Any
)