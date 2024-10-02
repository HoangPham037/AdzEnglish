package com.adzenglish.adzenglish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.models.local.Account
import com.adzenglish.adzenglish.models.local.PracticeQuestion
import com.adzenglish.adzenglish.models.local.room.entity.LessonQuestionEntity
import com.adzenglish.adzenglish.models.local.room.entity.Lessons
import com.adzenglish.adzenglish.models.local.room.entity.LevelEntity
import com.adzenglish.adzenglish.models.local.room.entity.RuleEntity
import com.adzenglish.adzenglish.repository.Repository
import com.adzenglish.adzenglish.ui.learn.checkpoint.Checkpoint
import com.adzenglish.adzenglish.ui.learn.checkpoint.Checkpoints
import com.adzenglish.adzenglish.ui.learn.checkpoint.Practice
import com.adzenglish.adzenglish.ui.learn.grammar.QuestionSound
import com.adzenglish.adzenglish.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    private val _levelList: MutableLiveData<DataStatus<List<LevelEntity>>> = MutableLiveData()
    val levelList: LiveData<DataStatus<List<LevelEntity>>>
        get() = _levelList

    private val _lessonList: MutableLiveData<DataStatus<List<Lessons>>> = MutableLiveData()
    private val _account: MutableLiveData<Account> = MutableLiveData()
    val lessonList: LiveData<DataStatus<List<Lessons>>>
        get() = _lessonList
    val listAccount: MutableLiveData<ArrayList<Account>> = MutableLiveData()
    val account: MutableLiveData<Account> get() = _account

    init {
        getAllLevelFromRoom()
    }

    private fun getAllLevelFromRoom() = viewModelScope.launch {
        repository.getAllLevel()
            .catch { error ->
                _levelList.postValue(DataStatus.error(error.message.toString()))
            }
            .collect { levelListResult ->
                _levelList.postValue(DataStatus.success(levelListResult))
            }
    }

    private val _levelById: MutableLiveData<DataStatus<LevelEntity>> = MutableLiveData()
    val levelById: LiveData<DataStatus<LevelEntity>>
        get() = _levelById

    fun getLevelById(isSelected: Int) = viewModelScope.launch {
        repository.getLevelById(isSelected)
            .catch { error ->
                _levelById.postValue(DataStatus.error(error.message.toString()))
            }
            .collect { levelListResult ->
                _levelById.postValue(DataStatus.success(levelListResult))
            }
    }

    fun updateLevelIsSelectedById(levelId: Int, isSelected: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLevelIsSelectedById(levelId, isSelected)
        }
    }

    fun updateIsFinishPartVocabulary(levelId: Int, isFinishPartVocabulary: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsFinishPartVocabulary(levelId, isFinishPartVocabulary)
        }
    }

    fun updateIsFinishPartGrammar(levelId: Int, isFinishPartGrammar: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsFinishPartGrammar(levelId, isFinishPartGrammar)
        }
    }

    fun updateIsFinishPartPractice(levelId: Int, isFinishPartPractice: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsFinishPartPractice(levelId, isFinishPartPractice)
        }
    }

    fun setCheckpointLearned(isLearn: Boolean, levelId: Int, levelPosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setCheckpointLearned(isLearn, levelId, levelPosition)
        }
    }

    fun getAllLessonFromRoom(levelId: Int) = viewModelScope.launch {
        repository.getALlLesson(levelId)
            .catch { error ->
                _lessonList.postValue(DataStatus.error(error.message.toString()))
            }
            .collect { lessonListResult ->
                _lessonList.postValue(DataStatus.success(lessonListResult))
            }
    }

    private val _ruleByLessonId: MutableLiveData<DataStatus<List<RuleEntity>>> = MutableLiveData()
    val ruleByLessonId: LiveData<DataStatus<List<RuleEntity>>>
        get() = _ruleByLessonId

    fun getRuleByLessonId(lessonId: Int) = viewModelScope.launch {
        repository.getRuleByLessonId(lessonId)
            .catch { error ->
                _ruleByLessonId.postValue(DataStatus.error(error.message.toString()))
            }
            .collect { levelListResult ->
                _ruleByLessonId.postValue(DataStatus.success(levelListResult))
            }
    }

    private val _lessonQuestionByRuleId: MutableLiveData<List<LessonQuestionEntity>> =
        MutableLiveData()
    val lessonQuestionByRuleId: LiveData<List<LessonQuestionEntity>>
        get() = _lessonQuestionByRuleId

    fun getLessonQuestionByLessonId(lessonId: Int) = viewModelScope.launch(Dispatchers.IO) {
        _lessonQuestionByRuleId.postValue(repository.getLessonQuestionByLessonId(lessonId))
    }

    fun getListQuickTaskFromJson(lessonId: Int, poolNumber: Int) =
        repository.getListQuickTaskFromJson().filter { quickTask ->
            quickTask.lessonId == lessonId && quickTask.poolNumber == poolNumber
        }

    fun getDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    fun initData(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            async {
                repository.insertDict()
                repository.insertTheme()
                repository.insertWordTheme()
                repository.insertRuleToRoom()
                repository.insertLevelToRoom()
                repository.insertQuestionToRoom()
                repository.insertLessonQuestionToRoom()
                insertByLesson()
            }.await()
            callback.invoke()
        }
    }

    private suspend fun insertByLesson() {
        val sumList: MutableList<Lessons> = mutableListOf()
        sumList.addAll(getListLesson())
        sumList.addAll(getListPractice())
        sumList.addAll(getListCheckPoint())
        for (lesson in sumList) {
            repository.insertLesson(lesson)
        }
    }

    fun getListAccounts() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getListAccounts(listAccount)
        }
    }

    private fun getListLesson() = repository.getListLesson()
    private fun getListPractice() = repository.getListPractice()
    private fun getListCheckPoint() = repository.getListCheckPoint()

    fun getListQuestionPractice(practice: Int): ArrayList<PracticeQuestion> {
        return repository.getListQuestionPractice(practice)
    }

    fun updateRule(rule: RuleEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRule(rule.id, rule.isLearning, rule.isStudying, rule.createdAt)
        }
    }

    fun getListCheckpoint(lessonPosition: Int, levelId: Int): Checkpoints? {
        return repository.getListCheckpoint(lessonPosition, levelId)
    }

    fun getPractice(lessonPosition: Int, levelId: Int): Practice? {
        return repository.getPractice(lessonPosition, levelId)
    }

    fun getQuestSoundByQuesId(questId: Int): QuestionSound? {
        return repository.getQuestSoundByQuesId(questId)
    }

    fun getListCheckpointQuest(checkpointId: Int): ArrayList<Checkpoint> {
        return repository.getListCheckpointQuest(checkpointId)
    }

    private val _listLearned: MutableLiveData<List<Lessons>> = MutableLiveData()
    val listLearned: LiveData<List<Lessons>> get() = _listLearned
    fun getLessonLearned(isLearned: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _listLearned.postValue(repository.getLessonLearned(isLearned))
        }
    }

    fun getAccount(deviceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAccount(deviceId) {
                _account.postValue(it)
            }
        }
    }

    fun updateGold(point: Int, deviceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateGold(point, deviceId)
        }
    }

    fun updateCoin(coin: Int, androidId: String?, callBack: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCoinToFireBase(coin, androidId, callBack)
        }
    }
}