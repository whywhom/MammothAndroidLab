package com.mammoth.androidlab.data

import kotlinx.serialization.Serializable

@Serializable
data class RadioCountry(
    val name: String,
    val iso_3166_1: String,
    val country: String
)

@Serializable
data class RadioStation(
    val name: String,
    val url: String,
    val stationcount: String?
)