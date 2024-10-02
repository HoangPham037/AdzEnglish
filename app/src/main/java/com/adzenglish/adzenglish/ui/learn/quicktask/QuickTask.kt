package com.adzenglish.adzenglish.ui.learn.quicktask

data class QuickTask(
    val id:Int,
    val lessonId: Int,
    val ruleId: Int,
    val poolNumber: Int,
    val question: String,
    val translation:String,
    val answerWrong: String,
    val isPremium: Int,
    val sound: String
)
