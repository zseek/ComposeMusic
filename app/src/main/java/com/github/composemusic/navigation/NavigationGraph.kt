package com.github.composemusic.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.composemusic.APP
import com.github.composemusic.parm.Constants
import com.github.composemusic.route.artist.ArtistDetailPage
import com.github.composemusic.route.drawer.about.AboutPage
import com.github.composemusic.route.drawer.download.DownloadMusicPage
import com.github.composemusic.route.drawer.favorite.FavoritePage
import com.github.composemusic.route.drawer.playback.PlaybackPage
import com.github.composemusic.route.drawer.recent.RecentPlayPage
import com.github.composemusic.route.drawer.setting.SettingPage
import com.github.composemusic.route.drawer.user.UserProfilePage
import com.github.composemusic.route.login.pwdlogin.PasswordLoginPage
import com.github.composemusic.route.login.qrcode.QrCodeLoginPage
import com.github.composemusic.route.musicplayer.MusicPlayerPage
import com.github.composemusic.route.nav.container.ContainerPager
import com.github.composemusic.route.playlist.PlaylistPage
import com.github.composemusic.route.radio.RadioDetailPage
import com.github.composemusic.route.search.SearchPage
import com.github.composemusic.route.searchresult.SearchResultPage
import com.github.composemusic.route.video.mlog.MlogPlayerPage
import com.github.composemusic.route.video.mv.MvPlayerPage
import com.github.composemusic.tool.SharedPreferencesUtil

