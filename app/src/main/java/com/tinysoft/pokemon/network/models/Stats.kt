package com.tinysoft.pokemon.network.models

import com.google.gson.annotations.SerializedName

data class Stats(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: Stat
)
