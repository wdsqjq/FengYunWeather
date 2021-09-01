package me.wsj.lib.net.exception

import java.lang.Exception

class RequestException(val msg: String) : Exception(msg) {
}