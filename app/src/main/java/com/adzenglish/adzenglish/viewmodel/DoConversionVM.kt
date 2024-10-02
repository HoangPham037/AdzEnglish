package com.adzenglish.adzenglish.viewmodel

import androidx.lifecycle.ViewModel
import com.adzenglish.adzenglish.models.local.PracticeQuestion
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DoConversionVM @Inject constructor(): ViewModel() {
    fun checkAnswer(answer: String, dictEntity: PracticeQuestion): Boolean {
        return answer == dictEntity.ru_1
    }
}