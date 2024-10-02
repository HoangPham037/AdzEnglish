package com.adzenglish.adzenglish.viewmodel

import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.room.entity.Lessons
import com.adzenglish.adzenglish.models.local.room.entity.QuestionsEntity
import com.adzenglish.adzenglish.models.local.room.entity.RuleEntity
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
class PracticeVM @Inject constructor(val repository: Repository) : ViewModel() {
    private val _listWord: MutableLiveData<DataStatus<List<DictEntity>>> = MutableLiveData()
    private val _listRule: MutableLiveData<DataStatus<List<RuleEntity>>> = MutableLiveData()
    private val _listGrammarStudying: MutableLiveData<DataStatus<List<RuleEntity>>> =
        MutableLiveData()
    private val _lesson: MutableLiveData<Lessons> = MutableLiveData()
    private val _listGrammarLearned: MutableLiveData<DataStatus<List<RuleEntity>>> =
        MutableLiveData()
    val listWord: LiveData<DataStatus<List<DictEntity>>> get() = _listWord
    val listRule: LiveData<DataStatus<List<RuleEntity>>> get() = _listRule
    val listGrammarStudying: LiveData<DataStatus<List<RuleEntity>>> get() = _listGrammarStudying
    val lesson: LiveData<Lessons> get() = _lesson
    val listGrammarLearned: LiveData<DataStatus<List<RuleEntity>>> get() = _listGrammarLearned
    fun getListWord() = viewModelScope.launch {
        repository.getListWord()
            .catch { error ->
                _listWord.postValue(DataStatus.error(error.message.toString()))
            }
            .collect { levelListResult ->
                _listWord.postValue(DataStatus.success(levelListResult))
            }
    }

    fun getDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    fun getTime(): Int {
        val calendar: Calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        return 24 - hour
    }

    fun getWord(listDict: List<DictEntity>): String {
        return listDict.map { dict ->
            dict.wordEn
        }.take(Constants.INDEX_5).toString().replace("[", "").replace("]", "")
    }

    fun updateDict(dictEntity: DictEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateDict(dictEntity)
        }
    }

    fun updateGold(point: Int, deviceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateGold(point, deviceId)
        }
    }

    fun getListRule() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getListRuleByLearning().catch { error ->
                _listRule.postValue(DataStatus.error(error.message.toString()))
            }.collect { levelListResult ->
                _listRule.postValue(DataStatus.success(levelListResult))
            }
        }
    }


    fun updateAtDict(dict: DictEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAtDict(dict, getDate())
        }
    }

    fun getListQuestion(id: Int): List<QuestionsEntity> {
        return repository.getListQuestion(id)
    }

    fun updateRuleToRoom(rule: RuleEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRuleToRoom(rule)
        }
    }

    fun getListGrammarStudying(isStudying: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getListGrammarStudying(isStudying).catch { error ->
                _listGrammarStudying.postValue(DataStatus.error(error.message.toString()))
            }
                .collect { levelListResult ->
                    _listGrammarStudying.postValue(DataStatus.success(levelListResult))
                }
        }
    }

    fun getListGrammarLearned(isStudying: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getListGrammarLearned(isStudying).catch { error ->
                _listGrammarLearned.postValue(DataStatus.error(error.message.toString()))
            }
                .collect { levelListResult ->
                    _listGrammarLearned.postValue(DataStatus.success(levelListResult))
                }
        }
    }

    fun getLesson(lessonId: Int) {
        viewModelScope.launch (Dispatchers.IO){
         _lesson.postValue( repository.getLessonById(lessonId))
        }
    }
}