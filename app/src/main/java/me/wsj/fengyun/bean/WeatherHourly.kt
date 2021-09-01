package me.wsj.fengyun.bean

data class WeatherHourly(
    val code: String,
    val hourly: List<Hourly>
)

data class Hourly(
    val cloud: String,
    val dew: String,
    val fxTime: String,
    val humidity: String,
    var icon: String,
    val pop: String,
    val precip: String,
    val pressure: String,
    val temp: String,
    val text: String,
    val wind360: String,
    val windDir: String,
    val windScale: String,
    val windSpeed: String
)