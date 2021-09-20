package me.wsj.lib.net

import me.wsj.lib.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import per.wsj.commonlib.net.SSLCerUtils
import per.wsj.commonlib.net.interceptor.LogInterceptor
import java.net.Proxy
import java.util.concurrent.TimeUnit

object OkHttpUtils {

    private const val DEFAULT_TIMEOUT = 15L

    // log interceptor
    private val mLoggingInterceptor: Interceptor by lazy { LogInterceptor() }

    private var cer: String? = null

    private val mClient: OkHttpClient by lazy { generateClient(cer) }

    fun getClient(cer: String? = ""): OkHttpClient {
        this.cer = cer
        return mClient
    }

    private fun generateClient(cer: String?): OkHttpClient {
        val builder = OkHttpClient.Builder().apply {
            connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
//            hostnameVerifier(SSLSocketClient.getHostnameVerifier())
        }

        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(mLoggingInterceptor)
        } else {
            builder.proxy(Proxy.NO_PROXY)
        }
//        if (cer.isNullOrEmpty()) {
//            // 信任所有证书
        SSLCerUtils.setTrustAllCertificate(builder)
//        } else {
//            // https证书
//            SSLCerUtils.setCertificate(context, builder, cer)
//        }

        return builder.build()
    }
}