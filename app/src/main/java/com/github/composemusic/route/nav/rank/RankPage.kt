package com.github.composemusic.route.nav.rank

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.github.composemusic.bean.rank.Rankbean
import com.github.composemusic.tool.Loading
import com.github.composemusic.ui.theme.ComposeMusicTheme

@Composable
fun RankPage(viewModel: RankViewModel = hiltViewModel(), onRank: (Long) -> Unit) {
    val value = viewModel.uiStatus.value

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(ComposeMusicTheme.colors.background)
            .padding(start = 12.dp, end = 12.dp, bottom = 72.dp)
    ){
        if (value.official.isEmpty() || value.global.isEmpty()){
            item(span = { GridItemSpan(this.maxLineSpan) }) { Loading() }
        }else{
            item(span = {GridItemSpan(this.maxLineSpan)}){
                OfficialRankLayout(charts = value.official, onRank = onRank)
                Spacer(modifier = Modifier.height(20.dp))
            }
            items(value.global) { rank ->
                RecommendRankItem(rank = rank, onRank = onRank)
            }
        }
    }
}

@Composable
fun OfficialRankLayout(charts: List<Rankbean>, onRank: (Long) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        charts.forEach { bean ->
            OfficialRankItem(bean = bean, onRank = onRank)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun OfficialRankItem(bean: Rankbean, onRank: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRank(bean.id) }
            .padding(8.dp)
    ) {
        // 左侧的图片
        AsyncImage(
            model = bean.coverImgUrl,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 右侧的排行榜内容
        Column(modifier = Modifier.fillMaxWidth()) {
            bean.tracks.forEachIndexed { index, song ->
                Text(
                    text = "${index + 1}. ${song.first} - ${song.second}",
                    color = ComposeMusicTheme.colors.textTitle,
                    style = MaterialTheme.typography.body2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun RecommendRankItem(rank: Rankbean, onRank: (Long) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onRank(rank.id) })
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // 固定高度
                .clip(RoundedCornerShape(8.dp)) // 圆角
        ) {
            AsyncImage(
                model = rank.coverImgUrl,
                contentDescription = rank.name,
                contentScale = ContentScale.Crop, // 图片裁剪填充
                modifier = Modifier.fillMaxSize()
            )
            // 左下角显示更新时间
            Text(
                text = rank.updateFrequency,
                color = Color.White,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .align(Alignment.BottomStart) // 左下角对齐
                    .padding(4.dp) // 左下角内边距
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp)) // 半透明黑色背景
                    .padding(horizontal = 6.dp, vertical = 2.dp) // 为文字本身添加一点内边距
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 榜单名称
        Text(
            text = rank.name,
            color = ComposeMusicTheme.colors.textTitle,
            style = MaterialTheme.typography.subtitle2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


