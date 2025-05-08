package me.wsj.lib.net

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.wsj.lib.BuildConfig
import me.wsj.lib.net.exception.ExceptionUtils
import me.wsj.lib.net.exception.RequestException
import me.wsj.lib.net.parser.FengYunParser
import me.wsj.lib.net.parser.HeFengParser
import me.wsj.lib.net.parser.ResultParser
import okhttp3.*
import org.json.JSONObject
import per.wsj.commonlib.utils.LogUtil
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

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
        headers: HashMap<String, String>? = null,
        useCache: Boolean = false,
        noinline error: ((String) -> Unit)? = null
    ): T? {
        val type = object : TypeToken<T>() {}.type
        return get(url, param, type, headers, useCache, error)
    }

    suspend fun <T> get(
        url: String,
        param: HashMap<String, Any>? = null,
        returnType: Type,
        headers: HashMap<String, String>? = null,
        useCache: Boolean = false,
        error: ((String) -> Unit)? = null
    ): T? {
        val newUrl = if (url.startsWith("http")) url else BASE_URL + url
        val urlBuilder = HttpUrl.parse(newUrl)!!.newBuilder()
        param?.let {
            it.keys.forEach { key ->
                urlBuilder.addQueryParameter(key, it[key].toString())
            }
        }
        return execRequest(
            "GET",
            urlBuilder.build(),
            headers,
            null,
            HeFengParser<T>(returnType),
            useCache,
            error
        )
    }

    suspend inline fun <reified T> post(
        url: String,
        param: HashMap<String, Any>? = null,
        headers: HashMap<String, String>? = null, noinline error: ((String) -> Unit)? = null
    ): T? {
        val type = object : TypeToken<T>() {}.type

        return post(url, param, type, headers, error)
    }

    suspend inline fun <reified T> post(
        url: String,
        param: Any? = null,
        headers: HashMap<String, String>? = null, noinline error: ((String) -> Unit)? = null
    ): T? {
        val type = object : TypeToken<T>() {}.type

        return post(url, param, type, headers, error)
    }

    suspend fun <T> post(
        url: String,
        param: Any? = null,
        returnType: Type,
        headers: HashMap<String, String>? = null, error: ((String) -> Unit)? = null
    ): T? {
        val newUrl = if (url.startsWith("http")) url else BASE_URL + url
        val urlBuilder = HttpUrl.parse(newUrl)!!.newBuilder()

        val requestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            Gson().toJson(param)
        )

        return execRequest(
            "POST",
            urlBuilder.build(),
            headers,
            requestBody,
            FengYunParser<T>(returnType),
            error = error
        )
    }

    suspend fun <T> execRequest(
        method: String,
        httpUrl: HttpUrl,
        headers: HashMap<String, String>? = null,
        requestBody: RequestBody?,
        parser: ResultParser<T>,
        useCache: Boolean = false,
        error: ((String) -> Unit)? = null
    ): T? {
        val request = Request.Builder().url(httpUrl).method(method, requestBody)
        headers?.keys?.forEach {
            request.addHeader(it, headers[it])
        }
        if (useCache) {
            request.cacheControl(
                CacheControl.Builder()
                    .maxAge(60, TimeUnit.SECONDS)
                    .maxStale(60, TimeUnit.SECONDS)
                    .build()
            )
        }
        try {
            OkHttpUtils.getClient().newCall(request.build()).execute().use { response ->
                if (!response.isSuccessful) {
                    throw RequestException("请求异常：${response.code()}")
                }
                val body = response.body()?.string() ?: throw RequestException("数据为空")

                val jsonObject = JSONObject(body)
                val code = jsonObject.get("code").toString()
                when (code) {
                    "200" -> {
                        return parser.parse(body)
                        /*return if (flag == 0) {
                            Gson().fromJson(body, returnType)
                        } else {
                            val data = jsonObject.get("result").toString()
                            Gson().fromJson(data, returnType)
                        }*/
                    }
                    "201" -> {
                        return null
                    }
                    "401" -> {
                        throw RequestException("请求异常异常：401 鉴权错误，请确认key是否正确", "401")
                    }
                    else -> {
                        throw RequestException("请求异常异常, code: $code", "$code")
                    }
                }
            }
        } catch (e: Throwable) {
            if (BuildConfig.DEBUG) {
                LogUtil.e("wtkLib_http", "url: ${httpUrl.url()} error: $e")
            }
            if (error != null && e is RequestException) {
                error(ExceptionUtils.parseException(e))
                return null
            } else {
                throw e
            }
        }
    }
}