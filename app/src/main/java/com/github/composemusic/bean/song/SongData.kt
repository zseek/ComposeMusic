package com.github.composemusic.bean.song


data class SongData(
    val name: String? = null, // 歌曲名称
    val id: Long = 0, // 歌曲 ID
    val position: Int = 0, // 位置
    val alias: List<String>? = null, // 别名列表
    val status: Int = 0, // 状态
    val fee: Int = 0, // 版权费用
    val copyrightId: Int = 0, // 版权 ID
    val disc: String? = null, //  碟片
    val no: Int = 0, // 序号
    val artists: List<Artist>, // 艺术家列表
    val album: Album? = null, // 专辑信息
    val starred: Boolean = false, // 是否已收藏
    val popularity: Int = 0, // 流行度
    val score: Int = 0, // 得分
    val starredNum: Int = 0, // 收藏数量
    val duration: Int = 0, // 时长
    val playedNum: Int = 0, // 播放次数
    val dayPlays: Int = 0, // 日播放次数
    val hearTime: Int = 0, // 听歌时间
    val sqMusic: MusicInfo? = null, // 超高品质音乐信息
    val hrMusic: MusicInfo? = null, // hrMusic
    val ringtone: String? = null, // 铃声
//    @SerialName("crbt") val crbt: Any? = null, // crbt
//    @SerialName("audition") val audition: Any? = null, // 试听
    val copyFrom: String? = null, // 复制来源
    val commentThreadId: String? = null, // 评论线程 ID
    val rtUrl: String? = null, // rtUrl
    val ftype: Int = 0, // ftype
    val rtUrls: List<String>? = null, // rtUrls
    val copyright: Int = 0, // 版权
    val transName: String? = null, // 翻译名称
//   val sign: Any? = null, // 签名
    val mark: Int = 0, // 标记
    val originCoverType: Int = 0, // 原始封面类型
//    @SerialName("originSongSimpleData") val originSongSimpleData: Any? = null, // 原始歌曲简单数据 // 暂不使用
//    @SerialName("noCopyrightRcmd") val noCopyrightRcmd: Any? = null, // noCopyrightRcmd // 暂不使用
    val mvid: Int = 0, // MV ID
    val rtype: Int = 0, // rtype
    val rurl: String? = null, // rurl
    val hMusic: MusicInfo? = null, // 高品质音乐信息
    val mMusic: MusicInfo? = null, // 中品质音乐信息
    val lMusic: MusicInfo? = null, // 低品质音乐信息
    val bMusic: MusicInfo? = null, // bMusic
    val mp3Url: String? = null, // MP3 URL
    val extProperties: ExtProperties? = null, // 扩展属性
    val xInfo: XInfo? = null // xInfo
)


data class MusicInfo(
    val name: String? = null, // 音乐名称 (在提供的示例中似乎始终为 null)
    val id: Long = 0, // 音乐 ID
    val size: Int = 0, // 音乐文件大小（以字节为单位）
    val extension: String? = null, // 文件扩展名（例如，“mp3”）
    val sr: Int = 0, // 采样率（例如，44100 Hz）
    val dfsId: Int = 0, // DFS ID （在提供的示例中似乎始终为 0）
    val bitrate: Int = 0, // 比特率，单位为 kbps（例如，320000）
    val playTime: Int = 0, // 播放时长（以毫秒为单位）
    val volumeDelta: Double = 0.0, // 音量增量值
    val extProperties: ExtProperties? = null, // 扩展属性（在提供的示例中似乎始终为 null）
    val xInfo: XInfo? = null // 附加信息（在提供的示例中似乎始终为 null）
)


data class ExtProperties(
    val picIdStr: String? = null // 图片 ID 字符串
)


data class XInfo(
    val valpicIdStr: String? = null, // 图片 ID 字符串
)


data class Artist(
    val name: String, // 艺术家姓名
    val id: Int = 0, // 艺术家 ID
    val picId: Int = 0, // 图片 ID
    val img1v1Id: Int = 0, //1v1 图片 ID
    val briefDesc: String, // 简介
    val picUrl: String, // 图片 URL
    val img1v1Url: String, // 1v1 图片 URL
    val albumSize: Int = 0, // 专辑数量
    val alias: List<String>, // 别名列表
    val trans: String, // 翻译
    val musicSize: Int = 0, // 歌曲数量
    val topicPerson: Int = 0, // 主题人物
    val extProperties: ExtProperties? = null, // 扩展属性
    val xInfo: XInfo? = null // xInfo
)


data class Album(
    val name: String, // 专辑名称
    val id: Int = 0, // 专辑 ID
    val type: String, // 专辑类型
    val size: Int = 0, // 专辑大小
    val picId: Long = 0, // 图片 ID
    val blurPicUrl: String, // 模糊图片 URL
    val companyId: Int = 0, // 公司 ID
    val pic: Long = 0, // 图片
    val picUrl: String, // 图片 URL
    val publishTime: Long = 0, // 发布时间
    val description: String, // 描述
    val tags: String, // 标签
    val company: String, // 公司
    val briefDesc: String, // 简介
    val artist: Artist, // 艺术家
//    @SerialName("songs") val songs: List<Any>? = null, // 歌曲列表
    val alias: List<String>, // 别名列表
    val status: Int = 0, // 状态
    val copyrightId: Int = 0, // 版权 ID
    val commentThreadId: String, // 评论线程 ID
    val artists: List<Artist>, // 艺术家列表
    val subType: String, // 子类型
//    @SerialName("transName") val transName: Any? = null, // 翻译名称
    val onSale: Boolean = false, // 是否在售
    val mark: Int = 0, // 标记
    val gapless: Int = 0, // gapless
    val dolbyMark: Int = 0, // 杜比标记
    val extProperties: ExtProperties? = null, // 扩展属性
    val xInfo: XInfo? = null // xInfo
)