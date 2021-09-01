package me.wsj.fengyun.db.dao

import androidx.room.*
import me.wsj.fengyun.db.entity.CacheEntity

@Dao
interface CacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCache(cache: CacheEntity): Long

    @Query("select *from cache where `key`=:key")
    fun getCache(key: String): CacheEntity?

    @Delete
    fun deleteCache(cache: CacheEntity): Int

}