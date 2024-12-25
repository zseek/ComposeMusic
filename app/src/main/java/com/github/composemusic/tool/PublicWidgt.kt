package com.github.composemusic.tool

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TabPosition
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.github.composemusic.R
import com.github.composemusic.bean.banner.BannerBean
import com.github.composemusic.bean.comment.CommentBean
import com.github.composemusic.route.drawer.user.transformData
import com.github.composemusic.route.playlist.NetworkStatus
import com.github.composemusic.ui.theme.ComposeMusicTheme
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.max

@Composable
fun TopTitleBar(
    modifier: Modifier = Modifier,
    titleBarBg: Color = Color.Transparent, //标题栏背景颜色
    iconTint: Color = ComposeMusicTheme.colors.textTitle,
    title: String, //标题栏中间的标题
    onBack: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = titleBarBg),
    ) {
        val (backRes, titleRes) = createRefs()

        IconButton(
            onClick = { onBack() },
            modifier = Modifier.constrainAs(backRes) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            color = iconTint,
            modifier = Modifier
                .constrainAs(titleRes) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
    }
}

@Composable
fun ComposeMusicBanner(
    modifier: Modifier = Modifier,
    items: List<BannerBean>,
    autoLoop: Boolean = true,
    bannerHeight: Dp = (LocalConfiguration.current.screenHeightDp.dp / 6),
    loopTimes: Long = 3000L,
    selectSize: Dp = 8.dp,
    unSelectSize: Dp = 4.dp,
    inactiveColor: Color = ComposeMusicTheme.colors.defaultIcon,//指示器未选中颜色
    activeColor: Color = ComposeMusicTheme.colors.highlightColor,//指示器选中颜色
    onItemClick: (BannerBean) -> Unit
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    var isAutoLoop by remember { mutableStateOf(autoLoop) }

    //当发生手动滑动事件时，取消自动播放
    LaunchedEffect(pagerState.interactionSource) {
        pagerState.interactionSource.interactions.collect {
            isAutoLoop = when (it) {
                is DragInteraction.Start -> false
                else -> true
            }
        }
    }

    //自动播放
    if (items.isNotEmpty()) {
        LaunchedEffect(pagerState.currentPage, isAutoLoop) {
            if (isAutoLoop) {
                delay(loopTimes)
                val next = (pagerState.currentPage + 1) % items.size
                scope.launch { pagerState.animateScrollToPage(next) }
            }
        }
    }

    HorizontalPager(
        count = items.size,
        state = pagerState,
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(bannerHeight)
        ) {
            repeat(items.size) {
                AsyncImage(
                    model = items[pagerState.currentPage].pic,
                    contentDescription = items[pagerState.currentPage].typeTitle,
                    placeholder = painterResource(id = R.drawable.composemusic_logo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onItemClick(items[pagerState.currentPage])
                        }
                )
            }
            HorizontalPagerIndicator(
                pageCount = items.size,
                pagerState = pagerState,
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                indicatorWidth = if (pagerState.currentPage == it) selectSize else unSelectSize,
                indicatorHeight = if (pagerState.currentPage == it) selectSize else unSelectSize,
                indicatorShape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(5.dp)
            )
        }
    }
}

/**重写TabRow的pagerTabIndicatorOffset, 让TabRow 的indicator宽度可以进行自定义*/
@SuppressLint("UnnecessaryComposedModifier")
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.customIndicatorOffset(
    pagerState: androidx.compose.foundation.pager.PagerState,
    tabPositions: List<TabPosition>,
    width: Dp
): Modifier = composed {
    if (pagerState.pageCount == 0) return@composed this

    val targetIndicatorOffset: Dp
    val indicatorWidth: Dp

    val currentTab = tabPositions[minOf(tabPositions.lastIndex, pagerState.currentPage)]
    val targetPage = pagerState.targetPage
    val targetTab = tabPositions.getOrNull(targetPage)

    if (targetTab != null) {
        val targetDistance = (targetPage - pagerState.currentPage).absoluteValue
        val fraction = (pagerState.currentPageOffsetFraction / max(targetDistance, 1)).absoluteValue

        targetIndicatorOffset = lerp(currentTab.left, targetTab.left, fraction)
        indicatorWidth = lerp(currentTab.width, targetTab.width, fraction).value.absoluteValue.dp
    } else {
        targetIndicatorOffset = currentTab.left
        indicatorWidth = currentTab.width
    }

    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .padding(horizontal = (indicatorWidth - width) / 2)
        .offset(x = targetIndicatorOffset)
        .width(width)
}

