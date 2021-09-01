package me.wsj.fengyun.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/*
    {
    "name": "上海",
    "id": "101020100",
    "lat": "31.23170",
    "lon": "121.47264",
    "adm2": "上海",
    "adm1": "上海市",
    "country": "中国",
    "tz": "Asia/Shanghai",
    "utcOffset": "+08:00",
    "isDst": "0",
    "type": "city",
    "rank": "11",
    "fxLink": "http://hfx.link/2bc1"
    }
 */
//@Parcelize
data class Location(
        val adm1: String,
        val adm2: String,
        val country: String,
        val fxLink: String,
        val id: String,
        val isDst: String,
        val lat: String,
        val lon: String,
        val name: String,
        val rank: String,
        val type: String,
        val tz: String,
        val utcOffset: String
) : Serializable