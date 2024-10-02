package com.adzenglish.adzenglish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashVM @Inject constructor(): ViewModel() {
    private val _navigateScreen : MutableLiveData<Unit> = MutableLiveData()
    val navigateScreen: LiveData<Unit> get() = _navigateScreen

     fun startDelay() {
        viewModelScope.launch {
            delay(10)
            _navigateScreen.value = Unit
        }
    }
}