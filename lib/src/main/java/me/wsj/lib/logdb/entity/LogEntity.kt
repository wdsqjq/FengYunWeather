package me.wsj.lib.logdb.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "log")
class LogEntity() {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Long = 0

    var content: String = ""

    var time: Long = 0

    @Ignore
    constructor(content: String) : this() {
        this.content = content
        this.time = System.currentTimeMillis()
    }
}