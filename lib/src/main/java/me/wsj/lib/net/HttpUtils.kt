package me.wsj.lib.net

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.wsj.lib.BuildConfig
import me.wsj.lib.net.exception.RequestException
import okhttp3.HttpUrl
import okhttp3.Request
import org.json.JSONObject
import per.wsj.commonlib.utils.LogUtil
import java.lang.reflect.Type

object HttpUtils {

    public var BASE_URL: String? = null

    private var cer: String? = null

    init {
//        BASE_URL = Constant.URL_OFFICIAL_SSL
//        cer = "gitstar.cer"
    }

    @JvmStatic
    fun getBaseUrl(): String? {
        return BASE_URL
    }

    suspend inline fun <reified T> get(
        url: String,
        param: HashMap<String, Any>? = null,
        headers: HashMap<String, String>? = null
    ): T {
        val type = object : TypeToken<T>() {}.type
        return execRequest(url, param, headers, type)
    }

    suspend fun <T> execRequest(
        url: String,
        param: HashMap<String, Any>? = null,
        headers: HashMap<String, String>? = null, returnType: Type
    ): T {
        val newUrl = if (url.startsWith("http")) url else BASE_URL + url

        val urlBuilder = HttpUrl.parse(newUrl)?.newBuilder()

        param?.let {
            it.keys.forEach { key ->
                urlBuilder?.addQueryParameter(key, it[key].toString())
            }
        }

        val request = Request.Builder().url(urlBuilder?.build())

        headers?.keys?.forEach {
            request.addHeader(it, headers[it])
        }
        try {
            OkHttpUtils.getClient("").newCall(request.build()).execute().use { response ->
                val body = response.body()?.string() ?: throw RequestException("数据为空")
                val jsonObject = JSONObject(body)
                val code = jsonObject.get("code").toString()
                when (code) {
                    "200" -> {
                        return Gson().fromJson(body, returnType)
                    }
                    "401" -> {
                        throw RequestException("请求异常异常：401 鉴权错误，请确认key是否正确")
                    }
                    else -> {
                        throw RequestException("请求异常异常：code=$code")
                    }
                }
            }
        } catch (e: Throwable) {
            if (BuildConfig.DEBUG) {
                LogUtil.e("wtkLib_http", "url: $request error: $e")
            }
            throw e
        }
    }
}