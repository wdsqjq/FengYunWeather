package me.wsj.lib.bean

/**
 * Json对象基类
 */
open class BaseBean<T> {
    var code = -1
    var msg = ""
    var result: T? = null
}