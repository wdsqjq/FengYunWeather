package me.wsj.fengyun.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import me.wsj.fengyun.BuildConfig
import me.wsj.lib.net.LoadState
import me.wsj.lib.net.exception.ExceptionUtils
import per.wsj.commonlib.utils.LogUtil
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger

open class BaseViewModel(app: Application) : AndroidViewModel(app) {

    // 加载状态
    val loadState = MutableLiveData<LoadState>()

    /**
     * 是否登录
     */
    val isLogin = MutableLiveData<Boolean>()

//    @Volatile
//    private var runningCount = 0

    private var runningCount = AtomicInteger(0)

    /**
     * 是否正在请求网络
     */
    fun isStopped(): Boolean {
        return runningCount.get() == 0
    }

    /**
     * 后台静默加载,不显示loading
     */
    fun launchSilent(block: suspend CoroutineScope.() -> Unit) {
        launchRequest(1, block)
    }

    /**
     * 开始显示loading,结束关闭loading
     */
    fun launch(block: suspend CoroutineScope.() -> Unit) {
        launchRequest(block = block)
    }

    /**
     * @param loadingType 0: 默认 1: silent
     */
    private fun launchRequest(loadingType: Int = 0, block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch() {
            try {
                if (loadingType == 0) {
                    runningCount.getAndIncrement()
//                    LogUtil.LOGE("runningCount + : $runningCount")
                    loadState.value = LoadState.Start()
                }
                withContext(Dispatchers.IO) {
                    block.invoke(this)
                }
            } catch (e: Throwable) {
                // handle error
                val error = ExceptionUtils.parseException(e)
                if (BuildConfig.DEBUG) {
                    LogUtil.e("$loadingType -> 异常：$e")
                    e.printStackTrace()
                }
                if (loadingType == 0) {
                    loadState.value = LoadState.Error(error)
                    if (runningCount.get() > 0) {
                        runningCount.getAndDecrement()
                    }
                    LogUtil.d("runningCount - : $runningCount")
                    loadState.value = LoadState.Finish
                }
            } finally {
                if (loadingType == 0) {
                    if (runningCount.get() > 0) {
                        runningCount.getAndDecrement()
                    }
//                    LogUtil.LOGE("runningCount - : $runningCount")
                    loadState.value = LoadState.Finish
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        runningCount.getAndSet(0)
    }
}