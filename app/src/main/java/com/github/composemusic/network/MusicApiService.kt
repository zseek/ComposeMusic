package com.github.composemusic.network

import com.github.composemusic.bean.BaseResponse
import com.github.composemusic.bean.albums.AlbumDetailBean
import com.github.composemusic.bean.albums.AlbumsBean
import com.github.composemusic.bean.albums.BaseAlbums
import com.github.composemusic.bean.albums.BaseProduct
import com.github.composemusic.bean.albums.DigitAlbumsBean
import com.github.composemusic.bean.albums.FavoriteAlbumBean
import com.github.composemusic.bean.artist.Artist
import com.github.composemusic.bean.artist.ArtistAlbumBean
import com.github.composemusic.bean.artist.ArtistBriefBean
import com.github.composemusic.bean.artist.ArtistMvBean
import com.github.composemusic.bean.artist.ArtistSongBean
import com.github.composemusic.bean.artist.BaseArtist
import com.github.composemusic.bean.comment.BaseCommentBean
import com.github.composemusic.bean.comment.FloorCommentBean
import com.github.composemusic.bean.comment.MlogCommentBean
import com.github.composemusic.bean.comment.SendCommentBean
import com.github.composemusic.bean.dj.DjRadioBean
import com.github.composemusic.bean.lrc.LyricInfoBean
import com.github.composemusic.bean.mv.MvDetailBean
import com.github.composemusic.bean.mv.MvUrlBean
import com.github.composemusic.bean.playlist.PlayListBean
import com.github.composemusic.bean.playlist.PlayListDetailBean
import com.github.composemusic.bean.playlist.Playlist
import com.github.composemusic.bean.pwdlogin.PwdLoginBean
import com.github.composemusic.bean.qrcode.QRCodeCookieBean
import com.github.composemusic.bean.qrcode.QRCodeImgBean
import com.github.composemusic.bean.qrcode.QRCodeKeyBean
import com.github.composemusic.bean.qrcode.UserAccountBean
import com.github.composemusic.bean.radio.BaseRadioProgram
import com.github.composemusic.bean.radio.BaseRadioStation
import com.github.composemusic.bean.radio.ProgramDetailBean
import com.github.composemusic.bean.radio.RecommendRadioBean
import com.github.composemusic.bean.radio.program.BaseProgram
import com.github.composemusic.bean.radio.program.NewHotRadioBean
import com.github.composemusic.bean.radio.program.ProgramRankBean
import com.github.composemusic.bean.rank.RankListBean
import com.github.composemusic.bean.recent.RecentBean
import com.github.composemusic.bean.recommend.newsongs.RecommendNewSongsBean
import com.github.composemusic.bean.recommend.playlist.RecommendPlaylistBean
import com.github.composemusic.bean.recommend.songs.RecommendSongsBean
import com.github.composemusic.bean.search.BaseSearch
import com.github.composemusic.bean.search.DefaultSearchBean
import com.github.composemusic.bean.search.HotSearchBean
import com.github.composemusic.bean.search.SearchSuggestionBean
import com.github.composemusic.bean.searchresult.SearchAlbumBean
import com.github.composemusic.bean.searchresult.SearchArtistBean
import com.github.composemusic.bean.searchresult.SearchDjBean
import com.github.composemusic.bean.searchresult.SearchMvBean
import com.github.composemusic.bean.searchresult.SearchPlaylistBean
import com.github.composemusic.bean.searchresult.SearchSongBean
import com.github.composemusic.bean.searchresult.SearchUserBean
import com.github.composemusic.bean.searchresult.SearchVideoBean
import com.github.composemusic.bean.song.BaseSong
import com.github.composemusic.bean.song.SongUrlBean
import com.github.composemusic.bean.song.Track
import com.github.composemusic.bean.user.UserDetailBean
import com.github.composemusic.bean.video.MlogInfoBean
import com.github.composemusic.bean.video.VideoBean
import com.github.composemusic.route.nav.recommend.playlist.RecommendResourceBean
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {
    /**********************************************************************************************/
    /** 手机密码登录*/
    @GET("/login/cellphone")
    suspend fun getPhoneLogin(@Query("phone") phone: String, @Query("password") password: String): PwdLoginBean

    /** 邮箱登录*/
    @GET("/login")
    suspend fun getEmailLogin(@Query("email") email: String, @Query("password") password: String): PwdLoginBean

    /** 二维码登录, 携带间戳防止缓存*/
    @GET("/login/qr/key") // 1: 生成一个key
    suspend fun getQRCodeLoginKey(@Query("timerstamp") timerstamp: String = System.currentTimeMillis().toString()): BaseResponse<QRCodeKeyBean>
    @GET("/login/qr/create") // 2: 传入key生成二维码图片的base64信息
    suspend fun getQRCodeLoginImg(@Query("key") key: String, @Query("qrimg") qrimg: Boolean = true, @Query("timerstamp") timerstamp: String = System.currentTimeMillis().toString()): BaseResponse<QRCodeImgBean>
    @GET("/login/qr/check") // 3: 轮询此接口获取扫码状态, 800为二维码过期, 801为等待扫码, 802为待确认, 803为授权登录成功(会返回cookie), 如扫码后返回502,则需加上noCookie参数,如&noCookie=true
    suspend fun getQRCodeLoginStatus(@Query("key") key: String, @Query("timerstamp") timerstamp: String = System.currentTimeMillis().toString()): QRCodeCookieBean

    /** 二维码登录后, 通过此接口获取用户信息 */
    @GET("/user/account")
    suspend fun getAccountInfo(@Query("cookie") cookie: String): UserAccountBean

    /**********************************************************************************************/

    /**获取用户详细信息*/
    @GET("/user/detail")
    suspend fun getUserDetailInfo(@Query("uid") uid: Long): UserDetailBean

    /** 获取用户歌单: 包含用户喜欢的歌曲、用户创建的歌单、用户收藏的歌单*/
    @GET("/user/playlist")
    suspend fun getPlayList(@Query("uid") uid: Long, @Query("cookie") cookie: String): PlayListBean

    /** 网易云推荐歌单 */
    @GET("/personalized")
    suspend fun getRecommendPlaylist(@Query("limit") limit: Int = 10): RecommendPlaylistBean

    /** 个人每日推荐歌单*/
    @GET("/recommend/resource")
    suspend fun getRecommendEveryDayPlaylist(@Query("cookie") cookie: String): RecommendResourceBean

    /** 推荐的新音乐*/
    @GET("/personalized/newsong")
    suspend fun getRecommendSongs(@Query("cookie") cookie: String, @Query("limit") limit: Int = 10): RecommendNewSongsBean

    /** 获取每日推荐歌曲,默认30首*/
    @GET("/recommend/songs")
    suspend fun getRecommendEveryDaySongs(@Query("cookie") cookie: String): BaseResponse<RecommendSongsBean>

    /** 最新专辑 */
    @GET("/album/newest")
    suspend fun getNewestAlbums(): BaseAlbums<List<AlbumsBean>>

    /** 最新数字专辑 */
    @GET("/album/list")
    suspend fun getDigitNewestAlbums(@Query("limit") limit: Int = 10): BaseProduct<List<DigitAlbumsBean>>

    /**
     * 获取数字专辑&数字单曲的榜单
     *@param type 榜单类型: daily:日榜,week:周榜,year:年榜,total:总榜
     * @param albumType 0为数字专辑;1 为数字单曲
     * @param limit 返回数量 , 默认为 30
     * @param offset 偏移数量，用于分页 , 如 :( 页数 -1)*30, 其中 30 为 limit 的值 , 默认为 0
     */
    @GET("/album/songsaleboard")
    suspend fun getDigitAlbumsRank(@Query("type") type: String = "daily", @Query("albumType") albumType: Int = 0): BaseProduct<List<DigitAlbumsBean>>

    /**
     * 获取歌手分类列表
     * @param type 取值: -1:全部 1:男歌手 2:女歌手 3:乐队
     * @param area 取值: -1:全部 7华语 96欧美 8:日本 16韩国 0:其他
     *@param limit 返回数量 , 默认为 30
     * @param offset 偏移数量，用于分页 , 如 :( 页数 -1)*30, 其中 30 为 limit 的值 , 默认为 0
     * @param initial 按首字母索引查找参数,如 /artist/list?type=1&area=96&initial=b 返回内容将以 name 字段开头为 b 或者拼音开头为 b 为顺序排列
     */
    @GET("/artist/list")
    suspend fun getAllArtists(@Query("type") type: Int = -1, @Query("area") area: Int = -1, @Query("limit") limit: Int = 20, @Query("offset") offset: Int = 0): BaseArtist

    /**可获取榜单详情*/
    @GET("/toplist/detail")
    suspend fun getTopListDetail(): RankListBean

    /**电台个性推荐列表, 默认为6条, 最多为6条*/
    @GET("/dj/personalize/recommend")
    suspend fun getRecommendRadioStation(@Query("cookie") cookie: String, @Query("limit") limit: Int = 6): BaseResponse<List<RecommendRadioBean>>

    /**
     * 热门电台
     * @param offset 偏移数量，用于分页 , 如 :( 页数 -1)*30, 其中 30 为 limit 的值 , 默认为 0
     * @param limit 返回数量 , 默认为 30
     */
    @GET("/dj/hot")
    suspend fun getHotRadioStation(@Query("cookie") cookie: String, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 10): BaseRadioStation<List<RecommendRadioBean>>

    /**
     * 电台节目榜
     * @param offset 偏移数量，用于分页 , 如 :( 页数 -1)*30, 其中 30 为 limit 的值 , 默认为 0
     * @param limit 返回数量 , 默认为 30
     */
    @GET("/dj/program/toplist")
    suspend fun getProgramRanking(@Query("cookie") cookie: String, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 20): BaseProgram<List<ProgramRankBean>>

    /**
     * 新晋电台榜/热门电台榜
     * @param type 榜单类型: new 为新晋电台榜,hot为热门电台榜
     * @param offset 偏移数量，用于分页 , 如 :( 页数 -1)*30, 其中 30 为 limit 的值 , 默认为 0
     * @param limit 返回数量 , 默认为 30
     * */
    @GET("/dj/toplist")
    suspend fun getNewHotProgramRanking(@Query("cookie") cookie: String, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 20, @Query("type") type: String): BaseProgram<List<NewHotRadioBean>>


    /**********************************************************************************************/

    /**默认搜索词*/
    @GET("/search/default")
    suspend fun getDefaultSearch(): BaseResponse<DefaultSearchBean>
    /**热搜列表*/
    @GET("/search/hot")
    suspend fun getHotSearch(): BaseSearch<HotSearchBean>
    /**搜索建议*/
    @GET("/search/suggest")
    suspend fun getSearchSuggestion(@Query("keywords") keywords: String): BaseSearch<SearchSuggestionBean>
    /**
     * 获取搜索结果: 传入搜索关键词可以搜索 音乐 / 专辑 / 歌手 / 歌单 / 用户
     * @param keywords 搜索关键词
     * @param limit 返回数量，默认为 30
     * @param offset 偏移数量，用于分页, 默认为 0
     * @param type 搜索类型，默认为 1 即单曲, 1: 单曲, 10: 专辑, 100: 歌手, 1000: 歌单, 1002: 用户, 1004: MV, 1006: 歌词, 1009: 电台, 1014: 视频, 1018:综合, 2000:声音
     * */
    @GET("/cloudsearch")
    suspend fun getSearchSongResult(@Query("keywords") keywords: String, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 30, @Query("type") type: Int = 1, ): BaseSearch<SearchSongBean>
    @GET("/cloudsearch")
    suspend fun getSearchAlbumResult(@Query("keywords") keywords: String, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 30, @Query("type") type: Int = 10, ): BaseSearch<SearchAlbumBean>
    @GET("/cloudsearch")
    suspend fun getSearchArtistResult(@Query("keywords") keywords: String, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 30, @Query("type") type: Int = 100, ): BaseSearch<SearchArtistBean>
    @GET("/cloudsearch")
    suspend fun getSearchPlaylistResult(@Query("keywords") keywords: String, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 30, @Query("type") type: Int = 1000, ): BaseSearch<SearchPlaylistBean>
    @GET("/cloudsearch")
    suspend fun getSearchUserResult(@Query("keywords") keywords: String, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 30, @Query("type") type: Int = 1002, ): BaseSearch<SearchUserBean>
    @GET("/cloudsearch")
    suspend fun getSearchMvResult(@Query("keywords") keywords: String, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 30, @Query("type") type: Int = 1004, ): BaseSearch<SearchMvBean>
    @GET("/cloudsearch")
    suspend fun getSearchDjResult(@Query("keywords") keywords: String, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 30, @Query("type") type: Int = 1009, ): BaseSearch<SearchDjBean>
    @GET("/cloudsearch")
    suspend fun getSearchVideoResult(@Query("keywords") keywords: String, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 30, @Query("type") type: Int = 1014, ): BaseSearch<SearchVideoBean>

    /**********************************************************************************************/

    /**
     * 获取音乐url
     * @param id 音乐id
     * @param level 播放音质等级
     * @param standard 标准,higher 较高,exhigh极高, lossless无损, hiresHi-Res, jyeffect高清环绕声, sky沉浸环绕声, jymaster超清母带
     */
    @GET("/song/url/v1")
    suspend fun getMusicUrl(@Query("id") id: Long, @Query("level") level: String = "jymaster"): BaseResponse<List<SongUrlBean>>

    /**获取歌曲详细信息*/
    @GET("/song/detail")
    suspend fun getMusicDetail(@Query("ids") ids: Long): BaseSong<List<Track>>

    /**歌曲歌词*/
    @GET("/lyric")
    suspend fun getMusicLyric(@Query("id") id: Long): LyricInfoBean

    /**获取歌单所有歌曲*/
    @GET("/playlist/track/all")
    suspend fun getPlaylistSongs(@Query("id") id: Long, @Query("offset") offset: Int = 0, @Query("offset") limit: Int = 20, ): BaseSong<List<Track>>

    @GET("/album")
    suspend fun getAlbumSongs(@Query("id") id: Long): AlbumDetailBean

    @GET("/playlist/detail")
    suspend fun getPlaylistDetail(@Query("id") id: Long): PlayListDetailBean

    /*****************************************歌手**************************************************/

    /**获取歌手部分信息*/
    @GET("/artist/detail")
    suspend fun getArtistDetailInfo(@Query("id") id: Long): BaseResponse<ArtistBriefBean>
    /**获取歌手热门50首歌曲*/
    @GET("/artists")
    suspend fun getArtistSongs(@Query("id") id: Long): ArtistSongBean
    /**获取歌手专辑*/
    @GET("/artist/album")
    suspend fun getArtistAlbums(@Query("id") id: Long, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 20): ArtistAlbumBean
    /**获取歌手MV*/
    @GET("/artist/mv")
    suspend fun getArtistMvs(@Query("id") id: Long): ArtistMvBean
    /**获取与歌手相似的其他歌手*/
    @GET("/simi/artist")
    suspend fun getSimilarArtist(@Query("id") id: Long, @Query("cookie") cookie: String): BaseArtist

    /**********************************************************************************************/

    /**获取电台详情-类似歌单*/
    @GET("/dj/detail")
    suspend fun getRadioStationDetail(@Query("rid") rid: Long): BaseResponse<ProgramDetailBean>

    /**获取电台音频-类似歌单中的歌曲*/
    @GET("/dj/program")
    suspend fun getRadioPrograms(@Query("rid") rid: Long, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 20): BaseRadioProgram

    /***************************************点赞评论收藏*********************************************/

    /**获取歌单评论*/
    @GET("/comment/playlist")
    suspend fun getPlaylistComments(@Query("id") id: Long, @Query("time") time: Long, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 20): BaseCommentBean

    /**获取歌曲评论*/
    @GET("/comment/music")
    suspend fun getSongComments(@Query("id") id: Long, @Query("time") time: Long, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 20): BaseCommentBean

    /**获取专辑评论*/
    @GET("/comment/album")
    suspend fun getAlbumComments(@Query("id") id: Long, @Query("time") time: Long, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 20): BaseCommentBean

    /**
     * 获取楼层评论
     * @param parentCommentId 楼层评论 id
     * @param id 资源 id
     * @param time 分页参数,取上一页最后一项的 time 获取下一页数据
     * @param type 资源类型, 0: 歌曲, 1: mv, 2: 歌单, 3: 专辑, 4: 电台节目, 5: 视频, 6: 动态, 7: 电台
     * @param offset 偏移数量,用于分页, 默认为 0
     * @param limit 返回数量, 默认为 20
     */
    @GET("/comment/floor")
    suspend fun getFloorComments(@Query("parentCommentId") parentCommentId: Long, @Query("id") id: String, @Query("time") time: Long, @Query("type") type: Int, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 20): BaseResponse<FloorCommentBean>

    /**
     * 为评论点赞
     * @param cid 评论 id
     * @param id 资源 id
     * @param t 是否点赞 , 1 为点赞 ,0 为取消点赞
     * @param type 资源类型, 0: 歌曲, 1: mv, 2: 歌单, 3: 专辑, 4: 电台节目, 5: 视频, 6: 动态, 7: 电台
     * 返回结果-successful:{code:200}
     * */
    @GET("/comment/like")
    suspend fun getAgreeComment(@Query("cid") cid: Long, @Query("id") id: String, @Query("t") t: Int, @Query("type") type: Int, ): BaseResponse<Any>

    /**
     * 资源点赞( MV,电台,视频)
     * @param id 资源 id
     * @param type 资源类型,0: 歌曲; 1: mv; 2: 歌单; 3: 专辑; 4: 电台节目; 5: 视频; 6: 动态; 7: 电台
     * @param t 1 为点赞,其他为取消点赞
     * */
    @GET("/resource/like")
    suspend fun getFavoriteResource(@Query("id") id: String, @Query("type") type: Int, @Query("t") t: Int): BaseResponse<Any>

    /**
     * 发送/删除 评论
     * @param id 资源 id
     * @param commentId 回复的评论 id (回复评论时必填)
     * @param t 发送/删除 评论, 1为发送, 0 删除
     * @param type 资源类型, 0: 歌曲, 1: mv, 2: 歌单, 3: 专辑, 4: 电台节目, 5: 视频, 6: 动态, 7: 电台
     * @param content 要发送的内容
     */
    @GET("/comment")
    suspend fun getSendComment(@Query("id") id: String, @Query("commentId") commentId: Long, @Query("t") t: Int, @Query("type") type: Int, @Query("content") content: String): SendCommentBean

    /**
     * 收藏 / 取消收藏 专辑
     * @param id 专辑 id
     * @param t 1 为收藏,其他为取消收藏
     */
    @GET("/album/sub")
    suspend fun getFavoriteAlbum(@Query("id") id: Long, @Query("t") t: Int)

    /**
     * 收藏 / 取消收藏 歌单
     * @param id 歌单 id
     * @param t 1 为收藏,其他为取消收藏
     */
    @GET("/playlist/subscribe")
    suspend fun getFavoritePlaylist(@Query("id") id: Long, @Query("t") t: Int)

    /**********************************************************************************************/

    /**最近播放-歌曲*/
    @GET("/record/recent/song")
    suspend fun getRecentSongs(@Query("cookie") cookie: String, @Query("limit") limit: Int = 30): BaseResponse<RecentBean<Track>>
    /**最近播放-视频*/
    @GET("/record/recent/video")
    suspend fun getRecentVideos(@Query("cookie") cookie: String, @Query("limit") limit: Int = 30): BaseResponse<RecentBean<Any>>
    /**最近播放-歌单*/
    @GET("/record/recent/playlist")
    suspend fun getRecentPlaylists(@Query("cookie") cookie: String, @Query("limit") limit: Int = 30): BaseResponse<RecentBean<Playlist>>
    /**最近播放-专辑*/
    @GET("/record/recent/album")
    suspend fun getRecentAlbums(@Query("cookie") cookie: String, @Query("limit") limit: Int = 30): BaseResponse<RecentBean<AlbumsBean>>
    /**最近播放-播客*/
    @GET("/record/recent/dj")
    suspend fun getRecentDjs(@Query("cookie") cookie: String, @Query("limit") limit: Int = 30): BaseResponse<RecentBean<DjRadioBean>>

    /**获取收藏的歌手列表*/
    @GET("/artist/sublist")
    suspend fun getFavoriteArtist(@Query("cookie") cookie: String, @Query("offset") offset: Int, @Query("limit") limit: Int): BaseResponse<List<Artist>>
    /**获取收藏的专辑列表*/
    @GET("/album/sublist")
    suspend fun getFavoriteAlbum(@Query("cookie") cookie: String, @Query("offset") offset: Int, @Query("limit") limit: Int): BaseResponse<List<FavoriteAlbumBean>>
    /**获取收藏的Mv列表*/
    @GET("/mv/sublist")
    suspend fun getFavoriteMvs(@Query("cookie") cookie: String, @Query("offset") offset: Int, @Query("limit") limit: Int): BaseResponse<List<VideoBean>>


    /**获取下载的歌曲URL*/
    @GET("/song/download/url")
    suspend fun getDownloadURL(@Query("cookie") cookie: String, @Query("id") id: Long, @Query("br") br: Int = 999000): BaseResponse<SongUrlBean>

    /*****************************************NV, Mlog*****************************************************/

    /**获取MV视频播放地址*/
    @GET("/mv/url")
    suspend fun getMvURL(@Query("id") id: Long, @Query("r") r: Int = 1080): BaseResponse<MvUrlBean>
    /**获取Mlog视频播放地址*/
    @GET("/mlog/url")
    suspend fun getMlogURL(@Query("id") id: String, @Query("res") res: Int = 1080): BaseResponse<MlogInfoBean>
    /**获取相似的MV*/
    @GET("/simi/mv")
    suspend fun getSimilarMvs(@Query("mvid") mvid: Long): SearchMvBean
    /**获取mv的详细信息*/
    @GET("/mv/detail")
    suspend fun getMvDetailInfo(@Query("mvid") mvid: Long): MvDetailBean
    /**获取mv的评论*/
    @GET("/comment/mv")
    suspend fun getMvComments(@Query("id") id: Long, @Query("time") time: Long, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 20): BaseCommentBean
    /**获取mlog的评论*/
    @GET("/comment/new")
    suspend fun getMlogComments(@Query("id") id: String, @Query("type") type: Int = 5, @Query("sortType") sortType: Int = 3, @Query("cursor") cursor: Long, @Query("pageNo") pageNo: Int = 0, @Query("pageSize") pageSize: Int = 20): BaseResponse<MlogCommentBean>
    /** 传入 mlog id, 可获取 video id, 然后可通过video/url 获取播放地址 */
    @GET("/mlog/to/video")
    suspend fun getVideoId(@Query("id") id: String): BaseResponse<String>

}      