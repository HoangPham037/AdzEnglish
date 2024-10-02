package com.adzenglish.adzenglish.models.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adzenglish.adzenglish.models.local.room.dao.DayInfoDao
import com.adzenglish.adzenglish.models.local.room.dao.DictDao
import com.adzenglish.adzenglish.models.local.room.dao.LessonDao
import com.adzenglish.adzenglish.models.local.room.dao.LessonQuestionDao
import com.adzenglish.adzenglish.models.local.room.dao.LevelDao
import com.adzenglish.adzenglish.models.local.room.dao.QuestionDao
import com.adzenglish.adzenglish.models.local.room.dao.RuleDao
import com.adzenglish.adzenglish.models.local.room.dao.ThemeDao
import com.adzenglish.adzenglish.models.local.room.dao.WordThemeDao
import com.adzenglish.adzenglish.models.local.room.entity.DayInfo
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.room.entity.LessonQuestionEntity
import com.adzenglish.adzenglish.models.local.room.entity.Lessons
import com.adzenglish.adzenglish.models.local.room.entity.LevelEntity
import com.adzenglish.adzenglish.models.local.room.entity.QuestionsEntity
import com.adzenglish.adzenglish.models.local.room.entity.RuleEntity
import com.adzenglish.adzenglish.models.local.room.entity.ThemeEntity
import com.adzenglish.adzenglish.models.local.room.entity.WordThemeEntity

@Database(entities = [LevelEntity::class, ThemeEntity::class, DictEntity::class, WordThemeEntity::class, Lessons::class, DayInfo::class, RuleEntity::class, QuestionsEntity::class, LessonQuestionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun levelDao(): LevelDao
    abstract fun themeDao(): ThemeDao
    abstract fun dictDao(): DictDao
    abstract fun wordThemeDao(): WordThemeDao
    abstract fun lessonDao(): LessonDao
    abstract fun dayInfoDao(): DayInfoDao
    abstract fun ruleDao(): RuleDao
    abstract fun questionDao(): QuestionDao
    abstract fun lessonQuestionDao(): LessonQuestionDao
}