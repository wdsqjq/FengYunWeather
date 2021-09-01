package me.wsj.fengyun.bean

data class WarningBean(
    val code: String,
    val warning: List<Warning>
)

data class Warning(
    val endTime: String,
    val id: String,
    val level: String,
    val pubTime: String,
    val startTime: String,
    val status: String,
    val text: String,
    val title: String,
    val type: String,
    val typeName: String
)