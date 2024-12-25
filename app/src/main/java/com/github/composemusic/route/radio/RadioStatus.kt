package com.github.composemusic.route.radio

import com.github.composemusic.bean.radio.ProgramDetailBean
import com.github.composemusic.bean.radio.program.Program

data class RadioUIStatus(
    val detail: ProgramDetailBean? = null,
    val programs: List<Program> = emptyList()
)

sealed class RadioStatus {
    data class NetworkFailed(val msg: String) : RadioStatus()
}