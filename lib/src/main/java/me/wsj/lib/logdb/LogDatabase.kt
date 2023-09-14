package me.wsj.lib.logdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import me.wsj.lib.logdb.dao.LogDao
import me.wsj.lib.logdb.entity.LogEntity
import per.wsj.commonlib.utils.LogUtil

/**
 * auto migrate
 * https://mp.weixin.qq.com/s/SMw22_jUphQ8ee49ck3cmg
 */
@Database(
    entities = [LogEntity::class],
    version = 1,
    exportSchema = false
)
internal abstract class LogDatabase : RoomDatabase() {

    abstract fun logDao(): LogDao

    companion object {
        val DATABASE_NAME = "fy-weather-log.db"

        @Volatile
        private var instance: LogDatabase? = null

        fun getInstance(context: Context): LogDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(
                        context
                    )
                        .also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): LogDatabase {
            return Room.databaseBuilder(
                context, LogDatabase::class.java,
                DATABASE_NAME
            )
                .allowMainThreadQueries()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        LogUtil.e("db：onCreate")
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
//                        LogUtil.LOGE("db：onOpen")
                    }
                })
                .build()
        }
    }
}