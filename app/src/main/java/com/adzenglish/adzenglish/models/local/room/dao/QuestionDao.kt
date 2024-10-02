package com.adzenglish.adzenglish.models.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adzenglish.adzenglish.models.local.room.entity.QuestionsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestion(question: QuestionsEntity)
    @Query("SELECT * FROM question")
    fun getAllQuestion(): Flow<MutableList<QuestionsEntity>>

    @Query("SELECT * FROM question WHERE ruleId = :ruleId")
    fun getQuestionByRuleId(ruleId: Int): List<QuestionsEntity>
    @Query("SELECT * FROM question WHERE ruleId = :id ORDER BY rulePosition DESC LIMIT 2 ")
    fun getQuestionById(id: Int): MutableList<QuestionsEntity>
}