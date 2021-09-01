package me.wsj.fengyun.bean

data class SearchCity(
    val code: String,
    val location: List<Location>
)

data class TopCity(
    val code: String,
    val topCityList: ArrayList<Location>
)