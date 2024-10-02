package com.adzenglish.adzenglish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DoGrammarVM: ViewModel() {
    private val _progress: MutableLiveData<Int> = MutableLiveData()
    val progress : LiveData<Int> get() = _progress
    fun setProgress(progress: Int) {
        _progress.postValue(progress)
    }
    fun updateProgress(progress: Int) {
        _progress.value = _progress.value?.plus(progress)
    }

    private val _max: MutableLiveData<Int> = MutableLiveData(0)
    val max : LiveData<Int> get() = _max
    fun setMax(max: Int) {
        _max.postValue(max)
    }
    fun updateMax(max: Int) {
        _max.value = _max.value?.plus(max)
    }
}