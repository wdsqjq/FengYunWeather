package me.wsj.lib.net.parser

import com.google.gson.Gson
import org.json.JSONObject
import java.lang.reflect.Type

/**
 * 和风api解析器
 */
class HeFengParser<T>(val returnType: Type) : ResultParser<T> {
    override fun parse(json: String): T {
        return Gson().fromJson(json, returnType)
    }
}

/**
 * 风云api解析器
 */
class FengYunParser<T>(val returnType: Type) : ResultParser<T> {
    override fun parse(json: String): T {
        val jsonObject = JSONObject(json)
        val data = jsonObject.get("result").toString()
        return Gson().fromJson(data, returnType)
    }
}