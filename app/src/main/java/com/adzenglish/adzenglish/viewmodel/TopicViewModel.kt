package com.adzenglish.adzenglish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.models.local.room.entity.DayInfo
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.room.entity.ThemeEntity
import com.adzenglish.adzenglish.models.local.room.entity.WordThemeEntity
import com.adzenglish.adzenglish.repository.Repository
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    val number: LiveData<Int> get() = _number
    var listItem: ArrayList<WordThemeEntity> = arrayListOf()
    val listDict: LiveData<List<DictEntity>> get() = _listDict
    val wordTheme: LiveData<WordThemeEntity> get() = _wordTheme
    val listDayInfo: LiveData<List<DayInfo>> get() = _listDayInfo
    private val _number: MutableLiveData<Int> = MutableLiveData()
    val listTheme: LiveData<DataStatus<List<ThemeEntity>>> get() = _listTheme
    private val _listDict: MutableLiveData<List<DictEntity>> = MutableLiveData()
    private val _listDayInfo: MutableLiveData<List<DayInfo>> = MutableLiveData()
    private val _wordTheme: MutableLiveData<WordThemeEntity> = MutableLiveData()
    private val _listTheme: MutableLiveData<DataStatus<List<ThemeEntity>>> = MutableLiveData()
    private val _listDictByLessonId: MutableLiveData<DataStatus<List<DictEntity>>> =
        MutableLiveData()

    val listDictByLessonId: LiveData<DataStatus<List<DictEntity>>>
        get() = _listDictByLessonId

    fun updateTheme(item: ThemeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTheme(item)
        }
    }

    fun getSelectTopic(list: List<ThemeEntity>): Int {
        return list.count { item -> item.category == Constants.INDEX_1 }
    }

    fun getListSelect(listItem: List<ThemeEntity>): ArrayList<ThemeEntity> {
        val listTmp: ArrayList<ThemeEntity> = arrayListOf()
        viewModelScope.launch(Dispatchers.IO) {
            listItem.forEach { item -> if (item.category == Constants.INDEX_1) listTmp.add(item)
            }
        }
        return listTmp
    }

    fun getListThemeSelect() = viewModelScope.launch {
        repository.getListDataThemeBySelect()
            .catch { error ->
                _listTheme.postValue(DataStatus.error(error.message.toString()))
            }
            .collect { levelListResult ->
                _listTheme.postValue(DataStatus.success(levelListResult))
            }
    }

    fun getListDictById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _listDict.postValue(repository.getAllDictByThemeId(id))
        }
    }

    fun checkAnswer(answer: String, dictEntity: DictEntity): Boolean {
        return answer == dictEntity.wordRu
    }

    fun getTotalWord(dictEntities: List<DictEntity>): Int =
        dictEntities.count { !it.isAnswer }

    fun updateDictToRoomByLearning(dictEntities: List<DictEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            val date = getDate()
            dictEntities.forEach { item ->
                item.createdAt = date
                repository.updateDictToRoomByLearning(item)
            }
        }
    }

    fun updateDictToRoomByAddWord(dictEntities: List<DictEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            dictEntities.forEach { item ->
                if (item.isAnswer) {
                    repository.updateDictToRoomByAddWord(item)
                }
            }
        }
    }

    fun getDictByLessonId(lessonId: Int) = viewModelScope.launch {
        repository.getDictByLessonId(lessonId)
            .catch { error ->
                _listDictByLessonId.postValue(DataStatus.error(error.message.toString()))
            }
            .collect { levelListResult ->
                _listDictByLessonId.postValue(DataStatus.success(levelListResult))
            }
    }

    fun getDataDay() =
        viewModelScope.launch {
            _listDayInfo.postValue(repository.getListDayInfo())
        }

    fun insertDay(itemData: DayInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertDay(itemData)
        }
    }

    fun getDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    fun compareLightValues(day: String): Int {
        return repository.getNumberDay(day)
    }


    fun upDateWordTheme(wordTheme: WordThemeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upDateWordTheme(wordTheme)
        }
    }

    fun getWordThemeById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _wordTheme.postValue(repository.getWordThemeById(id))
        }
    }

    fun insertDayToRom(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertDayToRom(date)
        }
    }

    var listAllDict: MutableLiveData<List<DictEntity>> = MutableLiveData()
    fun getAllDict() {
        viewModelScope.launch(Dispatchers.IO) {
            listAllDict.postValue(repository.getAllDict())
        }
    }

    fun getListTheme(callBack :(List<ThemeEntity>) -> Unit)  {
        viewModelScope.launch (Dispatchers.IO){
            callBack.invoke( repository.getListTheme())
        }
    }
}