package com.adzenglish.adzenglish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.models.local.room.entity.ThemeEntity
import com.adzenglish.adzenglish.repository.Repository
import com.adzenglish.adzenglish.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class IntroMainVM @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress

    private val _listTheme: MutableLiveData<DataStatus<List<ThemeEntity>>> = MutableLiveData()
    val listTheme: LiveData<DataStatus<List<ThemeEntity>>> get() = _listTheme

    fun startProgressAnimation() {
        viewModelScope.launch {
            for (i in 0..70 step 1) {
                _progress.postValue(i)
                delay(50)
            }

            for (i in 70..80 step 1) {
                _progress.postValue(i)
                delay(300)
            }

            for (i in 80..100 step 10) {
                _progress.postValue(i)
                delay(50)
            }
        }
    }

    private var _progressSetupData: MutableLiveData<Int> = MutableLiveData()
    val progressSetupData: LiveData<Int> get() = _progressSetupData

    fun updateProgress(progress: Int) {
        _progressSetupData.postValue(progress)
    }

    var index = 0
    fun setIndex() {
        index++
    }

    init {
        getListTheme()
    }

    private fun getListTheme() = viewModelScope.launch {
        repository.getListDataTheme()
            .catch { error ->
                _listTheme.postValue(DataStatus.error(error.message.toString()))
            }
            .collect { levelListResult ->
                _listTheme.postValue(DataStatus.success(levelListResult))
            }
    }

    fun getTotalTheme(listItem: List<ThemeEntity>): Int = listItem.count { it.category == 1 }
    fun upDateThemeToRoom(listItem: List<ThemeEntity>) {
        viewModelScope.launch (Dispatchers.IO){
            listItem.forEach { item ->
                if (item.category == 1) repository.updateTheme(item)
            }
        }
    }
}