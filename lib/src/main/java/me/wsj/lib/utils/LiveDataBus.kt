package me.wsj.lib.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class LiveDataBus private constructor() {
    private val bus: MutableMap<String, BusMutableLiveData<Any>> by lazy { HashMap() }

    private object SingletonHolder {
        val DEFAULT_BUS = LiveDataBus()
    }

    fun <T> with2(key: String): MutableLiveData<T> {
        if (!bus.containsKey(key)) {
            bus[key] = BusMutableLiveData()
        }
        return bus[key] as MutableLiveData<T>
    }


    fun with(key: String): MutableLiveData<Any> {
        if (!bus.containsKey(key)) {
            bus[key] = BusMutableLiveData()
        }
        return bus[key] as MutableLiveData<Any>
    }

    private class ObserverWrapper<T>(private val observer: Observer<in T>?) : Observer<T> {

        override fun onChanged(t: T) {
            if (observer != null) {
                if (isCallOnObserve) {
                    return
                }
                observer.onChanged(t)
            }
        }

        private val isCallOnObserve: Boolean
            get() {
                val stackTrace = Thread.currentThread().stackTrace
                if (stackTrace != null && stackTrace.isNotEmpty()) {
                    for (element in stackTrace) {
                        if ("android.arch.lifecycle.LiveData" == element.className && "observeForever" == element.methodName) {
                            return true
                        }
                    }
                }
                return false
            }
    }

    private class BusMutableLiveData<T> : MutableLiveData<T>() {
        private val observerMap: MutableMap<Observer<in T>, Observer<in T>> = HashMap()

        // 生命周期感知的注册监听处理，去除粘性事件
        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, observer)
            try {
                hook(observer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 非生命周期感知的注册监听处理，去除粘性事件
        override fun observeForever(observer: Observer<in T>) {
            if (!observerMap.containsKey(observer)) {
                observerMap[observer] = LiveDataBus.ObserverWrapper<T>(observer)
            }
            observerMap[observer]?.let { super.observeForever(it) }
        }

        // 非生命周期感知取消注册监听
        override fun removeObserver(observer: Observer<in T>) {
            var realObserver: Observer<in T>? = null
            realObserver = if (observerMap.containsKey(observer)) {
                observerMap.remove(observer)
            } else {
                observer
            }
            realObserver?.let { super.removeObserver(it) }
        }

        // 去除粘性事件
        @Throws(Exception::class)
        private fun hook(observer: Observer<in T>) {
            //get wrapper's version
            //1.得到mLastVersion
            //获取到LivData的类中的mObservers对象
            val classLiveData = LiveData::class.java
            val fieldObservers = classLiveData.getDeclaredField("mObservers")
            fieldObservers.isAccessible = true
            //获取到这个成员变量的对象
            val objectObservers = fieldObservers[this]
            //得到map对象的class对象
            val classObservers: Class<*> = objectObservers.javaClass
            //获取到mObservers对象的get方法
            val methodGet = classObservers.getDeclaredMethod("get", Any::class.java)
            methodGet.isAccessible = true
            //执行get方法
            val objectWrapperEntry = methodGet.invoke(objectObservers, observer)
            //取到entry中的value
            var objectWrapper: Any? = null
            if (objectWrapperEntry is Map.Entry<*, *>) {
                objectWrapper = objectWrapperEntry.value
            }
            if (objectWrapper == null) {
                throw NullPointerException("Wrapper can not be bull!")
            }
            //得到observerWraperr的类对象
            val classObserverWrapper: Class<*> = objectWrapper.javaClass.superclass
            val fieldLastVersion = classObserverWrapper.getDeclaredField("mLastVersion")
            fieldLastVersion.isAccessible = true
            //get livedata's version
            //2.得到mVersion
            val fieldVersion = classLiveData.getDeclaredField("mVersion")
            fieldVersion.isAccessible = true
            //3.mLastVersion=mVersion
            val objectVersion = fieldVersion[this]
            //set wrapper's version
            fieldLastVersion[objectWrapper] = objectVersion
        }
    }

    companion object {
        fun get(): LiveDataBus {
            return SingletonHolder.DEFAULT_BUS
        }
    }
}