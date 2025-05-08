package me.wsj.lib.net

sealed class LoadState {
    /**
     * 加载中
     */
    class Start(var tip: String = "正在加载中...") : LoadState()

//    /**
//     * 成功
//     */
//    class Success<T>(t: T) : HttpState()

    /**
     * 失败
     */
    class Error(val msg: String) : LoadState()

    /**
     * 完成
     */
    object Finish : LoadState()
}