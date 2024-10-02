package com.adzenglish.adzenglish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.models.local.Account
import com.adzenglish.adzenglish.models.local.room.entity.LevelEntity
import com.adzenglish.adzenglish.repository.Repository
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingVM @Inject constructor(val repository: Repository) : ViewModel() {
    private val _levelList: MutableLiveData<DataStatus<List<LevelEntity>>> = MutableLiveData()
    private val _account: MutableLiveData<Account?> = MutableLiveData()
    val account: LiveData<Account?> get() = _account
    private val _level: MutableLiveData<DataStatus<LevelEntity>> = MutableLiveData()
    val levelList: LiveData<DataStatus<List<LevelEntity>>>
        get() = _levelList
    val level: LiveData<DataStatus<LevelEntity>> get() = _level
    fun getAllLevelFromRoom() = viewModelScope.launch {
        repository.getAllLevel()
            .catch { error ->
                _levelList.postValue(DataStatus.error(error.message.toString()))
            }
            .collect { levelListResult ->
                _levelList.postValue(DataStatus.success(levelListResult))
            }
    }


    fun updateLevelIsSelectedById(levelId: Int, isSelected: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLevelIsSelectedById(levelId, isSelected)
        }
    }

    fun getLevelFromRoom() = viewModelScope.launch {
        repository.getLevelFromRoom()
            .catch { error ->
                _level.postValue(DataStatus.error(error.message.toString()))
            }
            .collect { levelListResult ->
                _level.postValue(DataStatus.success(levelListResult))
            }
    }


    fun getIdRandom(): Int {
        return repository.getIdRandom()
    }

    fun getAccount(id: String) {
        repository.getAccount(id) {
            _account.postValue(it)
        }
    }

    fun updateAccount(account: Account, callBack: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAccount(account, callBack)
        }
    }
    fun saveAccount(account: Account,callBack: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveAccount(account, callBack)
        }
    }
    fun resetData(deviceId: String, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            async {
                repository.resetDay()
                repository.resetDict()
                repository.insertTheme()
                repository.resetAllLevel()
                repository.resetWordTheme()
                repository.resetData(deviceId)
                repository.updateLevelIsSelectedById(Constants.INDEX_1, Constants.INDEX_1)
            }.await()
            callBack.invoke()
        }
    }

    fun startAlarmManager(newHour: Int, newMinute: Int) {
        repository.startAlarmManager(newHour , newMinute )
    }

    fun setAccount(account: Account, callBack: (Boolean) ->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setNewAccount(account , callBack)
        }
    }
}