@Composable
fun NavigationGraph(
    navHostController: NavHostController,
    startDistance: String
) {
    NavHost(navController = navHostController, startDestination = startDistance) { // 起始路径根据cookie登录判断

        //密码登陆
        composable(Screen.PasswordLoginPage.route) {
            PasswordLoginPage(
                onQrcode = {
                    //跳转到二维码登录界面
                    NavigationAction.toQrcodeLogin(navHostController)
                },
                onLoginSuccess = {
                    //登录成功，跳转到主界面(默认"我的"的界面)
                    SharedPreferencesUtil.instance.putValue(
                        APP.context,
                        Constants.LoginMode,
                        Constants.PhoneLoginMode
                    )
                    NavigationAction.toContainer(navHostController)
                }
            )
        }
        //二维码登陆
        composable(Screen.QrCodeLoginPage.route) {
            QrCodeLoginPage(
                onBack = { NavigationAction.onBack(navHostController) },
                onNavigation = {
                    SharedPreferencesUtil.instance.putValue(
                        APP.context,
                        Constants.LoginMode,
                        Constants.QRCodeLoginMode
                    )
                    NavigationAction.toContainer(navHostController)
                }
            )
        }

        // 主界面
        composable(Screen.ContainerPage.route) {
            ContainerPager(
                onSearch = { NavigationAction.toSearch(navHostController) },
                onDrawerItem = { NavigationAction.toDrawerItem(navHostController, it) },
                onSongItem = { NavigationAction.toMusic(navHostController, it) },
                onMinePlaylist = { id ->
                    NavigationAction.toPlaylist(
                        navHostController = navHostController,
                        id = id,
                        isPlaylist = true
                    )
                },
                onRecommendPlaylist = { id ->
                    NavigationAction.toPlaylist(
                        navHostController = navHostController,
                        id = id,
                        isPlaylist = true
                    )
                },
                onAlbum = { id ->
                    NavigationAction.toPlaylist(
                        navHostController = navHostController,
                        id = id,
                        isPlaylist = false
                    )
                },
                onArtist = { id ->
                    NavigationAction.toArtist(navHostController, id)
                },
                onRadio = { id ->
                    NavigationAction.toRadioDetail(navHostController, id)
                },
                onRank = { id ->
                    NavigationAction.toPlaylist(
                        navHostController = navHostController,
                        id = id,
                        isPlaylist = true
                    )
                },
                onLogout = {
                    NavigationAction.toPwdLogin(navHostController)
                },
                onUser = {
                    var id = SharedPreferencesUtil.instance.getValue(APP.context, Constants.UserId, 0L) as Long
                    Log.d("ContainerPager", "SharedPreferences读取id: $id")
                    id = APP.userId
                    Log.d("ContainerPager", "APP本次读取id: $id")
                    NavigationAction.toUserDetail(navHostController, id)
                }
            )
        }

        //搜索界面
        composable(Screen.SearchPage.route) {
            SearchPage(
                onBack = {
                    NavigationAction.onBack(navHostController)
                },
                onSearch = { key ->
                    NavigationAction.toSearchResult(navHostController, key)
                }
            )
        }

        //搜索结果界面
        composable(
            route = Screen.SearchResultPage.route.plus("?key={key}"),
            arguments = listOf(
                navArgument(name = "key") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            SearchResultPage(
                onSongItem = {
                    NavigationAction.toMusic(navHostController, it)
                },
                onAlbumItem = {
                    NavigationAction.toPlaylist(
                        navHostController = navHostController,
                        id = it,
                        isPlaylist = false
                    )
                },
                onArtistItem = { NavigationAction.toArtist(navHostController, it) },
                onDjItem = { NavigationAction.toRadioDetail(navHostController, it) },
                onPlaylistItem = {
                    NavigationAction.toPlaylist(
                        navHostController = navHostController,
                        id = it,
                        isPlaylist = true
                    )
                },
                onItemMv = { NavigationAction.toMvPage(navHostController, it) },
                onUser = { NavigationAction.toUserDetail(navHostController, it) },
                onBack = { NavigationAction.onBack(navHostController) }
            )
        }

        //音乐播放页面
        composable(
            route = Screen.MusicPlayerPage.route.plus("?MusicID={MusicID}"),
            arguments = listOf(
                navArgument(name = Constants.MusicID) {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            MusicPlayerPage {
                NavigationAction.onBack(navHostController)
            }
        }

        // 歌单界面
        composable(
            route = Screen.PlaylistPage.route.plus("?PlaylistID={PlaylistID}&IsPlaylist={IsPlaylist}"),
            arguments = listOf(
                navArgument(name = Constants.PlaylistID) {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument(name = Constants.IsPlaylist) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            PlaylistPage(
                onBack = { NavigationAction.onBack(navHostController) },
                onSongItem = { NavigationAction.toMusic(navHostController, it) }
            )
        }

        // 歌手详情界面
        composable(
            route = Screen.ArtistPage.route.plus("?ArtistID={ArtistID}"),
            arguments = listOf(
                navArgument(name = Constants.ArtistID) {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            ArtistDetailPage(
                onBack = { NavigationAction.onBack(navHostController) },
                onItemMv = { NavigationAction.toMvPage(navHostController, it) },
                onSongItem = { NavigationAction.toMusic(navHostController, it) },
                onArtist = {
                    navHostController.navigateUp()
                    NavigationAction.toArtist(navHostController, it)
                },
                onAlbumItem = {
                    NavigationAction.toPlaylist(
                        navHostController = navHostController,
                        id = it,
                        isPlaylist = false
                    )
                }
            )
        }

        // 电台详情界面
        composable(
            route = Screen.RadioStationDetailPage.route.plus("?RadioStationID={RadioStationID}"),
            arguments = listOf(
                navArgument(name = Constants.RadioStationID) {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            RadioDetailPage(
                onBack = { NavigationAction.onBack(navHostController) },
                onSongItem = { NavigationAction.toMusic(navHostController, it) }
            )
        }

      /*********************左侧抽屉栏***************************/
        //用户信息
        composable(
            route = Screen.UserProfile.route.plus("?ConsumerID={ConsumerID}"),
            arguments = listOf(
                navArgument(name = Constants.ConsumerID) {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            UserProfilePage {
                NavigationAction.onBack(navHostController)
            }
        }

        //当前播放歌单
        composable(Screen.Playback.route) {
            PlaybackPage(
                onSongItem = { NavigationAction.toMusic(navHostController, it) },
                onBack = { NavigationAction.onBack(navHostController) }
            )
        }

        //最近播放
        composable(Screen.RecentPlay.route) {
            RecentPlayPage(
                onSongItem = { NavigationAction.toMusic(navHostController, it) },
                onAlbumItem = {
                    NavigationAction.toPlaylist(
                        navHostController = navHostController,
                        id = it,
                        isPlaylist = false
                    )
                },
                onDjItem = { NavigationAction.toRadioDetail(navHostController, it) },
                onPlaylistItem = {
                    NavigationAction.toPlaylist(
                        navHostController = navHostController,
                        id = it,
                        isPlaylist = true
                    )
                },
                onMvItem = { NavigationAction.toMvPage(navHostController, it) },
                onMlogItem = { NavigationAction.toMlogPage(navHostController, it) },
                onBack = { NavigationAction.onBack(navHostController) }
            )
        }

        //收藏
        composable(Screen.Favorite.route) {
            FavoritePage(
                onBack = { NavigationAction.onBack(navHostController) },
                onArtist = { NavigationAction.toArtist(navHostController, it) },
                onAlbum = {
                    NavigationAction.toPlaylist(
                        navHostController = navHostController,
                        id = it,
                        isPlaylist = false
                    )
                },
                onVideo = { NavigationAction.toMvPage(navHostController, it.toLong()) }
            )
        }

        //关于
        composable(Screen.About.route) {
            AboutPage { NavigationAction.onBack(navHostController) }
        }

        //设置
        composable(Screen.Setting.route) {
            SettingPage { NavigationAction.onBack(navHostController) }
        }

        //下载
        composable(Screen.Download.route) {
            DownloadMusicPage(
                onBack = { NavigationAction.onBack(navHostController) },
                onSongItem = { NavigationAction.toMusic(navHostController, it) }
            )
        }

        //MV
        composable(
            route = Screen.MvPlayerPage.route.plus("?MvID={MvID}"),
            arguments = listOf(
                navArgument(name = Constants.MvID) {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            MvPlayerPage(
                onBack = { NavigationAction.onBack(navHostController) },
                onItemMv = {
                    navHostController.navigateUp()
                    NavigationAction.toMvPage(navHostController, it)
                }
            )
        }

        //Mlog
        composable(
            route = Screen.MlogPlayerPage.route.plus("?MlogID={MlogID}"),
            arguments = listOf(
                navArgument(name = Constants.MlogID) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            MlogPlayerPage { NavigationAction.onBack(navHostController) }
        }
    }
}

