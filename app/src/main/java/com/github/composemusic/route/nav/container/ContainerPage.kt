package com.github.composemusic.route.nav.container

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.github.composemusic.R
import com.github.composemusic.navigation.Screen
import com.github.composemusic.route.nav.mine.MinePage
import com.github.composemusic.route.nav.radiostation.RadioStationPage
import com.github.composemusic.route.nav.rank.RankPage
import com.github.composemusic.route.nav.recommend.RecommendPage
import com.github.composemusic.tool.NoRippleTheme
import com.github.composemusic.ui.theme.ComposeMusicTheme
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.ui.BottomNavigation
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ContainerPager(
    viewModel: ContainerViewModel = hiltViewModel(),
    onSearch: () -> Unit,
    onDrawerItem: (String) -> Unit,
    onSongItem: (Long) -> Unit,
    onMinePlaylist: (Long) -> Unit, // 歌单条目点击事件
    onRecommendPlaylist: (Long) -> Unit,
    onAlbum: (Long) -> Unit,
    onArtist: (Long) -> Unit,
    onRadio: (Long) -> Unit,
    onRank: (Long) -> Unit,
    onLogout: () -> Unit,
    onUser: () -> Unit
) {
    val navHostController = rememberNavController()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val drawerState = DrawerState(DrawerValue.Closed)
    val scaffoldState = rememberScaffoldState(drawerState = drawerState)
    val userStatus = viewModel.userStatus.value

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            //顶部导航栏
            TopBar(
                isPlaying = userStatus.isPlaying,
                onDrawer = {
                    scope.launch {
                        //打开侧边栏
                        if (drawerState.isClosed) {
                            scaffoldState.drawerState.open()
                        }
                    }
                },
                onSearch = onSearch,
                onPrior = { viewModel.onEvent(ContainerEvent.Prior) },
                onPlayOrPause = { viewModel.onEvent(ContainerEvent.ChangePlayStatus) },
                onNext = { viewModel.onEvent(ContainerEvent.Next) }
            )
        },
        bottomBar = {
            //底部导航栏
            BottomBar(navHostController = navHostController)
        },
        drawerContent = {
            //侧边栏
            DrawerContent(
                imgURL = userStatus.imgUrl,
                nickname = userStatus.nickname,
                state = scrollState,
                checked = userStatus.isDark,
                onDrawerItem = onDrawerItem,
                onCheckedChange = { viewModel.onEvent(ContainerEvent.UIMode(it)) },
                onLogout = {
                    viewModel.onEvent(ContainerEvent.Logout)
                    onLogout()
                },
                onUser = onUser
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeMusicTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        NavHost(
            startDestination = Screen.MinePage.route,
            navController = navHostController
        ) {
            //我的
            composable(Screen.MinePage.route) { MinePage(onPlaylist = onMinePlaylist) }
            //榜单
            composable(Screen.RankPage.route) { RankPage(onRank = onRank) }
            //推荐
            composable(Screen.RecommendPage.route) {
                RecommendPage(
                    onSongItem = onSongItem,
                    onPlaylist = onRecommendPlaylist,
                    onAlbum = onAlbum,
                    onArtist = onArtist
                )
            }
            //电台
            composable(Screen.RadioStationPage.route) {
                RadioStationPage(
                    onRadio = onRadio,
                    onSongItem = onSongItem
                )
            }
        }
    }
}


//底部导航栏各界面元素
enum class BottomNavElement(@StringRes val title: Int, @StringRes val icon: Int, val route: String) {
    RecommendPage(R.string.bottom_recommend_page, R.drawable.icon_navigation_recommend, Screen.RecommendPage.route),
    RankPage(R.string.bottom_rank_page, R.drawable.icon_navigation_rank, Screen.RankPage.route),
    RadioStationPage(R.string.bottom_radiostation_page, R.drawable.icon_navigation_radiostation, Screen.RadioStationPage.route),
    MinePage(R.string.bottom_mine_page, R.drawable.icon_navigation_mine, Screen.MinePage.route)
}
@SuppressLint("ResourceType")
@Composable
private fun BottomBar(
    navHostController: NavHostController,
    elements: Array<BottomNavElement> = BottomNavElement.values()
) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState() // 获取当前导航栈
    val currentRoute = navBackStackEntry?.destination?.route // 获取当前路由
    val route = elements.map { it.route }
    if (currentRoute in route) {
        // 使用 CompositionLocalProvider 来提供无水波纹主题
        CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
            BottomNavigation(
                Modifier
                    .windowInsetsBottomHeight(WindowInsets.navigationBars.add(WindowInsets(bottom = 60.dp))), // 适配设备底部导航栏(一定使用add函数添加底边距)
                backgroundColor = ComposeMusicTheme.colors.bottomBar,
            ) {
                elements.forEach { // 遍历添加底部导航栏元素
                    BottomNavigationItem(
                        selected = currentRoute == it.route,
                        icon = { Icon(painter = painterResource(id = it.icon), contentDescription = it.route, modifier = Modifier.size(36.dp).padding(4.dp)) },
                        label = { Text(text = stringResource(id = it.title), style = MaterialTheme.typography.overline)},
                        alwaysShowLabel = true,
                        selectedContentColor = ComposeMusicTheme.colors.selectIcon,
                        unselectedContentColor = ComposeMusicTheme.colors.unselectIcon,
                        onClick = {
                            navHostController.navigate(it.route) {
                                // 当用户选择子项时在返回栈中弹出到导航图中的起始目的地, 来避免太过臃肿的目的地堆栈
                                navHostController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) { saveState = true }
                                }
                                launchSingleTop = true // 当重复选择相同项时避免相同目的地的多重拷贝
                                restoreState = true // 当重复选择之前已经选择的项时恢复状态
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * 顶部导舏栏
 * @param isPlaying 是否正在播放
 * @param onDrawer 打开侧边栏
 * @param onSearch 打开搜索界面
 * @param onPlayOrPause 播放或暂停
 * @param onNext 下一首
 * @param onPrior 上一首
 */
@Composable
private fun TopBar(isPlaying: Boolean, onDrawer: () -> Unit, onSearch: () -> Unit, onPlayOrPause: () -> Unit, onNext: () -> Unit, onPrior: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ComposeMusicTheme.colors.background)
            .padding(top = 10.dp, start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "OpenDrawer",
            tint = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier.size(32.dp).clickable { onDrawer() }
        )
        Surface(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).padding(start = 10.dp, end = 10.dp)
        ) {
            MusicBar(isPlaying = isPlaying, onNext = onNext, onPlayOrPause = onPlayOrPause, onPrior = onPrior)
        }
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier.size(32.dp).clickable { onSearch() }
        )
    }
}

