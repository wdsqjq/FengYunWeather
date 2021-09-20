package me.wsj.fengyun.bean

data class VersionBean(
    val describe: String,
    val isForce: Boolean,
    val urlFull: String,
    val versionCode: Int,
    val versionName: String
)