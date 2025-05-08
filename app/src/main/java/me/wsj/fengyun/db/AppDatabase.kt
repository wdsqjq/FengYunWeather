package me.wsj.fengyun.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import me.wsj.fengyun.db.dao.CacheDao
import me.wsj.fengyun.db.dao.CityDao
import me.wsj.fengyun.db.entity.CacheEntity
import me.wsj.fengyun.db.entity.CityEntity
import per.wsj.commonlib.utils.LogUtil

/**
 * auto migrate
 * https://mp.weixin.qq.com/s/SMw22_jUphQ8ee49ck3cmg
 */
@Database(
    entities = [CacheEntity::class, CityEntity::class],
    version = 1,
    exportSchema = false
)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun cacheDao(): CacheDao

    abstract fun cityDao(): CityDao

    companion object {
        val DATABASE_NAME = "fy-weather.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(
                        context
                    )
                        .also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context, AppDatabase::class.java,
                DATABASE_NAME
            )
//                .addMigrations(MIGRATION1_2)
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

    object MIGRATION0_1 : Migration(0, 1) {
        override fun migrate(database: SupportSQLiteDatabase) {
            //1.创建一个新的符合Entity字段的新表user_new
            database.execSQL(
                "CREATE TABLE AdProfile_New (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT,"       //关注点1
                        + "url TEXT,"       //关注点1
                        + "md5 TEXT,"       //关注点1
                        + "size TEXT,"       //关注点1
                        + "download INTEGER NOT NULL,"     //关注点2
                        + "enable INTEGER NOT NULL)"     //关注点2
            )
            //2.将旧表user中的数据拷贝到新表user_new中
            database.execSQL(("INSERT INTO AdProfile_New(id,name,url,md5,size,download,enable) " + "SELECT id,name,url,md5,size,download,enable FROM AdProfile"))
            //3.删除旧表user
            database.execSQL("DROP TABLE AdProfile")
            //4.将新表user_new重命名为user,升级完毕
            database.execSQL("ALTER TABLE AdProfile_New RENAME TO AdProfile")
//            database.close()
        }
    }
}