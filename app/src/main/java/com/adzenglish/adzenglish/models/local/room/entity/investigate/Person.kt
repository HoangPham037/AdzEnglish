package com.adzenglish.adzenglish.models.local.room.entity.investigate

data class Person(
    val id: Int,
    val investigation_id: Int,
    val name: String,
    val job_title: String,
    val description: String,
    val image_url: String,
    val role: Int,
    val is_culprit: Boolean,
    val animation_url: String?,
)
