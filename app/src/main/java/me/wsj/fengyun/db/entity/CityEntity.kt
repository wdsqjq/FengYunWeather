package me.wsj.fengyun.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "city")
class CityEntity() {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    var cityId: String = ""

    var cityName: String = ""

    var isLocal: Boolean = false

    @Ignore
    constructor(id: String, name: String, local: Boolean = false) : this() {
        cityId = id
        cityName = name
        isLocal = local
    }
}