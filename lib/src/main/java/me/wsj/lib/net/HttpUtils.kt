package me.wsj.lib.net

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.wsj.lib.net.callback.CallBack
import me.wsj.lib.net.callback.HttpCallback
import me.wsj.lib.net.exception.RequestException
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import per.wsj.commonlib.utils.LogUtil

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

    /*suspend inline fun <T> get(
            url: String,
            param: HashMap<String, Any>? = null,
            headers: HashMap<String, String>? = null,
            crossinline getResult: (T) -> Unit
    ) {
        get(url, param, headers, object : HttpCallback<T>() {
            override fun onSuccess(result: T, code: String?, msg: String?) {
                getResult(result)
            }
        })
    }

    suspend fun <T> get(
            url: String,
            param: HashMap<String, Any>? = null,
            headers: HashMap<String, String>? = null,
            callBack: CallBack<T>
    ) {
        val newUrl = if (url.startsWith("http")) url else BASE_URL + url

        val urlBuilder = HttpUrl.parse(newUrl)?.newBuilder()

        param?.let {
            it.keys.forEach { key ->
                urlBuilder?.addQueryParameter(key, it[key].toString())
            }
        }
        val httpUrl = urlBuilder?.build()
        execRequest("GET", httpUrl, headers, null, callBack)
    }

    private suspend fun <T> execRequest(
        method: String,
        url: HttpUrl?,
        headers: HashMap<String, String>?,
        requestBody: RequestBody?,
        callBack: CallBack<T>
    ) {
        val requestBuilder = Request.Builder().run {
            url(url)
            method(method, requestBody)
        }
        headers?.keys?.forEach {
            requestBuilder.addHeader(it, headers[it])
        }

        val request = requestBuilder.build()

        OkHttpUtils.getClient(cer).newCall(request).execute().use { response ->
            val body = response.body()?.string() ?: throw RequestException("数据为空")
            val jsonObject = JSONObject(body)
            when (jsonObject.get("code").toString()) {
                "200" -> {
                    withContext(Dispatchers.Main) {
                        callBack.onNext(body)
                    }
                }
                else -> {
                    throw RequestException("未知异常")
                }
            }
        }
    }*/

    suspend inline fun <reified T> get(
        url: String,
        param: HashMap<String, Any>? = null,
        headers: HashMap<String, String>? = null, crossinline getResult: (String, T) -> Unit
    ) {
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

        OkHttpUtils.getClient("").newCall(request.build()).execute().use { response ->
            val body = response.body()?.string() ?: throw RequestException("数据为空")
            val jsonObject = JSONObject(body)
            val code = jsonObject.get("code").toString()
            when (code) {
                "200" -> {
                    withContext(Dispatchers.Main) {
                        val result: T = Gson().fromJson(body, T::class.java)
                        getResult(code, result)
                    }
                }
                "401" -> {
                    throw RequestException("请求异常异常：401 鉴权错误，请确认key是否正确")
                }
                else -> {
                    throw RequestException("请求异常异常：code=$code")
                }
            }
        }
    }

    suspend inline fun <reified T> get(
        url: String,
        param: HashMap<String, Any>? = null,
        headers: HashMap<String, String>? = null
    ): T? {
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

        OkHttpUtils.getClient("").newCall(request.build()).execute().use { response ->
            val body = response.body()?.string() ?: throw RequestException("数据为空")
            val jsonObject = JSONObject(body)
            val code = jsonObject.get("code").toString()
            when (code) {
                "200" -> {
                    val result: T = Gson().fromJson(body, T::class.java)
                    return result
                }
                "401" -> {
                    throw RequestException("请求异常异常：401 鉴权错误，请确认key是否正确")
                    return null
                }
                else -> {
                    throw RequestException("请求异常异常：code=$code")
                    return null
                }
            }
        }
    }
}