package me.wsj.fengyun

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import kotlin.properties.Delegates

//@HiltAndroidApp
open class MyApp : Application() {

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