package com.github.composemusic.navigation

import androidx.navigation.NavHostController
import com.github.composemusic.parm.Constants

class NavigationAction {
    companion object {

        fun onBack(navHostController: NavHostController) {
            navHostController.navigateUp() // 使用navigateUp返回上一个页面
        }

        fun toPwdLogin(navHostController: NavHostController) {
            navHostController.navigate(Screen.PasswordLoginPage.route) {
                // 使用popUpTo表示导航到目标页面时，将之前的页面全部出栈
                popUpTo(Screen.ContainerPage.route) {
                    inclusive = true // 目标页面也出栈
                }
            }
        }

        fun toQrcodeLogin(navHostController: NavHostController) {
            navHostController.navigate(Screen.QrCodeLoginPage.route)
        }

        fun toNavigation(navHostController: NavHostController) {
            navHostController.navigate(Screen.NavigationPage.route)
        }

        fun toContainer(navHostController: NavHostController) {
            navHostController.navigate(Screen.ContainerPage.route)
        }

        fun toDrawerItem(navHostController: NavHostController, route: String) {
            navHostController.navigate(route)
        }

        fun toSearch(navHostController: NavHostController) {
            navHostController.navigate(Screen.SearchPage.route)
        }

        fun toSearchResult(navHostController: NavHostController, key: String) {
            navHostController.navigate(Screen.SearchResultPage.route.plus("?key=$key"))
        }

        fun toUserDetail(navHostController: NavHostController, id: Long) {
            navHostController.navigate(Screen.UserProfile.route.plus("?ConsumerID=$id"))
        }

        fun toMusic(navHostController: NavHostController, musicID: Long) {
            navHostController.navigate(Screen.MusicPlayerPage.route.plus("?MusicID=$musicID"))
        }

        fun toPlaylist(navHostController: NavHostController, id: Long, isPlaylist: Boolean) {
            navHostController.navigate(Screen.PlaylistPage.route.plus("?PlaylistID=$id&IsPlaylist=$isPlaylist"))
        }

        fun toArtist(navHostController: NavHostController, id: Long) {
            navHostController.navigate(Screen.ArtistPage.route.plus("?ArtistID=$id"))
        }

        fun toRadioDetail(navHostController: NavHostController, id: Long) {
            navHostController.navigate(Screen.RadioStationDetailPage.route.plus("?RadioStationID=$id"))
        }

        fun toMvPage(navHostController: NavHostController, id: Long) {
            navHostController.navigate(Screen.MvPlayerPage.route.plus("?${Constants.MvID}=$id"))
        }

        fun toMlogPage(navHostController: NavHostController, id: String) {
            navHostController.navigate(Screen.MlogPlayerPage.route.plus("?${Constants.MlogID}=$id"))
        }
    }
}