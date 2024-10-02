package com.adzenglish.adzenglish.models.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question")
data class QuestionsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "ruleId")
    val ruleId: Int,
    @ColumnInfo(name = "rulePosition")
    val rulePosition: Int,
    @ColumnInfo(name = "task")
    val task: String,
    @ColumnInfo(name = "answer")
    val answer:String,
    @ColumnInfo(name = "wrong")
    val wrong: String
)
