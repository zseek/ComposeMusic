package com.github.composemusic.bean.song

data class Privilege(
    val id: Long, // 歌曲或资源 ID
    val fee: Int, // 费用类型 (0: 免费, 1: 付费)
    val payed: Int, // 已支付金额
    val realPayed: Int, // 实际支付金额
    val st: Int, //  歌曲状态
    val pl: Int, // 播放权限级别 (0: 无权限, 1: 低品质, 4: 高品质)
    val dl: Int, // 下载权限级别 (0: 无权限, 1: 低品质, 4: 高品质)
    val sp: Int, //  
    val cp: Int, // 
    val subp: Int, //  
    val cs: Boolean, // 是否云同步
    val maxbr: Int, // 最高码率
    val fl: Int, // 无损音质权限级别 (0: 无权限, 1: 有权限)
    val pc: Any?, // 
    val toast: Boolean, // 是否显示提示信息
    val flag: Int, //  
    val paidBigBang: Boolean, // 是否已购买 BigBang 权益
    val preSell: Boolean, // 是否预售
    val playMaxbr: Int, // 播放最高码率
    val downloadMaxbr: Int, // 下载最高码率
    val maxBrLevel: String, // 最高码率等级 (例如，standard, higher, exhigh, lossless, hires)
    val playMaxBrLevel: String, // 播放最高码率等级
    val downloadMaxBrLevel: String, // 下载最高码率等级
    val plLevel: String, // 播放权限等级 (例如，none, standard, higher, exhigh, lossless, hires)
    val dlLevel: String, // 下载权限等级
    val flLevel: String, // 无损音质权限等级
    val rscl: Int, // 
    val freeTrialPrivilege: FreeTrialPrivilege, // 免费试听权限
    val rightSource: Int, //权限来源
    val chargeInfoList: List<ChargeInfo>, // 计费信息列表
    val code: Int, // 返回码
    val message: String? // 返回消息
)

data class FreeTrialPrivilege(
    val resConsumable: Boolean, // 资源是否可消费
    val userConsumable: Boolean, // 用户是否可消费
    val listenType: Any?, // 试听类型
    val cannotListenReason: Int, // 无法试听原因
    val playReason: Any?, // 播放原因
    val freeLimitTagType: Any? // 免费限制标签类型
)

data class ChargeInfo(
    val rate: Int, // 码率
    val chargeUrl: Any?, // 计费 URL
    val chargeMessage: Any?, // 计费信息
    val chargeType: Int // 计费类型
)