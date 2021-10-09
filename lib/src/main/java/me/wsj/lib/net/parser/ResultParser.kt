package me.wsj.lib.net.parser

import java.lang.reflect.Type

interface ResultParser<T> {
    fun parse(json: String): T
}