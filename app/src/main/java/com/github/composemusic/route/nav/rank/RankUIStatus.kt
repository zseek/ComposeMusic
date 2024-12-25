package com.github.composemusic.route.nav.rank

import com.github.composemusic.bean.rank.Rankbean

data class RankUIStatus(
    val official:MutableList<Rankbean> = mutableListOf(),
    val global:MutableList<Rankbean> = mutableListOf()
)
