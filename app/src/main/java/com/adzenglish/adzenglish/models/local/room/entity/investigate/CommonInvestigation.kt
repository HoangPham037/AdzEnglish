package com.adzenglish.adzenglish.models.local.room.entity.investigate

data class CommonInvestigation(
    val id: Int,
    val type: Int,
    val text: String,
    val name: String,
    val sound_url: String,
    val job_title: String,
    val image_url: String,
    val explanation: String?,
    val sound_level : List<Int>,
    val answer : Boolean,
    val position : Int
)