/**圆圈加载指示器*/
@Composable
fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp),
            color = ComposeMusicTheme.colors.highlightColor
        )
    }
}

/**加载失败*/
@Composable
fun LoadingFailed(
    maxHeight: Int = LocalConfiguration.current.screenHeightDp,
    content: String = "加载失败,请重试!",
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_loading_failed),
                contentDescription = "加载失败",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size((maxHeight / 5).dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.caption,
                color = ComposeMusicTheme.colors.textContent,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**搜索框*/
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    value: String,
    hint: String,
    focusRequester: FocusRequester = FocusRequester(),
    focusManager: FocusManager = LocalFocusManager.current,
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit = {}
) {
    Surface(
        border = BorderStroke(width = 1.dp, color = ComposeMusicTheme.colors.searchBar),
        shape = RoundedCornerShape(10.dp),
        color = ComposeMusicTheme.colors.background,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        CustomTextField(
            value = value,
            valueColor = ComposeMusicTheme.colors.textTitle,
            hintColor = ComposeMusicTheme.colors.textContent,
            onValueChange = onValueChange,
            placeholderText = hint,
            textStyle = MaterialTheme.typography.caption,
            maxLines = 1,
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
                onSearch()
            }),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "back",
                    tint = ComposeMusicTheme.colors.textTitle,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = ComposeMusicTheme.colors.unselectIcon,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            focusManager.clearFocus()
                            onSearch()
                        }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (!it.isFocused) {
                        keyboardController?.hide()
                    }
                }
        )
    }
}

/**文本输入框*/
@Composable
fun CustomTextField(
    value: String,
    valueColor: Color,
    hintColor: Color,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    placeholderText: String = "",
    onTextLayout: (TextLayoutResult) -> Unit = {},
    cursorBrush: Brush = SolidColor(Color.Black),
) {
    BasicTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        maxLines = maxLines,
        enabled = enabled,
        readOnly = readOnly,
        interactionSource = interactionSource,
        textStyle = textStyle.copy(color = valueColor),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        onTextLayout = onTextLayout,
        cursorBrush = cursorBrush,
        decorationBox = { innerTextField ->
            Row(
                modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty())
                        Text(
                            text = placeholderText,
                            style = textStyle.copy(color = hintColor),
                            maxLines = maxLines,
                            overflow = TextOverflow.Ellipsis
                        )
                    innerTextField()
                }
                if (trailingIcon != null) trailingIcon()
            }
        }
    )
}

/**自定义对话框*/
@Composable
fun CustomAlertDialog(
    visibility: Boolean,
    title: String,
    content: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    if (visibility) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = title,
                    color = ComposeMusicTheme.colors.highlightColor,
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = content,
                    color = ComposeMusicTheme.colors.textTitle,
                    style = MaterialTheme.typography.caption
                )
            },
            shape = RoundedCornerShape(10.dp),
            backgroundColor = ComposeMusicTheme.colors.background,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(
                        text = confirmText,
                        color = ComposeMusicTheme.colors.highlightColor,
                        style = MaterialTheme.typography.button
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onCancel) {
                    Text(
                        text = cancelText,
                        color = ComposeMusicTheme.colors.textContent,
                        style = MaterialTheme.typography.button
                    )
                }
            },
            properties = DialogProperties(
                //是否可以通过按下后退按钮来关闭对话框。 如果为 true，按下后退按钮将调用 onDismissRequest。
                dismissOnBackPress = true,
                //是否可以通过在对话框边界外单击来关闭对话框。 如果为 true，单击对话框外将调用 onDismissRequest
                dismissOnClickOutside = true,
                //用于在对话框窗口上设置 WindowManager.LayoutParams.FLAG_SECURE 的策略。
                securePolicy = SecureFlagPolicy.Inherit,
                //对话框内容的宽度是否应限制为平台默认值，小于屏幕宽度。
                usePlatformDefaultWidth = true
            )
        )
    }
}

