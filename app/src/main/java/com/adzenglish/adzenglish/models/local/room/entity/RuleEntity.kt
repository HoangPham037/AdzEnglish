package com.adzenglish.adzenglish.models.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rule")
data class RuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "lesson_id") val lessonId: Int,
    @ColumnInfo(name = "lesson_position") val lessonPosition: Int,
    @ColumnInfo(name = "number") val number: Int,
    @ColumnInfo(name = "rule") val rule: String,
    @ColumnInfo(name = "created_at") var createdAt: String,
    @ColumnInfo(name = "updated_at") var updatedAt: String,
    @ColumnInfo(name = "isLearning") var isLearning: Boolean ,
    @ColumnInfo(name = "isStudying") var isStudying: Boolean ,
)
