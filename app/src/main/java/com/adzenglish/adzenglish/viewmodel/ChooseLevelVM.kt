package com.adzenglish.adzenglish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class ChooseLevelVM @Inject constructor(): ViewModel() {
    private val _currentItem : MutableLiveData<Int> = MutableLiveData(0)
    val currentItem : LiveData<Int> get() = _currentItem
    fun setCurrentItem(position: Int) {
        _currentItem.postValue(position)
    }

}