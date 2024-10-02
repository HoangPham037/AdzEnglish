package com.adzenglish.adzenglish.models.local.room.entity.investigate

data class WitnessPhrase(
    val id: Int,
    val target_id: Int,
    val target_type: Int,
    val investigation_id: Int,
    val position: Int,
    val text: String,
    val person_id: Int,
    val sound_url: String,
    val sound_level: List<Int>,
    val image_url: String,
    val explanation: String
)
