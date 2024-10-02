package com.adzenglish.adzenglish.models.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level")
data class LevelEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "position")
    val position: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "lessonsCount")
    val lessonsCount : Int,
    @ColumnInfo(name = "isSelected")
    val isSelected: Int,
    @ColumnInfo(name = "image")
    val image: String
)
