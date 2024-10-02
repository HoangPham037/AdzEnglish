package com.adzenglish.adzenglish.ui.learn.checkpoint

data class Checkpoint(
    val id: Int,
    val checkpoint_id:Int,
    val rule_id: Int,
    val question: String,
    val answer: Int,
    val position: Int,
    var isAnswer: Boolean,
    var isLearn: Boolean
)
