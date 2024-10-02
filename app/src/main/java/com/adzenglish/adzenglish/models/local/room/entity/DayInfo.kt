package com.adzenglish.adzenglish.models.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dateinfo")
data class DayInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "dayName")
    val dayName: String,
    @ColumnInfo(name = "date")
    val date: String,
)
