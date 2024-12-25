package com.github.composemusic.route.nav.rank

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.composemusic.bean.rank.Rankbean
import com.github.composemusic.network.MusicApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankViewModel @Inject constructor(private val service: MusicApiService) : ViewModel() {

    private val _uiStatus = mutableStateOf(RankUIStatus())
    val uiStatus: State<RankUIStatus> = _uiStatus

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val official = mutableListOf<Rankbean>() // 官方榜单
                val global = mutableListOf<Rankbean>() // 全球榜单
                val result = service.getTopListDetail()
                if (result.code == 200) {
                    val charts = result.list // 获取排行榜列表
                    charts.forEach { bean -> // 遍历排行榜数据
                        if (bean.tracks.isNotEmpty()) { // 如果有歌曲数据，归类为官方榜单
                            official.add(bean)
                        } else { // 否则，归类为全球榜单
                            global.add(bean)
                        }
                    }
                    // 更新 UI 状态
                    _uiStatus.value = uiStatus.value.copy(official = official, global = global)
                }
            } catch (e: Exception) {
                Log.e("RankViewModel Exception:", e.message.toString()) // 记录异常信息
            }
        }
    }
}
