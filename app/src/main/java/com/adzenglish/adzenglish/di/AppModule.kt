package com.adzenglish.adzenglish.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.adzenglish.adzenglish.models.local.room.dao.DayInfoDao
import com.adzenglish.adzenglish.models.local.room.dao.DictDao
import com.adzenglish.adzenglish.models.local.room.dao.LessonDao
import com.adzenglish.adzenglish.models.local.room.dao.LessonQuestionDao
import com.adzenglish.adzenglish.models.local.room.dao.LevelDao
import com.adzenglish.adzenglish.models.local.room.dao.QuestionDao
import com.adzenglish.adzenglish.models.local.room.dao.RuleDao
import com.adzenglish.adzenglish.models.local.room.dao.ThemeDao
import com.adzenglish.adzenglish.models.local.room.dao.WordThemeDao
import com.adzenglish.adzenglish.models.local.room.database.AppDatabase
import com.adzenglish.adzenglish.repository.Repository
import com.adzenglish.adzenglish.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSharedPreference(context: Application): SharedPreferences =
        context.getSharedPreferences(Constants.KEY_PREFS_NAME, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        Constants.KEY_APP_DATABASE,
    ).build()

    @Provides
    fun providesRepository(
        @ApplicationContext context: Context,
        levelDao: LevelDao,
        themeDao: ThemeDao,
        dictDao: DictDao,
        wordThemeDao: WordThemeDao, lessonDao: LessonDao, dayInfoDao: DayInfoDao,
        ruleDao: RuleDao,
        questionDao: QuestionDao,
        lessonQuestionDao: LessonQuestionDao
    ): Repository =
        Repository(
            context,
            levelDao,
            themeDao,
            dictDao,
            wordThemeDao,
            lessonDao,
            dayInfoDao,
            ruleDao,
            questionDao,
            lessonQuestionDao
        )

    @Provides
    fun providesAlertDao(appDatabase: AppDatabase): LevelDao = appDatabase.levelDao()

    @Provides
    fun providesThemeDao(appDatabase: AppDatabase): ThemeDao = appDatabase.themeDao()

    @Provides
    fun providesDictDao(appDatabase: AppDatabase): DictDao = appDatabase.dictDao()

    @Provides
    fun providesWordThemeDao(appDatabase: AppDatabase): WordThemeDao = appDatabase.wordThemeDao()
    fun providesLevelDao(appDatabase: AppDatabase): LevelDao = appDatabase.levelDao()

    @Provides
    fun providesLessonDao(appDatabase: AppDatabase): LessonDao = appDatabase.lessonDao()

    @Provides
    fun providesDayInfoDao(appDatabase: AppDatabase): DayInfoDao = appDatabase.dayInfoDao()

    @Provides
    fun providesRuleDao(appDatabase: AppDatabase): RuleDao = appDatabase.ruleDao()

    @Provides
    fun providesQuestionDao(appDatabase: AppDatabase): QuestionDao = appDatabase.questionDao()
    @Provides
    fun providesLessonQuestionDao(appDatabase: AppDatabase): LessonQuestionDao = appDatabase.lessonQuestionDao()
}