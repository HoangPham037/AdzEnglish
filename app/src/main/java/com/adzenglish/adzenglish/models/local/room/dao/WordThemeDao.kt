package com.adzenglish.adzenglish.models.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adzenglish.adzenglish.models.local.room.entity.WordThemeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordThemeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWordTheme(wordTheme: WordThemeEntity)

    @Query("SELECT * FROM word")
    fun getAllWordTheme(): Flow<MutableList<WordThemeEntity>>

    @Query("SELECT * FROM word WHERE set_id =:id ")
    fun getWordThemeBySetId(id: Int): List<WordThemeEntity>

    @Query("UPDATE word SET  is_hidden =1 WHERE id = :id ")
    fun upDateWordTheme(id: Int)

    @Query("SELECT * FROM word WHERE id =:id ")
    fun getWordThemeById(id: Int): WordThemeEntity
    @Query("UPDATE word SET  is_hidden = 0")
    fun resetData()
}