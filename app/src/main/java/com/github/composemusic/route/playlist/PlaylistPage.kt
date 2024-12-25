package com.github.composemusic.route.playlist

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.github.composemusic.R
import com.github.composemusic.bean.song.Track
import com.github.composemusic.route.nav.recommend.playlist.transformNum
import com.github.composemusic.tool.CommentBottomSheet
import com.github.composemusic.tool.FloorCommentBottomSheet
import com.github.composemusic.tool.Loading
import com.github.composemusic.ui.theme.ComposeMusicTheme
import com.google.accompanist.insets.imePadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun PlaylistPage(viewModel: PlaylistViewModel = hiltViewModel(), onBack: () -> Unit, onSongItem: (Long) -> Unit) {

    val value = viewModel.uiStatus.value
    val commentValue = viewModel.commentStatus.value
    val maxOffset = with(LocalDensity.current) {
        (viewModel.maxTopBarHeight.roundToPx().toFloat() - viewModel.minTopBarHeight.roundToPx()
            .toFloat())
    }
    val offset = remember { mutableFloatStateOf(0f) }
    val infiniteOffset = remember { mutableFloatStateOf(0f) }
    val isShowTitle = remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val commentListState = rememberLazyListState()
    val floorCommentListState = rememberLazyListState()

    /**判断评论列表是否加载到了最后一项*/
    val commentLoadMore = remember {
        derivedStateOf {
            commentListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == commentListState.layoutInfo.totalItemsCount - 1
        }
    }
    val floorCommentLoadMore = remember {
        derivedStateOf {
            floorCommentListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == floorCommentListState.layoutInfo.totalItemsCount - 1
        }
    }

    // 收集事件发出的状态
    LaunchedEffect(key1 = scaffoldState.snackbarHostState) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is PlaylistStatus.TransformResult -> {
                    scaffoldState.snackbarHostState.showSnackbar(it.msg)
                }
                is PlaylistStatus.NetworkFailed -> {
                    scaffoldState.snackbarHostState.showSnackbar(it.msg)
                }
                is PlaylistStatus.Without -> {
                    scaffoldState.snackbarHostState.showSnackbar(it.msg)
                }
                is PlaylistStatus.OpenComment -> {
                    scaffoldState.bottomSheetState.expand()
                }
                is PlaylistStatus.CommentResult -> {
                    scaffoldState.snackbarHostState.showSnackbar(it.msg)
                }
            }
        }
    }

    // 如果评论列表滑动到了最后一项则发起网络请求继续(评论)
    LaunchedEffect(key1 = commentLoadMore) {
        snapshotFlow { commentLoadMore.value }
            .distinctUntilChanged()
            .collect {
                if (it) {
                    viewModel.onEvent(PlaylistEvent.NextCommentPage)
                }
            }
    }

    // 如果评论列表滑动到了最后一项则发起网络请求继续(对评论的回复)
    LaunchedEffect(key1 = floorCommentLoadMore) {
        snapshotFlow { floorCommentLoadMore.value }
            .distinctUntilChanged()
            .collect {
                if (it) {
                    viewModel.onEvent(PlaylistEvent.NextFloorCommentPage)
                }
            }
    }

    // 嵌套滚动连接器
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset { // available可用滚动偏移量, source滚动事件来源
                // 滚动到顶部时, 不再向上滚动
                val newOffset = offset.floatValue + available.y
                val newInOffset = infiniteOffset.floatValue + available.y
                offset.floatValue = newOffset.coerceIn(-maxOffset, 0f) // 限制偏移量在[-maxOffset, 0]之间
                infiniteOffset.floatValue = newInOffset.coerceIn(-(maxOffset * 1.5f), 0f) // 限制偏移量在[-maxOffset*1.5, 0]之间
                isShowTitle.value = -offset.floatValue.roundToInt() == maxOffset.roundToInt() // 决定工具栏标题显示/折叠
                return Offset.Zero
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(hostState = it) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding()
                )
            }
        },
        sheetContent = {
            when (viewModel.bottomSheetScreen.value) {
                is BottomSheetScreen.PlaylistComments -> {
                    CommentBottomSheet(
                        state = commentListState,
                        text = commentValue.commentText,
                        status = commentValue.commentStatus,
                        commentCount = commentValue.commentCount,
                        cover = value.cover,
                        name = value.name,
                        artist = value.artist,
                        comments = viewModel.comments,
                        onAgreeComment = { id, index ->
                            viewModel.onEvent(PlaylistEvent.AgreeComment(id, index, false))
                        },
                        onFloorComment = { id, index ->
                            viewModel.onEvent(PlaylistEvent.OpenFloorComment(id, index))
                        },
                        onSend = { viewModel.onEvent(PlaylistEvent.SendComment) },
                        onValueChange = { viewModel.onEvent(PlaylistEvent.ChangeComment(it)) }
                    )
                }
                is BottomSheetScreen.FloorComments -> {
                    FloorCommentBottomSheet(
                        state = floorCommentListState,
                        text = commentValue.floorCommentText,
                        status = commentValue.floorCommentStatus,
                        commentCount = commentValue.floorCommentCount,
                        ownComment = commentValue.ownFloorComment,
                        comments = viewModel.floorComments,
                        onAgreeComment = { id, index ->
                            viewModel.onEvent(PlaylistEvent.AgreeComment(id, index, true))
                        },
                        onSend = { viewModel.onEvent(PlaylistEvent.SendFloorComment(it)) },
                        onValueChange = { viewModel.onEvent(PlaylistEvent.ChangeFloorComment(it)) }
                    )
                }
                else -> {}
            }
        },
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetBackgroundColor = ComposeMusicTheme.colors.background,
        sheetPeekHeight = 0.dp,
        modifier = Modifier
            .pointerInput(Unit) {
                //点击BottomSheet以外的区域，则关闭当前sheet
                detectTapGestures(onTap = {
                    scope.launch {
                        if (scaffoldState.bottomSheetState.isExpanded)
                            scaffoldState.bottomSheetState.collapse()
                    }
                })
            }
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeMusicTheme.colors.background)
                .nestedScroll(nestedScrollConnection)
                .navigationBarsPadding()
        ) {
            PlaylistList(
                isPlaylist = value.isPlaylist,
                maxTopBarHeight = viewModel.maxTopBarHeight,
                songs = value.songs,
                onSongItem = { index, id ->
                    viewModel.onEvent(PlaylistEvent.PlayMusicItem(index, id))
                    onSongItem(id)
                }
            )
            FlexibleTopBar(
                title = if (value.isPlaylist) "歌单" else "专辑",
                isShowTitle = isShowTitle,
                cover = value.cover,
                albumName = value.name,
                expendHeight = viewModel.maxTopBarHeight,
                collapseHeight = viewModel.minTopBarHeight,
                offset = offset,
                onBack = onBack
            )
            PlaylistInfo(
                maxHeight = viewModel.maxTopBarHeight,
                minHeight = viewModel.minTopBarHeight,
                cover = value.cover,
                name = value.name,
                artist = value.artist,
                description = value.description,
                shareCount = value.shareCount,
                commentCount = value.commentCount,
                favoriteCount = value.favoriteCount,
                isFollow = value.isFollow,
                offset = infiniteOffset,
                onShowDialog = { viewModel.onEvent(PlaylistEvent.IsShowDialog) },
                onComment = { viewModel.onEvent(PlaylistEvent.OpenPlaylistComment) }
            )
            PlaylistDialog(
                cover = value.cover,
                name = value.name,
                tags = value.tags,
                company = value.company,
                subType = value.type,
                description = value.description,
                isPlaylist = value.isPlaylist,
                isShowDialog = value.isShowDialog,
                onDismiss = { viewModel.onEvent(PlaylistEvent.IsShowDialog) },
                onSave = { viewModel.onEvent(PlaylistEvent.SavePhoto) }
            )
        }
    }
}

