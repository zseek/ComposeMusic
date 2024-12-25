package com.github.composemusic.bean.radio

import com.github.composemusic.bean.radio.program.Program

data class BaseRadioProgram(
    val count:Int,
    val code:Int,
    val programs:List<Program>,
    val more:Boolean
)
