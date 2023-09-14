package me.wsj.lib.logdb


import me.wsj.lib.BaseApp
import me.wsj.lib.logdb.dao.LogDao
import me.wsj.lib.logdb.entity.LogEntity


class LogRepo {


    private val logDao: LogDao = LogDatabase.getInstance(BaseApp.context).logDao()

    fun addLog(content: String) {
        logDao.addLog(LogEntity(content))
    }

    companion object {
        @Volatile
        private var instance: LogRepo? = null

        @JvmStatic
        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: LogRepo()
                        .also { instance = it }
            }
    }
}