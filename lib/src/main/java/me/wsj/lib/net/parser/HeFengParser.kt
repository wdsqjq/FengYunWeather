package me.wsj.lib.net.parser

import com.google.gson.Gson
import org.json.JSONObject
import java.lang.reflect.Type

class HeFengParser<T>(val returnType: Type) : ResultParser<T> {
    override fun parse(json: String): T {
        return Gson().fromJson(json, returnType)
    }
}

class FengYunParser<T>(val returnType: Type) : ResultParser<T> {
    override fun parse(json: String): T {
        val jsonObject = JSONObject(json)
        val data = jsonObject.get("result").toString()
        return Gson().fromJson(data, returnType)
    }
}