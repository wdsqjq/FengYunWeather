package me.wsj.lib

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

//@HiltAndroidApp
open class BaseApp : Application() {

    companion object {
        var context: Context by Delegates.notNull()
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this

//        ClassLoaderInjector.inject(this, ClassLoader.getSystemClassLoader(), ArrayList<File>())
    }
}