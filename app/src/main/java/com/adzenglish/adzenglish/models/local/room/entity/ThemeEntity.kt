package com.adzenglish.adzenglish.models.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "theme")
data class ThemeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "position")
    val position: Int,
    @ColumnInfo(name = "category")
    var category: Int,
    @ColumnInfo(name = "bg_color")
    val backgroundColor : String,
    @ColumnInfo(name = "isSelected")
    val created_at: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
