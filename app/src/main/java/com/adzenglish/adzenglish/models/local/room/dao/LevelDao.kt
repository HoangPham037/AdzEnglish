package com.adzenglish.adzenglish.models.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adzenglish.adzenglish.models.local.room.entity.LevelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLevel(level: LevelEntity)
    @Query("SELECT * FROM level")
    fun getAllLevel(): Flow<MutableList<LevelEntity>>

    @Query("SELECT * FROM level WHERE isSelected = :isSelected")
    fun getLevelById(isSelected: Int): Flow<LevelEntity>
    @Query("UPDATE level SET isSelected = :isSelected WHERE id = :levelId")
    suspend fun updateLevelIsSelectedById(levelId: Int, isSelected: Int)
    @Query("UPDATE level SET isSelected = 0 ")
    suspend fun resetAllLevel()
    @Query("SELECT * FROM level WHERE isSelected = :index")
    fun getLevel(index: Int): Flow<LevelEntity>
}