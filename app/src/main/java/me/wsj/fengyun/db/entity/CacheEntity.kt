package me.wsj.fengyun.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache")
class CacheEntity() {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    var key: String = ""

    //缓存数据的二进制
    var data: ByteArray? = null

    var dead_line: Long = 0
}