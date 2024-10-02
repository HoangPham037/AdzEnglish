package com.adzenglish.adzenglish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MainActivityVM @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _navigateFragment: MutableLiveData<Int> = MutableLiveData(1)
    val navigateFragment : LiveData<Int> get() = _navigateFragment

    fun setNavigateFragment(toFragment: Int) {
        _navigateFragment.postValue(toFragment)
    }

    fun updateCoin(remainCoin: Int , event : (Boolean) ->Unit) {
        viewModelScope.launch (Dispatchers.IO){
            repository.updateCoin(remainCoin , event)
        }
    }
}