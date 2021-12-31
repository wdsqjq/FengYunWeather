package me.wsj.lib.net

import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException

class HttpDns : Dns {

    private val cacheHost = hashMapOf<String, InetAddress>()

    override fun lookup(hostname: String): MutableList<InetAddress> {
        if (cacheHost.containsKey(hostname)) {
            cacheHost[hostname]?.apply {
                return mutableListOf(this)
            }
        }
        return try {
            var inetAddress = InetAddress.getAllByName(hostname)
            inetAddress?.first()?.apply {
                cacheHost[hostname] = this
            }
            mutableListOf(*inetAddress)
        } catch (e: NullPointerException) {
            val unknownHostException =
                UnknownHostException("Broken system behaviour for dns lookup of $hostname")
            unknownHostException.initCause(e)
            throw unknownHostException
        }
    }
}