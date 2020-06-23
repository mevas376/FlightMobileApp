package com.example.flightmobileapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UrlDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveUrl(url:UrlEntity)

    @Query("UPDATE URL_data_table3 SET URL_Date = :date WHERE URL_NAME= :url")
    fun updateUrl(url:String, date:Long)

    @Query("SELECT Distinct URL_NAME FROM URL_data_table3 order by URL_Date desc Limit 5")
    fun getRecentUrl(): List<String>

    @Query("SELECT Distinct URL_NAME FROM URL_data_table3 order by URL_DATE desc Limit 1")
    fun getLastUrl():String

    @Query("DELETE FROM URL_data_table3")
    fun deleteAll()
}