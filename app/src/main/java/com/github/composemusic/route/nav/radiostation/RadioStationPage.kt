package com.github.composemusic.route.nav.radiostation

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.github.composemusic.R
import com.github.composemusic.bean.radio.RecommendRadioBean
import com.github.composemusic.bean.radio.program.NewHotRadioBean
import com.github.composemusic.bean.radio.program.ProgramRankBean
import com.github.composemusic.route.nav.recommend.MusicTabRow
import com.github.composemusic.tool.Loading
import com.github.composemusic.tool.LoadingFailed
import com.github.composemusic.ui.theme.ComposeMusicTheme
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs
import androidx.compose.foundation.pager.HorizontalPager

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn( ExperimentalFoundationApi::class)
@Composable
fun RadioStationPage(viewModel: RadioStationViewModel = hiltViewModel(), onRadio:(Long)->Unit, onSongItem:(Long)->Unit){
    val titles = remember { ProgramRankingHeader.values() } // 标题
    val programStatus = viewModel.getProgramRanking().collectAsLazyPagingItems() // 节目榜
    val newRadioStatus = viewModel.getNewProgramRanking().collectAsLazyPagingItems() // 新晋电台榜
    val hotRadioStatus = viewModel.getHotProgramRanking().collectAsLazyPagingItems() // 热门电台榜

    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberScrollState()
    val nestedScrollConnection = remember {
        object :NestedScrollConnection{
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return if (available.y > 0)
                    Offset.Zero
                else
                    Offset(0f, -scrollState.dispatchRawDelta(-available.y))
            }
        }
    }

    LaunchedEffect(key1 = scaffoldState.snackbarHostState){
        viewModel.eventFlow.collectLatest {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(hostState = it) { data->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    ){
        Surface(
            color = ComposeMusicTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp, bottom = 80.dp)
                    .verticalScroll(state = scrollState)
            ) {
                titles.forEach { title->
                    Text(text = stringResource(id = title.header), style = MaterialTheme.typography.body1, color = ComposeMusicTheme.colors.textTitle)
                    when(title.header){
                        ProgramRankingHeader.Recommend.header->{
                            RadioStationList(viewModel.recommends,onRadio)
                        }
                        ProgramRankingHeader.Hot.header->{
                            RadioStationList(viewModel.hots,onRadio)
                        }
                        ProgramRankingHeader.TopList.header->{
                            ProgramRankPager(
                                programStatus = programStatus,
                                newRadioStatus = newRadioStatus,
                                hotRadioStatus = hotRadioStatus,
                                nestedScrollConnection = nestedScrollConnection,
                                onRadio = onRadio,
                                onItemRadio = {
                                    viewModel.playProgram(it)
                                    onSongItem(it.program.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**电台个性推荐 / 热门电台*/
@Composable
private fun RadioStationList(recommends: List<RecommendRadioBean>, onRadio:(Long)->Unit){
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp), // 自动添加间隔
        verticalAlignment = Alignment.CenterVertically
    ){
        items(recommends) { recommend ->
            RadioStationListItem(bean = recommend, onRadio = onRadio)
        }
    }
}

/**电台子项item*/
@Composable
private fun RadioStationListItem(bean: RecommendRadioBean, onRadio: (Long) -> Unit){
    Column (
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(100.dp)
            .clickable { onRadio(bean.id) }
    ){
        // 图片
        AsyncImage(
            model = bean.picUrl,
            contentDescription = bean.name,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            modifier = Modifier
                .size(100.dp) // 设置宽高相等
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(5.dp))
        // 文本
        Text(
            text = "${bean.dj.nickname} | ${bean.name}",
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProgramRankPager(
    programStatus: LazyPagingItems<ProgramRankBean>, // 节目榜
    newRadioStatus: LazyPagingItems<NewHotRadioBean>, // 最新节目
    hotRadioStatus: LazyPagingItems<NewHotRadioBean>, // 热门节目
    nestedScrollConnection:NestedScrollConnection, // // 嵌套滚动的连接器
    tabs:List<String> = remember { ProgramRankingTab.values().map { it.tab } },
    pageState:androidx.compose.foundation.pager.PagerState = androidx.compose.foundation.pager.rememberPagerState{tabs.size},
    onRadio:(Long)->Unit,
    onItemRadio: (ProgramRankBean) -> Unit
){
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
            .background(ComposeMusicTheme.colors.background)
            .nestedScroll(nestedScrollConnection)
    ) {
        MusicTabRow(pagerState = pageState, tabs = tabs)
        HorizontalPager(state = pageState) {
            when(tabs[it]){
                ProgramRankingTab.Program.tab->{
                    ProgramRankList(programStatus, onItemRadio = onItemRadio)
                }
                ProgramRankingTab.NewRadio.tab->{
                    NewHotRankList(newRadioStatus,onRadio)
                }
                ProgramRankingTab.PopularRadio.tab->{
                    NewHotRankList(hotRadioStatus,onRadio)
                }
            }
        }
    }
}

@Composable
private fun ProgramRankList(programStatus: LazyPagingItems<ProgramRankBean>, onItemRadio:(ProgramRankBean)->Unit){
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
            .background(ComposeMusicTheme.colors.background),
        verticalArrangement = Arrangement.Center
    ){
        when(programStatus.loadState.refresh){
            is LoadState.Loading->{ item { Loading() } }
            is LoadState.Error->{ item { LoadingFailed {programStatus.retry()} } }
            else->{}
        }
        items(items = programStatus){ item->
            item?.let { bean->
                ProgramRankListItem(
                    curRank = bean.rank,
                    lastRank = bean.lastRank,
                    cover = bean.program.blurCoverUrl,
                    name = bean.program.name,
                    author = bean.program.dj.nickname,
                    onRadio = {onItemRadio(bean)}
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        when(programStatus.loadState.append){
            is LoadState.Loading-> {
                item { Loading() }
            }
            is LoadState.Error-> {
                item { LoadingFailed {programStatus.retry()} }
            }
            else ->{}
        }
    }
}

@Composable
private fun NewHotRankList(status: LazyPagingItems<NewHotRadioBean>, onRadio:(Long)->Unit){
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
            .background(ComposeMusicTheme.colors.background),
        verticalArrangement = Arrangement.Center
    ){
        when(status.loadState.refresh){
            is LoadState.Loading->{ item { Loading() } }
            is LoadState.Error->{ item { LoadingFailed {} } }
            else->{}
        }
        items(items = status){ item->
            item?.let { bean->
                ProgramRankListItem(
                    curRank = bean.rank,
                    lastRank = bean.lastRank,
                    cover = bean.picUrl,
                    name = bean.name,
                    author = bean.creatorName,
                    onRadio = { onRadio(bean.id) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        when(status.loadState.append){
            is LoadState.Loading-> { item { Loading() } }
            is LoadState.Error-> { item { LoadingFailed {} } }
            else->{}
        }
    }
}

/**
 * 节目榜item
 *
 * 当前排名, 上次排名, 封面, 节目名, 作者名, 点击事件
 */
@Composable
private fun ProgramRankListItem(curRank:Int, lastRank:Int, cover:String, name:String, author:String, onRadio:()->Unit){
    val color:Color
    val icon: Int
     if(curRank == lastRank){
        //排名不变
         color = ComposeMusicTheme.colors.stableRank
         icon = R.drawable.icon_rank_stable
    }else if (curRank-lastRank > 0){
        //排名下降
         color = ComposeMusicTheme.colors.downRank
         icon = R.drawable.icon_rank_down
    }else{
       //排名上升
         color = ComposeMusicTheme.colors.upRank
         icon = R.drawable.icon_rank_up
    }
        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onRadio() }
        ) {
            val (cur,last,pic,title,num,more) = createRefs()
            Text(
                text = "$curRank",
                color = ComposeMusicTheme.colors.highlightColor,
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.constrainAs(cur){
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(last.start)
                    end.linkTo(last.end)
                }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.constrainAs(last){
                    top.linkTo(cur.bottom)
                    start.linkTo(parent.start)
                }
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "rank",
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )

                Text(
                    text = "${abs(curRank-lastRank)}",
                    style = MaterialTheme.typography.overline.copy(fontFamily = FontFamily.Default),
                    color = color
                )
            }

            AsyncImage(
                model = cover,
                contentDescription = name,
                placeholder = painterResource(id = R.drawable.composemusic_logo),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .constrainAs(pic) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(last.end, 10.dp)
                    }
            )
            Text(
                text = name,
                style = MaterialTheme.typography.body2,
                color = ComposeMusicTheme.colors.textTitle,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.constrainAs(title){
                    top.linkTo(pic.top,2.dp)
                    start.linkTo(pic.end,10.dp)
                    end.linkTo(more.start)
                    width = Dimension.fillToConstraints
                }
            )
            Text(
                text = author,
                style = MaterialTheme.typography.caption,
                color = ComposeMusicTheme.colors.textContent,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(num){
                    bottom.linkTo(pic.bottom,2.dp)
                    start.linkTo(pic.end,10.dp)
                    end.linkTo(more.start)
                    width = Dimension.fillToConstraints
                }
            )
            Icon(
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


private enum class ProgramRankingHeader(@StringRes val header:Int){
    Recommend(R.string.recommend),
    Hot(R.string.hot),
    TopList(R.string.rank),
}
private enum class ProgramRankingTab(val tab:String){
    Program("节目榜"),
    NewRadio("最新节目"),
    PopularRadio("最热节目"),
}