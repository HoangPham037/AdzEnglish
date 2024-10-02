package com.adzenglish.adzenglish.models.local.room.entity

data class PracticeEntity(
    val id: Int,
    val levelId: Int,
    val levelPosition: Int,
    val title: String,
    val header: String,
    val description: String,
    val isPremium: Int
)
