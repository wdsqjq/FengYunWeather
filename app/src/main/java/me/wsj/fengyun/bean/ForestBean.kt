package me.wsj.fengyun.bean

data class ForestBean(
    val code: String,
    val daily: List<Daily>
)

data class Daily(
    var cloud: String = "",
    var fxDate: String = "2021-01-01",
    var humidity: String = "",
    var iconDay: String = "",
    var iconNight: String = "",
    var moonPhase: String = "",
    var moonrise: String = "",
    var moonset: String = "",
    var precip: String = "",
    var pressure: String = "",
    var sunrise: String = "",
    var sunset: String = "",
    var tempMax: String = "",
    var tempMin: String = "",
    var textDay: String = "",
    var textNight: String = "",
    var uvIndex: String = "",
    var vis: String = "",
    var wind360Day: String = "",
    var wind360Night: String = "",
    var windDirDay: String = "",
    var windDirNight: String = "",
    var windScaleDay: String = "",
    var windScaleNight: String = "",
    var windSpeedDay: String = "",
    var windSpeedNight: String = ""
)