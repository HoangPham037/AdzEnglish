package com.adzenglish.adzenglish.models.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adzenglish.adzenglish.models.local.room.entity.Lessons
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: Lessons)

    @Query("SELECT * FROM lesson WHERE levelId =:levelId")
    fun getAllLesson(levelId:Int): Flow<MutableList<Lessons>>

    @Query("UPDATE lesson SET isFinishPartVocabulary = :isFinishPartVocabulary WHERE id = :levelId")
    suspend fun updateIsFinishPartVocabulary(levelId: Int, isFinishPartVocabulary: Boolean)

    @Query("UPDATE lesson SET isFinishPartGrammar = :isFinishPartGrammar WHERE id = :levelId")
    suspend fun updateIsFinishPartGrammar(levelId: Int, isFinishPartGrammar: Boolean)

    @Query("UPDATE lesson SET isFinishPartPractice = :isFinishPartPractice WHERE id = :levelId")
    suspend fun updateIsFinishPartPractice(levelId: Int, isFinishPartPractice: Boolean)

    @Query("SELECT * FROM lesson WHERE id =:id")
    fun getLessonById (id: Int): Lessons
    @Query("SELECT * FROM lesson WHERE isFinishPartGrammar = :isFinishPartPractice")
    fun getLessonLearned (isFinishPartPractice: Boolean): List<Lessons>
    @Query("UPDATE lesson SET isLearned = :isLearn WHERE levelId =:levelId and levelPosition = :levelPosition")
    suspend fun setCheckpointLearned (isLearn: Boolean, levelId: Int,levelPosition: Int)
}