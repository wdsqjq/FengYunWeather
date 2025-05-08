package me.wsj.lib.net.exception

import android.accounts.NetworkErrorException
import android.content.res.Resources
import android.util.MalformedJsonException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.TimeoutCancellationException
import me.wsj.lib.net.exception.RequestException
import org.json.JSONException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

object ExceptionUtils {
    /**
     * 处理异常
     */
    fun parseException(e: Throwable): String {
        return when (e) {
            is SocketTimeoutException, is TimeoutCancellationException -> "请求超时,请重试"
            is UnknownHostException -> "当前无网络，请检查你的网络设置"
            is ConnectException, is NetworkErrorException -> "网络错误,请确保网络通畅"
            is SSLHandshakeException -> "网络异常,如使用网络代理,请关闭后重试"
            is NullPointerException, is ClassCastException, is Resources.NotFoundException, is MalformedJsonException -> "数据处理异常"
            is JsonSyntaxException, is JSONException -> "数据解析异常"
            is RequestException -> e.msg
            else -> "其他异常：$e"
        }
    }
}