// 歌单界面下部分中显示歌曲列表
@Composable
private fun PlaylistList(
    isPlaylist: Boolean,
    maxTopBarHeight: Dp,
    songs: List<Track>,
    onSongItem: (Int, Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .clip(RoundedCornerShape(topEnd = 40.dp, topStart = 40.dp))
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
            .background(ComposeMusicTheme.colors.background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = maxTopBarHeight + (10).dp)
    ) {
        //数据未加载完成时，显示loading
        if (songs.isEmpty()) {
            item { Loading() }
        }
        if (isPlaylist) {
            //显示歌单列表
            itemsIndexed(songs) { index, item ->
                PlaylistSongsItem(bean = item, onSongItem = { onSongItem(index, it) })
                if (index < songs.size - 1) {
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        } else {
            //显示专辑列表
            itemsIndexed(songs) { index, item ->
                AlbumSongsItem(
                    order = index + 1,
                    bean = item,
                    onSongItem = { onSongItem(index, it) }
                )
                if (index < songs.size - 1) {
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}

// TopBar部分
@Composable
fun FlexibleTopBar(
    title: String,
    isShowTitle: MutableState<Boolean>,
    cover: String,
    albumName: String,
    expendHeight: Dp, //展开时的高度
    collapseHeight: Dp, //折叠时的高度
    offset: MutableState<Float>,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(expendHeight)
            .offset { IntOffset(x = 0, y = offset.value.roundToInt()) }
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = cover,
            contentDescription = "背景",
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(collapseHeight * 2)
        )
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .offset { IntOffset(x = 0, y = -offset.value.roundToInt()) } //让标题栏始终保持在顶部
                .fillMaxWidth()
                .height(collapseHeight)
                .statusBarsPadding()
                .padding(start = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "返回",
                tint = ComposeMusicTheme.colors.white,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBack() }
            )

            AnimatedVisibility(visible = isShowTitle.value, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = albumName,
                    style = MaterialTheme.typography.subtitle1.copy(fontFamily = FontFamily.Default),
                    color = ComposeMusicTheme.colors.white,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnimatedVisibility(visible = !isShowTitle.value, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                    color = ComposeMusicTheme.colors.white,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// 歌单界面上部分中显示的歌单信息
@Composable
fun PlaylistInfo(
    maxHeight: Dp,
    minHeight: Dp,
    cover: String,
    name: String,
    artist: String,
    description: String,
    shareCount: Long,
    commentCount: Long,
    favoriteCount: Long,
    isFollow: Boolean,
    offset: MutableState<Float>,
    onShowDialog: () -> Unit,
    onComment: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = minHeight, start = 20.dp, end = 20.dp, bottom = 20.dp)
            .height(maxHeight - minHeight)
            .offset { IntOffset(x = 0, y = offset.value.roundToInt()) },
    ) {
        val (picRes, nameRes, artistRes, descriptionRes, othersRes) = createRefs()
        AsyncImage(
            model = cover,
            contentDescription = "歌单图",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .size((LocalConfiguration.current.screenWidthDp / 3).dp)
                .constrainAs(picRes) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        )
        Text(
            text = name,
            style = MaterialTheme.typography.subtitle1.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default
            ),
            color = ComposeMusicTheme.colors.white,
            maxLines = 1,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(nameRes) {
                start.linkTo(picRes.end, 10.dp)
                end.linkTo(parent.end)
                top.linkTo(parent.top, 10.dp)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = artist,
            style = MaterialTheme.typography.subtitle2.copy(fontFamily = FontFamily.Default),
            color = ComposeMusicTheme.colors.white,
            maxLines = 1,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(artistRes) {
                start.linkTo(picRes.end, 10.dp)
                end.linkTo(parent.end)
                top.linkTo(nameRes.bottom, 5.dp)
                width = Dimension.fillToConstraints
            }
        )

        AnimatedVisibility(
            visible = description.isNotEmpty(),
            modifier = Modifier.constrainAs(descriptionRes) {
                start.linkTo(picRes.end, 10.dp)
                end.linkTo(parent.end)
                top.linkTo(artistRes.bottom, 20.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.caption,
                    color = ComposeMusicTheme.colors.white.copy(alpha = 0.8f),
                    maxLines = 2,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Expend",
                    tint = ComposeMusicTheme.colors.white.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(20.dp)
//                        .clickable { onShowDialog() }
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(othersRes) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(descriptionRes.bottom, 5.dp)
                bottom.linkTo(parent.bottom, 5.dp)
            }
        ) {
            ShareCommentFavorite(
                modifier = Modifier.weight(1f),
                icon = R.drawable.icon_share,
                defaultText = "分享",
                count = shareCount,
                onClick = {} // 待完成
            )
            ShareCommentFavorite(
                modifier = Modifier.weight(1f),
                icon = R.drawable.icon_comment,
                defaultText = "评论",
                count = commentCount,
                onClick = onComment
            )
            ShareCommentFavorite(
                modifier = Modifier.weight(1f),
                icon = R.drawable.icon_follow,
                defaultText = "喜爱",
                count = favoriteCount,
                tine = if (isFollow) ComposeMusicTheme.colors.selectIcon else ComposeMusicTheme.colors.white,
                onClick = {} // 待完成
            )

        }
    }
}

// 分享/评论/喜爱 按钮
@Composable
private fun ShareCommentFavorite(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    defaultText: String,
    count: Long,
    tine: Color = ComposeMusicTheme.colors.white,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ComposeMusicTheme.colors.white.copy(alpha = 0.5f))
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            .clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "分享/评论/喜爱 按钮",
            tint = tine,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = if (count == 0L) defaultText else transformNum(count),
            style = MaterialTheme.typography.subtitle2.copy(fontFamily = FontFamily.Default),
            color = ComposeMusicTheme.colors.white,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// 歌单歌曲项
@Composable
private fun PlaylistSongsItem(
    bean: Track,
    onSongItem: (Long) -> Unit,
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .clickable { onSongItem(bean.id) }
    ) {
        val (pic, title, num, more) = createRefs()
        AsyncImage(
            model = bean.al.picUrl,
            contentDescription = bean.al.name,
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .constrainAs(pic) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
        )

        //歌曲名字
        Text(
            text = bean.name,
            style = MaterialTheme.typography.body2,
            color = ComposeMusicTheme.colors.textTitle,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(pic.top, 2.dp)
                start.linkTo(pic.end, 10.dp)
                end.linkTo(more.start)
                width = Dimension.fillToConstraints
            }
        )

        //歌手名称
        Text(
            text = bean.ar[0].name,
            style = MaterialTheme.typography.caption,
            color = ComposeMusicTheme.colors.textContent,
            modifier = Modifier.constrainAs(num) {
                bottom.linkTo(pic.bottom, 2.dp)
                start.linkTo(pic.end, 10.dp)
            }
        )

        androidx.compose.material.Icon(
            painter = painterResource(id = R.drawable.icon_more),
            contentDescription = stringResource(id = R.string.more),
            tint = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier
                .size(24.dp)
                .constrainAs(more) {
                    top.linkTo(title.top)
                    bottom.linkTo(num.bottom)
                    end.linkTo(parent.end, 5.dp)
                }
        )
    }
}

// 专辑歌曲项
@Composable
fun AlbumSongsItem(
    order: Int,
    bean: Track,
    onSongItem: (Long) -> Unit,
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .clickable { onSongItem(bean.id) }
    ) {
        val (orderRes, title, num, more) = createRefs()

        Text(
            text = "$order",
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
            color = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier.constrainAs(orderRes) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }
        )

        //歌曲名字
        Text(
            text = bean.name,
            style = MaterialTheme.typography.body2,
            color = ComposeMusicTheme.colors.textTitle,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(orderRes.end, 10.dp)
                end.linkTo(more.start)
                width = Dimension.fillToConstraints
            }
        )

        //歌手名称
        Text(
            text = bean.ar[0].name,
            style = MaterialTheme.typography.caption,
            color = ComposeMusicTheme.colors.textContent,
            modifier = Modifier.constrainAs(num) {
                bottom.linkTo(parent.bottom)
                start.linkTo(orderRes.end, 10.dp)
            }
        )

        androidx.compose.material.Icon(
            painter = painterResource(id = R.drawable.icon_more),
            contentDescription = stringResource(id = R.string.more),
            tint = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier
                .size(24.dp)
                .constrainAs(more) {
                    top.linkTo(title.top)
                    bottom.linkTo(num.bottom)
                    end.linkTo(parent.end, 5.dp)
                }
        )
    }
}

/********************************保存图片对话框,待删除****************************************/
@Composable
private fun PlaylistDialog(
    cover: String,
    name: String,
    tags: List<String>,
    company: String,
    subType: String,
    description: String,
    isPlaylist: Boolean,
    isShowDialog: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    if (isShowDialog) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(
                //是否可以通过按下后退按钮来关闭对话框。 如果为 true，按下后退按钮将调用 onDismissRequest。
                dismissOnBackPress = true,
                //是否可以通过在对话框边界外单击来关闭对话框。 如果为 true，单击对话框外将调用 onDismissRequest
                dismissOnClickOutside = true,
                //用于在对话框窗口上设置 WindowManager.LayoutParams.FLAG_SECURE 的策略。
                securePolicy = SecureFlagPolicy.Inherit,
                usePlatformDefaultWidth = false //自定义宽度
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = cover,
                    contentDescription = "背景",
                    placeholder = painterResource(id = R.drawable.composemusic_logo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur((LocalConfiguration.current.screenWidthDp / 2).dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .verticalScroll(state = rememberScrollState()),
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "关闭对话框",
                        tint = ComposeMusicTheme.colors.white,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.End)
                            .clickable { onDismiss() }
                    )
                    AsyncImage(
                        model = cover,
                        contentDescription = "Cover",
                        placeholder = painterResource(id = R.drawable.composemusic_logo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size((LocalConfiguration.current.screenWidthDp / 2).dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(20.dp))
                    )
                    Text(
                        text = name,
                        color = ComposeMusicTheme.colors.white,
                        style = MaterialTheme.typography.subtitle1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = ComposeMusicTheme.colors.white.copy(alpha = 0.5f)
                    )
                    if (isPlaylist) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Tags:",
                                style = MaterialTheme.typography.caption,
                                color = ComposeMusicTheme.colors.white,
                            )
                            if (tags.isEmpty()) {
                                Text(
                                    text = "None",
                                    style = MaterialTheme.typography.caption,
                                    color = ComposeMusicTheme.colors.white,
                                )
                            } else {
                                tags.forEach { TagText(tag = it) }
                            }
                        }
                    } else {
                        Text(
                            text = "Company: $company",
                            style = MaterialTheme.typography.caption,
                            color = ComposeMusicTheme.colors.white,
                        )
                        Text(
                            text = "Type: $subType",
                            style = MaterialTheme.typography.caption,
                            color = ComposeMusicTheme.colors.white,
                        )
                    }
                    Text(
                        text = description,
                        style = MaterialTheme.typography.caption,
                        color = ComposeMusicTheme.colors.white,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, ComposeMusicTheme.colors.textContent),
                        onClick = { onSave() },
                        modifier = Modifier
                            .padding(5.dp)
                            .align(Alignment.CenterHorizontally),
                    ) {
                        Text(
                            text = "保存图片",
                            style = MaterialTheme.typography.subtitle2,
                            color = ComposeMusicTheme.colors.white,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TagText(tag: String) {
    Text(
        text = tag,
        style = MaterialTheme.typography.overline,
        color = ComposeMusicTheme.colors.white,
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(10.dp))
            .background(ComposeMusicTheme.colors.white.copy(alpha = 0.2f))
            .padding(5.dp)
    )
}