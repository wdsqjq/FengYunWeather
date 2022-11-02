package me.wsj.fengyun.bean

data class LifeIndicator(
    val code: String,
    val daily: List<LifeIndicatorDaily>
)

data class LifeIndicatorDaily(
    val category: String,
    val date: String,
    val level: String,
    val name: String,
    val text: String,
    val type: String
)