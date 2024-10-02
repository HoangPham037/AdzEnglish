package com.adzenglish.adzenglish.models.local

data class PracticeQuestion (
    val id: Int,
    val practice_id: Int,
    val practice_position: Int,
    val en_1: String,
    val ru_1: String,
    val en_2: String,
    val ru_2: String,
    val translation_wrong: String,
    val extra_words: String,
    val rule_id: String,
    val sound: String,
    val isLearned: Boolean?=false,
    var isAnswer: Boolean?= false
)