@Composable
private fun MusicBar(isPlaying: Boolean = false, onPlayOrPause: () -> Unit, onNext: () -> Unit, onPrior: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ComposeMusicTheme.colors.backgroundBottom)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_prior),
            contentDescription = "上一首",
            tint = ComposeMusicTheme.colors.background,
            modifier = Modifier
                .size(24.dp)
                .clickable { onPrior() }
        )
        Icon(
            painter = painterResource(id = if (isPlaying) R.drawable.icon_play else R.drawable.icon_stop),
            contentDescription = "播放/暂停",
            tint = ComposeMusicTheme.colors.background,
            modifier = Modifier
                .size(24.dp)
                .clickable { onPlayOrPause() }
        )
        Icon(
            painter = painterResource(id = R.drawable.icon_next),
            contentDescription = "下一首",
            tint = ComposeMusicTheme.colors.background,
            modifier = Modifier
                .size(24.dp)
                .clickable { onNext() }
        )
    }
}


//侧边栏
enum class DrawerElement(@StringRes val title: Int, @StringRes val icon: Int, val route: String) {
    PersonalInfo(R.string.drawer_personal_info, R.drawable.icon_navigation_mine, Screen.UserProfile.route),
    PlayList(R.string.drawer_player_ist, R.drawable.icon_drawer_playerlist, Screen.Playback.route),
    RecentPlay(R.string.drawer_recent_play, R.drawable.icon_drawer_recentplay, Screen.RecentPlay.route),
    Downloading(R.string.drawer_downloading, R.drawable.icon_drawer_downloading, Screen.Download.route),
    Favorite(R.string.drawer_favorite, R.drawable.icon_drawer_favorite, Screen.Favorite.route),
    Setting(R.string.drawer_setting, R.drawable.icon_drawer_setting, Screen.Setting.route),
    About(R.string.drawer_about, R.drawable.icon_drawer_about, Screen.About.route)
}
@Composable
private fun DrawerContent(
    imgURL: String,
    nickname: String,
    state: ScrollState,
    checked: Boolean = false,
    elements: Array<DrawerElement> = DrawerElement.values(),
    onCheckedChange: (Boolean) -> Unit,
    onDrawerItem: (String) -> Unit,
    onLogout: () -> Unit,
    onUser: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeMusicTheme.colors.background)
            .verticalScroll(state)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imgURL,
            contentDescription = nickname,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(20.dp))
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = nickname,
            style = MaterialTheme.typography.body2,
            color = ComposeMusicTheme.colors.textContent
        )
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            items(elements.size) {
                //跳转到对应的Drawer子项界面
                DrawerItem(element = elements[it], onDrawerItem = onDrawerItem, onUser = onUser)
                Spacer(modifier = Modifier.height(10.dp))
            }
            /**深色模式*/
            item {
                Surface(
                    color = ComposeMusicTheme.colors.backgroundTop,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_drawer_dark),
                            contentDescription = stringResource(id = R.string.drawer_dark),
                            tint = ComposeMusicTheme.colors.textTitle,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(id = R.string.drawer_dark),
                            style = MaterialTheme.typography.body2,
                            color = ComposeMusicTheme.colors.textTitle
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        androidx.compose.material3.Switch(
                            checked = checked,
                            onCheckedChange = onCheckedChange,
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            /**退出登录*/
            item {
                TextButton(
                    onClick = { onLogout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, start = 20.dp, end = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(ComposeMusicTheme.colors.grayBackground)
                        .padding(5.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.login_out),
                        style = MaterialTheme.typography.body2,
                        color = ComposeMusicTheme.colors.highlightColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

    }
}

@SuppressLint("ResourceType")
@Composable
private fun DrawerItem(
    element: DrawerElement,
    onDrawerItem: (String) -> Unit,
    onUser: () -> Unit
) {
    Surface(
        color = ComposeMusicTheme.colors.backgroundTop,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    if (element.route == Screen.UserProfile.route)
                        onUser()
                    else
                        onDrawerItem(element.route)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = element.icon),
                contentDescription = stringResource(id = element.title),
                tint = ComposeMusicTheme.colors.textTitle,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(id = element.title),
                style = MaterialTheme.typography.body2,
                color = ComposeMusicTheme.colors.textTitle
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "More",
                tint = ComposeMusicTheme.colors.defaultIcon,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 5.dp),
            )
        }
    }
}







@Preview
@Composable
private fun MusicBar() {
    MusicBar()
}