package me.wsj.lib.net

import okhttp3.*
import per.wsj.commonlib.utils.LogUtil
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy


class OkHttpEventListener : EventListener() {

    override fun dnsStart(call: Call, domainName: String) {
        super.dnsStart(call, domainName)
        LogUtil.e("dnsStart: $domainName")
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: MutableList<InetAddress>) {
        super.dnsEnd(call, domainName, inetAddressList)
        LogUtil.e("dnsEnd: $domainName")
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        super.connectStart(call, inetSocketAddress, proxy)
        LogUtil.e("connectStart: ")
    }

    override fun connectEnd(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?
    ) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol)
        LogUtil.e("connectEnd: ")
    }

    override fun secureConnectStart(call: Call) {
        super.secureConnectStart(call)
        LogUtil.e("secureConnectStart: ")
    }

    override fun secureConnectEnd(call: Call, handshake: Handshake?) {
        super.secureConnectEnd(call, handshake)
        LogUtil.e("secureConnectEnd: ")
    }

    override fun requestHeadersStart(call: Call) {
        super.requestHeadersStart(call)
        LogUtil.e("requestHeadersStart: ")
    }

    override fun requestHeadersEnd(call: Call, request: Request) {
        super.requestHeadersEnd(call, request)
        LogUtil.e("requestHeadersEnd: ")
    }

    override fun requestBodyStart(call: Call) {
        super.requestBodyStart(call)
        LogUtil.e("requestBodyStart: ")
    }

    override fun requestBodyEnd(call: Call, byteCount: Long) {
        super.requestBodyEnd(call, byteCount)
        LogUtil.e("requestBodyEnd: byteCount -> $byteCount")
    }

    override fun responseBodyStart(call: Call) {
        super.responseBodyStart(call)
        LogUtil.e("responseBodyStart: ")
    }

    override fun responseBodyEnd(call: Call, byteCount: Long) {
        super.responseBodyEnd(call, byteCount)
        LogUtil.e("responseBodyEnd: byteCount -> $byteCount")
    }

    override fun callEnd(call: Call) {
        super.callEnd(call)
        LogUtil.e("callEnd: success")
    }

    override fun callFailed(call: Call, ioe: IOException) {
        super.callFailed(call, ioe)
        LogUtil.e("callFailed: fail")
    }

    companion object {
        val FACTORY = Factory {
            OkHttpEventListener()
        }
    }
}