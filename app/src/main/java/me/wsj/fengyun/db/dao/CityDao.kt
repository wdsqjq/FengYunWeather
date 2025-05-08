package me.wsj.fengyun.db.dao

import androidx.room.*
import me.wsj.fengyun.db.entity.CityEntity

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCity(city: CityEntity): Long

    // 删除前定位城市
    @Query("update city set isLocal = 0 where cityId!=:cityId")
    fun removeLocal(cityId: String)

    @Query("select * from city order by isLocal desc")
    fun getCities(): List<CityEntity>

    @Query("delete from city where cityId=:id")
    fun removeCity(id: String)

    @Query("delete from city where isLocal = 0")
    fun removeNotLocalCity()

    @Query("delete from city")
    fun removeAllCity()

}