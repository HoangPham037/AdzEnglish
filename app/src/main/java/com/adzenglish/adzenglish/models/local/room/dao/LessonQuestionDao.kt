package com.adzenglish.adzenglish.models.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adzenglish.adzenglish.models.local.room.entity.LessonQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLessonQuestion(question: LessonQuestionEntity)
    @Query("SELECT * FROM lessonQuestion")
    fun getAllLessonQuestion(): Flow<MutableList<LessonQuestionEntity>>

    @Query("SELECT * FROM lessonQuestion WHERE lessonId = :lessonId")
    fun getLessonQuestionByRuleId(lessonId: Int): List<LessonQuestionEntity>
}