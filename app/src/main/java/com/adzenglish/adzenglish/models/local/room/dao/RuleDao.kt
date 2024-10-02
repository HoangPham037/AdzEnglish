package com.adzenglish.adzenglish.models.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adzenglish.adzenglish.models.local.room.entity.RuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRule(rule: RuleEntity)
    @Query("SELECT * FROM rule")
    fun getAllLevel(): Flow<MutableList<RuleEntity>>

    @Query("SELECT * FROM rule WHERE lesson_id = :lessonId")
    fun getRuleByLessonId(lessonId: Int): Flow<List<RuleEntity>>

    @Query("SELECT * FROM rule WHERE isLearning = :isLearning")
    fun getListRuleByLearning(isLearning : Boolean): Flow<MutableList<RuleEntity>>

    @Query("UPDATE rule SET created_at = :date , isLearning = :learning , isStudying = :isStudying WHERE id = :id ")
    fun updateRule(id: Int, learning: Boolean, isStudying: Boolean, date: String)

    @Query("UPDATE rule SET isStudying = :studying  WHERE id = :id ")
    fun updateRuleToRoom(id: Int, studying: Boolean)

    @Query("SELECT * FROM rule WHERE isStudying = :studying")
    fun getListGrammarStudying  (studying: Boolean): Flow<List<RuleEntity>>

    @Query("SELECT * FROM rule WHERE isStudying = :studying AND isLearning = :isLearning")
    fun getListGrammarLearned  (studying: Boolean , isLearning : Boolean): Flow<List<RuleEntity>>
}