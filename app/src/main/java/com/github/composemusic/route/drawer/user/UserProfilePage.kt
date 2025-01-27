package com.github.composemusic.route.drawer.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.github.composemusic.R
import com.github.composemusic.tool.XMLParserUtil.getCityNameByCode
import com.github.composemusic.ui.theme.ComposeMusicTheme
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserProfilePage(
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val value = viewModel.uiStatus.value
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(scaffoldState.snackbarHostState) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is UserProfileStatus.NetworkFailed -> scaffoldState.snackbarHostState.showSnackbar(
                    it.msg
                )
            }
        }
    }
    if (value.profile != null) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeMusicTheme.colors.background)
                .navigationBarsPadding()
        ) {
            val (backRes, picRes, bgRes, nameRes, signatureRes, cardRes, infoRes) = createRefs()

            //背景图片
            AsyncImage(
                model = value.profile.profile.backgroundUrl,
                contentDescription = "背景",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.composemusic_logo),
                modifier = Modifier
                    .fillMaxWidth()
                    .height((LocalConfiguration.current.screenHeightDp / 3).dp)
                    .constrainAs(bgRes) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    }
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "返回",
                tint = ComposeMusicTheme.colors.background,
                modifier = Modifier
                    .statusBarsPadding()
                    .size(24.dp)
                    .clickable { onBack() }
                    .constrainAs(backRes) {
                        top.linkTo(parent.top, 10.dp)
                        start.linkTo(parent.start, 10.dp)
                    }
            )

            // 用户图像
            AsyncImage(
                model = value.profile.profile.avatarUrl,
                contentDescription = "用户图像",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.composemusic_logo),
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .constrainAs(picRes) {
                        start.linkTo(bgRes.start)
                        end.linkTo(bgRes.end)
                        bottom.linkTo(bgRes.bottom, (-50).dp)
                    }
            )
            // 用户名称
            Text(
                text = value.profile.profile.nickname,
                color = ComposeMusicTheme.colors.textTitle,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(nameRes) {
                    top.linkTo(picRes.bottom, 15.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
            // 用户签名
            Text(
                text = value.profile.profile.signature,
                color = ComposeMusicTheme.colors.textTitle,
                style = MaterialTheme.typography.subtitle2,
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(signatureRes) {
                    top.linkTo(nameRes.bottom, 5.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
            Card(
                backgroundColor = ComposeMusicTheme.colors.background,
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .constrainAs(cardRes) {
                        top.linkTo(signatureRes.bottom, 10.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 关注
                        Text(
                            text = "关注",
                            color = ComposeMusicTheme.colors.textTitle,
                            style = MaterialTheme.typography.subtitle2.copy(
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        // 粉丝
                        Text(
                            text = "粉丝",
                            color = ComposeMusicTheme.colors.textTitle,
                            style = MaterialTheme.typography.subtitle2.copy(
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        // 等级
                        Text(
                            text = "Level",
                            color = ComposeMusicTheme.colors.textTitle,
                            style = MaterialTheme.typography.subtitle2.copy(
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //用户关注的人数
                        Text(
                            text = "${value.profile.profile.follows}",
                            color = ComposeMusicTheme.colors.textTitle,
                            style = MaterialTheme.typography.subtitle2.copy(fontFamily = FontFamily.Default),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        //粉丝人数
                        Text(
                            text = "${value.profile.profile.followeds}",
                            color = ComposeMusicTheme.colors.textTitle,
                            style = MaterialTheme.typography.subtitle2.copy(fontFamily = FontFamily.Default),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "${value.profile.level}",
                            color = ComposeMusicTheme.colors.textTitle,
                            style = MaterialTheme.typography.subtitle2.copy(fontFamily = FontFamily.Default),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            // 基本信息
            Card(
                backgroundColor = ComposeMusicTheme.colors.background,
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .constrainAs(infoRes) {
                        top.linkTo(cardRes.bottom, 10.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "基本信息",
                        color = ComposeMusicTheme.colors.textTitle,
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "注册时间: ${transformData(value.profile.createTime)}",
                        color = ComposeMusicTheme.colors.textContent,
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "性别: ${if (value.profile.profile.gender == 1) "男" else "女"}",
                        color = ComposeMusicTheme.colors.textContent,
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "年龄: ${transformData(value.profile.profile.birthday)}",
                        color = ComposeMusicTheme.colors.textContent,
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "所在地: ${
                            getCityNameByCode(
                                provinceCode = value.profile.profile.province,
                                cityCode = value.profile.profile.city,
                                split = "-"
                            )
                        }",
                        color = ComposeMusicTheme.colors.textContent,
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

fun transformData(time: Long, pattern: String = "yyyy年MM月dd日"): String {
    val date = Date(time)
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(date)
}