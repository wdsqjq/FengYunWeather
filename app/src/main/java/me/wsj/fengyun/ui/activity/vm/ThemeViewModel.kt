package me.wsj.fengyun.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import me.wsj.fengyun.ui.base.BaseViewModel
import me.wsj.lib.net.OkHttpUtils
import me.wsj.lib.utils.SpUtil
import okhttp3.Request
import per.wsj.commonlib.utils.FileUtil
import per.wsj.commonlib.utils.LogUtil
import java.io.File
import java.io.FileOutputStream

class ThemeViewModel(private val app: Application) : BaseViewModel(app) {

    val downloadStatus = MutableLiveData<Boolean>()

    fun downPlugin() {
        val path =
            "https://wangsj.oss-cn-shanghai.aliyuncs.com/fengyun/plugin/plugin-colorful.apk"
        val destFileName = FileUtil.getNameFromPath(path)
        val destFileDir = app.externalCacheDir!!.absolutePath + File.separator + "plugin"
        //储存下载文件的目录
        val dir = File(destFileDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, destFileName)
        launch {
            flow<Int> {
                val request = Request.Builder().url(path).build()
                OkHttpUtils.getClient().newCall(request).execute().use {
                    val buf = ByteArray(1024)
                    var len = 0

                    val inputStream = it.body()!!.byteStream()
                    val total = it.body()!!.contentLength()
                    val fos = FileOutputStream(file)
                    var sum: Long = 0
                    var curProgress = 0
                    while (inputStream.read(buf).also { len = it } != -1) {
                        fos.write(buf, 0, len)
                        sum += len.toLong()
                        val progress = (sum * 1.0f / total * 100).toInt()
                        if(curProgress!=progress){
                            curProgress = progress
                            emit(progress)
                        }
                    }
                    fos.flush()
                    fos.close()
                    //下载完成
                    LogUtil.e("下载完成。。。")
                    SpUtil.setPluginPath(app, file.absolutePath)
                    downloadStatus.postValue(true)
                }
            }.collect {
                LogUtil.e("progress: $it")
            }
        }
    }
}