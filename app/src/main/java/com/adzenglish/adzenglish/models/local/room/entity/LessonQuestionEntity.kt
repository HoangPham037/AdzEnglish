package com.adzenglish.adzenglish.models.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "lessonQuestion")
data class LessonQuestionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "lessonId")
    val lessonId: Int,
    @ColumnInfo(name = "ruleId")
    val ruleId: Int,
    @ColumnInfo(name = "trainerPosition")
    val trainerPosition: Int,
    @ColumnInfo(name = "type")
    val type:Int,
    @ColumnInfo(name = "task")
    val task: String,
    @ColumnInfo(name = "answer")
    val answer: String,
    @ColumnInfo(name = "wrong")
    val wrong: String
)
