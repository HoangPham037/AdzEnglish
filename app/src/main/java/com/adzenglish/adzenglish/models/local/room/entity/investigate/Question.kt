package com.adzenglish.adzenglish.models.local.room.entity.investigate

class Question(
  val  id: Int,
  val target_id: Int,
  val target_type: Int,
  val investigation_id: Int,
  val  position: Int,
  val   text: String,
  val  answer: Boolean,
)