/**
 * 评论BottomSheet
 * @param state 控制评论列表滚动状态的LazyListState对象
 * @param text 当前用户输入的评论文本
 * @param status 网络状态: 等待、失败和成功
 * @param commentCount 评论总数，用于在标题中显示总评论数
 * @param cover 播放列表或歌曲的封面图片URL
 * @param name 播放列表或歌曲的名称
 * @param artist 创作者的名称
 * @param comments 评论列表数据，包含每条评论的信息
 * @param coverHeight 封面图片的高度，默认值为64.dp
 * @param picHeight 评论者头像的高度，默认值为32.dp
 * @param onAgreeComment 回调函数，当用户点击“赞同”按钮时触发，参数为评论的ID和索引
 * @param onFloorComment 回调函数，当用户点击“回复”按钮时触发，参数为评论的ID和索引
 * @param onValueChange 回调函数，当用户输入框的内容发生变化时触发，参数为新的输入内容
 * @param onSend 回调函数，当用户点击发送按钮时触发
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CommentBottomSheet(
    state: LazyListState,
    text: String,
    status: NetworkStatus,
    commentCount: Long,
    cover: String,
    name: String,
    artist: String,
    comments: List<CommentBean>,
    coverHeight: Dp = 64.dp,
    picHeight: Dp = 32.dp,
    onAgreeComment: (Long, Int) -> Unit,
    onFloorComment: (Long, Int) -> Unit,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current // 用于控制键盘的显示/隐藏
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current // 用于管理焦点状态
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((LocalConfiguration.current.screenHeightDp * 0.9f).dp) // 高度为屏幕高度的90%
            .pointerInput(Unit) { // 用于处理指针事件(触摸,鼠标点击), 传入指针输入处理函数
                detectTapGestures( // 指针输入处理函数detectTapGestures
                    onTap = { focusManager.clearFocus() } // 单击时清除焦点
                )
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeMusicTheme.colors.background)
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
        ) {
            Divider( // 仿网易云装饰线
                color = ComposeMusicTheme.colors.textContent,
                thickness = DividerDefaults.Thickness,
                modifier = Modifier
                    .width(40.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
            Text(
                text = "评论($commentCount)",
                style = MaterialTheme.typography.subtitle1.copy(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold
                ),
                color = ComposeMusicTheme.colors.textTitle,
                textAlign = TextAlign.Center
            )
            LazyColumn(
                state = state,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = coverHeight + picHeight)
            ) {
                item {
                    PlaylistBriefInfo(
                        cover = cover,
                        name = name,
                        artist = artist,
                        coverHeight = coverHeight
                    )
                }
                item {
                    Text(
                        text = "所有评论",
                        style = MaterialTheme.typography.subtitle2.copy(
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ComposeMusicTheme.colors.textTitle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                when (status) {
                    is NetworkStatus.Waiting -> {
                        item { Loading() }
                    }

                    is NetworkStatus.Failed -> {
                        item { LoadingFailed(content = status.error) {} }
                    }

                    is NetworkStatus.Successful -> {
                        //加载成功，但是没有评论
                        if (comments.isEmpty()) {
                            item { LoadingFailed(content = "没有评论!") {} }
                        }
                    }
                }

                //评论
                itemsIndexed(comments) { index, item ->
                    CommentItem(
                        picHeight = picHeight,
                        comment = item,
                        onAgreeComment = {
                            onAgreeComment(it, index)
                        },
                        onFloorComment = {
                            onFloorComment(it, index)
                        }
                    )
                    if (index < comments.size - 1) {
                        Divider(
                            color = ComposeMusicTheme.colors.textContent.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = picHeight, top = 10.dp)
                        )
                    }
                }
            }
        }
        // 底部评论输入框
        CommentBar(
            text = text,
            onValueChange = onValueChange,
            onSend = {
                onSend()
                focusManager.clearFocus()
            },
            modifier = Modifier.align(Alignment.BottomCenter),
            keyboardActions = KeyboardActions(onSend = {
                onSend()
                focusManager.clearFocus()
            }),
            textModifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (!it.isFocused) {
                        keyboardController?.hide()
                    }
                }
        )
    }
}


/**
 * 楼层评论BottomSheet
 * @param state 控制评论列表滚动状态的LazyListState对象
 * @param text 当前用户输入的评论文本
 * @param status 网络状态: 等待、失败和成功
 * @param commentCount 评论总数，用于在标题中显示总评论数
 * @param coverHeight 封面图片的高度，默认值为64.dp
 * @param picHeight 评论者头像的高度，默认值为32.dp
 * @param ownComment 当前用户的评论信息
 * @param comments 评论列表数据，包含每条评论的信息
 * @param onAgreeComment 回调函数，当用户点击“赞同”按钮时触发，参数为评论的ID和索引
 * @param onValueChange 回调函数，当用户输入框的内容发生变化时触发，参数为新的输入内容
 * @param onSend 回调函数，当用户点击发送按钮时触发
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FloorCommentBottomSheet(
    state: LazyListState,
    text: String,
    status: NetworkStatus,
    commentCount: Long,
    coverHeight: Dp = 64.dp,
    picHeight: Dp = 32.dp,
    ownComment: CommentBean?,
    comments: List<CommentBean>,
    onAgreeComment: (Long, Int) -> Unit,
    onValueChange: (String) -> Unit,
    onSend: (Long) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((LocalConfiguration.current.screenHeightDp * 0.9f).dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeMusicTheme.colors.background)
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
        ) {
            Divider(
                color = ComposeMusicTheme.colors.textContent,
                thickness = DividerDefaults.Thickness,
                modifier = Modifier
                    .width(40.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
            Text(
                text = "回复($commentCount)",
                style = MaterialTheme.typography.subtitle1.copy(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold
                ),
                color = ComposeMusicTheme.colors.textTitle,
                textAlign = TextAlign.Center
            )
            LazyColumn(
                state = state,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = coverHeight + picHeight)
            ) {
                item {
                    if (ownComment != null) {
                        OwnCommentItem(
                            comment = ownComment,
                            picHeight = picHeight
                        )
                        Divider(
                            color = ComposeMusicTheme.colors.textContent.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .padding(top = 10.dp)
                        )
                    }
                }
                item {
                    Text(
                        text = "所有回复",
                        style = MaterialTheme.typography.subtitle2.copy(
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ComposeMusicTheme.colors.textTitle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                when (status) {
                    is NetworkStatus.Waiting -> {
                        item { Loading() }
                    }

                    is NetworkStatus.Failed -> {
                        item { LoadingFailed(content = status.error) {} }
                    }

                    is NetworkStatus.Successful -> {
                        //加载成功，但是没有评论
                        if (comments.isEmpty()) {
                            item { LoadingFailed(content = "没有回复!") {} }
                        }
                    }
                }

                //评论
                itemsIndexed(comments) { index, item ->
                    CommentItem(
                        showExpend = false,
                        picHeight = picHeight,
                        comment = item,
                        onAgreeComment = {
                            onAgreeComment(it, index)
                        },
                        onFloorComment = {}
                    )
                    if (index < comments.size - 1) {
                        Divider(
                            color = ComposeMusicTheme.colors.textContent.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = picHeight, top = 10.dp)
                        )
                    }
                }
            }
        }
        CommentBar(
            text = text,
            onValueChange = onValueChange,
            onSend = {
                onSend(ownComment?.commentId ?: 0L)
                focusManager.clearFocus()
            },
            modifier = Modifier.align(Alignment.BottomCenter),
            keyboardActions = KeyboardActions(onSend = {
                onSend(ownComment?.commentId ?: 0L)
                focusManager.clearFocus()
            }),
            textModifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (!it.isFocused) {
                        keyboardController?.hide()
                    }
                }
        )
    }
}

/**歌单评论BottomSheet中显示的歌单简略信息*/
@Composable
private fun PlaylistBriefInfo(
    cover: String,
    name: String,
    artist: String,
    coverHeight: Dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(coverHeight)
    ) {
        AsyncImage(
            model = cover,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            modifier = Modifier
                .size(coverHeight)
                .clip(RoundedCornerShape(20.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .height(coverHeight)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.body1,
                color = ComposeMusicTheme.colors.textTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = artist,
                style = MaterialTheme.typography.caption,
                color = ComposeMusicTheme.colors.textContent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "更多",
            tint = ComposeMusicTheme.colors.defaultIcon,
            modifier = Modifier.size(32.dp)
        )
    }
}

/**单条评论item*/
@Composable
private fun CommentItem(
    showExpend: Boolean = true,
    picHeight: Dp,
    comment: CommentBean,
    onFloorComment: (Long) -> Unit,
    onAgreeComment: (Long) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (picRes, nameRes, dateRes, agreeRes, commentRes, floorRes) = createRefs()
        //评论者图像
        AsyncImage(
            model = comment.user.avatarUrl,
            contentDescription = comment.user.nickname,
            contentScale = ContentScale.Fit,
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            modifier = Modifier
                .size(picHeight)
                .clip(CircleShape)
                .constrainAs(picRes) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
        )
        //评论者名称
        Text(
            text = comment.user.nickname,
            style = MaterialTheme.typography.caption,
            color = ComposeMusicTheme.colors.textTitle.copy(alpha = 0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier.constrainAs(nameRes) {
                top.linkTo(picRes.top)
                start.linkTo(picRes.end, 5.dp)
                end.linkTo(agreeRes.start)
                width = Dimension.fillToConstraints
            }
        )
        //评论日期
        Text(
            text = comment.timeStr ?: transformData(comment.time, "yyyy-MM-dd"),
            style = MaterialTheme.typography.overline.copy(fontFamily = FontFamily.Default),
            color = ComposeMusicTheme.colors.textContent,
            modifier = Modifier.constrainAs(dateRes) {
                bottom.linkTo(picRes.bottom)
                start.linkTo(picRes.end, 5.dp)
            }
        )

        //点赞icon及点赞人数
        IconButton(
            onClick = { onAgreeComment(comment.commentId) },
            modifier = Modifier.constrainAs(agreeRes) {
                top.linkTo(nameRes.top)
                bottom.linkTo(dateRes.bottom)
                end.linkTo(parent.end)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (comment.likedCount == 0) "" else "${comment.likedCount}",
                    style = MaterialTheme.typography.caption,
                    color = if (comment.liked) ComposeMusicTheme.colors.selectIcon else ComposeMusicTheme.colors.unselectIcon,
                )
                Icon(
                    painter = painterResource(id = R.drawable.icon_agree_comment),
                    contentDescription = "点赞",
                    tint = if (comment.liked) ComposeMusicTheme.colors.selectIcon else ComposeMusicTheme.colors.unselectIcon,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        //评论内容
        Text(
            text = comment.content,
            style = MaterialTheme.typography.caption,
            color = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier.constrainAs(commentRes) {
                top.linkTo(picRes.bottom, 5.dp)
                start.linkTo(picRes.end, 5.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        AnimatedVisibility(
            visible = showExpend,
            modifier = Modifier
                .constrainAs(floorRes) {
                    top.linkTo(commentRes.bottom, 5.dp)
                    start.linkTo(picRes.end, 5.dp)
                }
        ) {
            //楼层回复
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onFloorComment(comment.commentId) }
            ) {
                Text(
                    text = "更多回复",
                    style = MaterialTheme.typography.caption,
                    color = ComposeMusicTheme.colors.selectIcon,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Expend Reply",
                    tint = ComposeMusicTheme.colors.selectIcon,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }
}

/**我的评论*/
@Composable
private fun OwnCommentItem(picHeight: Dp, comment: CommentBean) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (picRes, nameRes, dateRes, commentRes) = createRefs()
        //评论者图像
        AsyncImage(
            model = comment.user.avatarUrl,
            contentDescription = comment.user.nickname,
            contentScale = ContentScale.Fit,
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            modifier = Modifier
                .size(picHeight)
                .clip(CircleShape)
                .constrainAs(picRes) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
        )
        //评论者名称
        Text(
            text = comment.user.nickname,
            style = MaterialTheme.typography.caption,
            color = ComposeMusicTheme.colors.textTitle.copy(alpha = 0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier.constrainAs(nameRes) {
                top.linkTo(picRes.top)
                start.linkTo(picRes.end, 5.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        //评论日期
        Text(
            text = comment.timeStr ?: transformData(comment.time, "yyyy-MM-dd"),
            style = MaterialTheme.typography.overline.copy(fontFamily = FontFamily.Default),
            color = ComposeMusicTheme.colors.textContent,
            modifier = Modifier.constrainAs(dateRes) {
                bottom.linkTo(picRes.bottom)
                start.linkTo(picRes.end, 5.dp)
            }
        )

        //评论内容
        Text(
            text = comment.content,
            style = MaterialTheme.typography.caption,
            color = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier.constrainAs(commentRes) {
                top.linkTo(picRes.bottom, 5.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
    }
}

/**评论输入框*/
@Composable
private fun CommentBar(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    text: String,
    keyboardActions: KeyboardActions,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 20.dp, end = 20.dp)
            .navigationBarsPadding()
            .background(ComposeMusicTheme.colors.background)
            .padding(top = 2.dp, bottom = 2.dp)
            .imePadding()
    ) {
        CustomTextField(
            value = text,
            valueColor = ComposeMusicTheme.colors.textTitle,
            hintColor = ComposeMusicTheme.colors.textContent,
            placeholderText = "请输入...",
            maxLines = 1,
            textStyle = MaterialTheme.typography.caption,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Send
            ),
            keyboardActions = keyboardActions,
            modifier = textModifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(ComposeMusicTheme.colors.grayBackground)
                .padding(5.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextButton(onClick = onSend) {
            Text(
                text = "发送",
                style = MaterialTheme.typography.caption,
                color = ComposeMusicTheme.colors.highlightColor
            )
        }
    }
}