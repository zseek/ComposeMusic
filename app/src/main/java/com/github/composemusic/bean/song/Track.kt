package com.github.composemusic.bean.song

// 未登录获取不到完整的歌曲信息
data class Track(
    val name: String, // 歌曲名称
    val id: Long, // 歌曲 ID
    val pst: Int, // 歌曲位置
    val t: Int, //  
    val ar: List<Ar>, // 歌手列表
    val alia: List<String>, // 歌曲别名
    val pop: Int, // 歌曲流行度
    val st: Int, //歌曲状态
    val rt: String, // 
    val fee: Int, // 歌曲费用类型
    val v: Int, // 歌曲版本
    val crbt: Any?, // 
    val cf: String, // 
    val al: Al, // 专辑信息
    val dt: Int, // 歌曲时长 (ms)
    val h: Quality?, // 高品质音频信息
    val m: Quality?, // 中品质音频信息
    val l: Quality?, // 低品质音频信息
    val sq: Quality?, // 超高品质音频信息(SQ)
    val hr: Quality?, // 无损品质音频信息 (HR)
    val a: Any?, // 
    val cd: String, // 专辑 CD 编号
    val no: Int, // 歌曲编号
    val rtUrl: Any?, // 
    val ftype: Int, // 文件类型
    val rtUrls: List<String>, // 
    val djId: Int, // DJ ID
    val copyright: Int, // 版权信息
    val s_id: Int, // 
    val mark: Long, // 
    val originCoverType: Int, // 原始封面类型
    val originSongSimpleData: OriginSongSimpleData?, // 原始歌曲简要信息
    val tagPicList: Any?, // 标签图片列表
    val resourceState: Boolean, // 资源状态
    val version: Int, // 歌曲版本号
    val songJumpInfo: Any?, // 
    val entertainmentTags: Any?, // 娱乐标签
    val awardTags: Any?, // 奖项标签
    val single: Int, // 
    val noCopyrightRcmd: Any?, //val alg: Any?, // 
    val displayReason: Any?, // 显示原因
    val rtype: Int, // 
    val rurl: Any?, // 
    val mst: Int, // 
    val cp: Int, // 
    val mv: Int, // MV ID
    val publishTime: Long, // 发布时间
    val videoInfo: VideoInfo, // 视频信息

    val reason: String, // 推荐理由
    val recommendReason: String, // 推荐理由
    val privilege: Privilege, //
    val alg: String // 推荐算法
)

// 未登录也可完整获取歌曲id信息
data class TrackId (
    val id: Long, // 歌曲 ID
    val v: Int, //  歌曲版本
    val t: Int, //
    val at: Long, // 添加时间
    val alg: String?, // 推荐算法
    val uid: Int, // 用户 ID
    val rcmdReason: String, // 推荐理由
    val sc: Any?, //
    val f: Any?, //
    val sr: Any?, //
    val dpr: Any? //
)

data class Quality(
    val br: Int, // 比特率
    val fid: Int, // 文件 ID
    val size: Int, // 文件大小
    val vd: Int, //
    val sr: Int // 采样率
)

data class OriginSongSimpleData(
    val songId: Int, // 原始歌曲 ID
    val name: String, // 原始歌曲名称
    val artists: List<Ar>, // 原始歌曲歌手列表
    val albumMeta: AlbumMeta // 原始歌曲专辑信息
)

data class AlbumMeta(
    val id: Int, // 专辑 ID
    val name: String // 专辑名称
)

data class VideoInfo(
    val moreThanOne: Boolean, // 是否有多个视频
    val video: Any? // 视频信息
)

data class Al(
    val id: Int,
    val name: String,
    val picUrl: String,
    val tns: List<Any>,
    val pic_str: String,
    val pic: Long
)

data class Ar(
    val id: Int,
    val name: String,
    val tns: List<Any>,
    val alias: List<Any>
)




