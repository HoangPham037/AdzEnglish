package com.adzenglish.adzenglish.ui.learn.checkpoint

data class Practice(
    val id: Int,
    val level_id: Int,
    val level_position:Int,
    val title: String,
    val header: String,
    val description: String,
    val is_premium: Int,
    val type: Int
)
