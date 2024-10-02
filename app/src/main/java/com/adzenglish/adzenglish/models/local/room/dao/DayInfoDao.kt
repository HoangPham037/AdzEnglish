package com.adzenglish.adzenglish.models.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adzenglish.adzenglish.models.local.room.entity.DayInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface DayInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertDay(dayInfo: DayInfo)
     @Query("SELECT * FROM dateinfo")
    fun getAllDay(): Flow<MutableList<DayInfo>>

    @Query("SELECT * FROM dateinfo ORDER BY date DESC LIMIT 1")
     fun getLastSavedDate(): DayInfo?

    @Query("SELECT * FROM dateinfo WHERE date = :date")
    fun getDayInfo(date: String): DayInfo?
    @Query("DELETE FROM dateinfo")
    fun resetDay()
}