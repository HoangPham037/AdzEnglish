package com.adzenglish.adzenglish.models.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adzenglish.adzenglish.models.local.room.entity.ThemeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ThemeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertTheme(theme: ThemeEntity)
    @Query("SELECT * FROM theme ORDER BY position ASC")
    fun getAllTheme(): Flow<MutableList<ThemeEntity>>

    @Query("SELECT * FROM theme ORDER BY position ASC")
    fun getListTheme():List<ThemeEntity>

    @Query("UPDATE theme SET category = :category WHERE id = :themeId")
    suspend fun updateTheme(themeId: Int, category: Int)

    @Query("SELECT * FROM theme WHERE category = 1 ")
    fun getListThemeBySelect(): Flow<MutableList<ThemeEntity>>

    @Query("UPDATE theme SET category = :category WHERE id = :themeId")
    fun updateThemeClick(themeId: Int, category: Int)
    @Query("UPDATE theme SET category = 0")
    fun resetDateTheme()

}