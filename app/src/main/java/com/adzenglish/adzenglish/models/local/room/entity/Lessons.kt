package com.adzenglish.adzenglish.models.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "lesson")
data class Lessons(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int? = 0,
    @ColumnInfo(name = "levelId")
    val levelId: Int,
    @ColumnInfo(name = "levelPosition")
    val levelPosition: Int,
    @ColumnInfo(name = "themeId")
    val themeId: Int?=0,
    @ColumnInfo(name = "number")
    val number: Int? = 0,
    @ColumnInfo(name = "numberUser")
    val numberUser: Int? = 0,
    @ColumnInfo(name = "title")
    val title: String? = null,
    @ColumnInfo(name = "isPremium")
    val isPremium: Int,
    @ColumnInfo(name = "image")
    val image: String? = null,
    @ColumnInfo(name = "header")
    val header: String? = null,
    @ColumnInfo(name = "description")
    val description: String? = null,
    @ColumnInfo(name = "type")
    val type: Int,
    @ColumnInfo(name = "isFinishPartVocabulary")
    val isFinishPartVocabulary: Boolean?=false ,
    @ColumnInfo(name = "isFinishPartGrammar")
    val isFinishPartGrammar: Boolean?=false,
    @ColumnInfo(name = "isFinishPartPractice")
    val isFinishPartPractice: Boolean?=false,
    @ColumnInfo(name = "isLearned")
    val isLearned: Boolean?= false
)