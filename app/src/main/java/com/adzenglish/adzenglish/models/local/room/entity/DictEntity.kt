package com.adzenglish.adzenglish.models.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "dict")
data class DictEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "lesson_id")
    val lessonId: Int,
    @ColumnInfo(name = "lesson_position")
    val lessonPosition: Int,
    @ColumnInfo(name = "word_theme_id")
    val wordThemeId: Int,
    @ColumnInfo(name = "word_en")
    val wordEn: String,
    @ColumnInfo(name = "transcription")
    val transcription: String,
    @ColumnInfo(name = "word_ru")
    val wordRu: String,
    @ColumnInfo(name = "form_2")
    var form2: String,
    @ColumnInfo(name = "form_3")
    val form3: String,
    @ColumnInfo(name = "example_en")
    var exampleEn: String,
    @ColumnInfo(name = "example_ru")
    val exampleRu: String,
    @ColumnInfo(name = "translation_wrong")
    val translationWrong: String,
    @ColumnInfo(name = "created_at")
    var createdAt: String,
    @ColumnInfo(name = "updated_at")
    var updatedAt: String,
    @ColumnInfo(name = "sound")
    val sound: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "visibility")
    val visibility : Int,
    @ColumnInfo(name = "isLeaning")
    var isLeaning : Boolean = false,
    @ColumnInfo(name = "isAnswer")
    var isAnswer : Boolean = false,
)


