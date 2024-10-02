package com.adzenglish.adzenglish.models.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DictDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertDict(dict: DictEntity)

    @Query("SELECT * FROM dict")
    fun getAllDict(): MutableList<DictEntity>
    @Query("SELECT * FROM dict WHERE word_theme_id = :id")
    fun getListDict(id: Int): List<DictEntity>
    @Query("UPDATE  dict SET isLeaning = :isLearning , created_at = :date WHERE id = :id")
    fun updateDictToRoomByLearning(id : Int , isLearning : Boolean , date: String)
    @Query("UPDATE  dict SET example_en = :isAnswer  WHERE id = :id")
    fun updateDictToRoomByAddWord(id : Int , isAnswer : Boolean )

    @Query("SELECT * FROM dict WHERE isAnswer = :isAnswer")
    fun getDict(isAnswer: Boolean): List<DictEntity>

    @Query("SELECT * FROM dict WHERE lesson_id = :lessonId")
    fun getDictByLessonId(lessonId: Int): Flow<MutableList<DictEntity>>
    @Query("SELECT * FROM dict WHERE isLeaning = :isAnswer")
    fun getListWord(isAnswer: Boolean ):Flow<MutableList<DictEntity>>

    @Query("UPDATE  dict SET example_en = :exampleEn WHERE id = :id")
     fun updateDict(id: Int, exampleEn: String)
    @Query("UPDATE  dict SET example_en = :type , isAnswer = :isAnswer , created_at =:type , isLeaning = :isAnswer ")
    fun resetData(type : String , isAnswer : Boolean)

    @Query("UPDATE  dict SET updated_at = :date  WHERE id = :id ")
    fun updateAtDict(id: Int, date: String)
}