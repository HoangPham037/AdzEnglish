package com.adzenglish.adzenglish.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.provider.Settings.Secure
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.models.local.Account
import com.adzenglish.adzenglish.models.local.PracticeQuestion
import com.adzenglish.adzenglish.models.local.room.dao.DayInfoDao
import com.adzenglish.adzenglish.models.local.room.dao.DictDao
import com.adzenglish.adzenglish.models.local.room.dao.LessonDao
import com.adzenglish.adzenglish.models.local.room.dao.LessonQuestionDao
import com.adzenglish.adzenglish.models.local.room.dao.LevelDao
import com.adzenglish.adzenglish.models.local.room.dao.QuestionDao
import com.adzenglish.adzenglish.models.local.room.dao.RuleDao
import com.adzenglish.adzenglish.models.local.room.dao.ThemeDao
import com.adzenglish.adzenglish.models.local.room.dao.WordThemeDao
import com.adzenglish.adzenglish.models.local.room.entity.DayInfo
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.room.entity.LessonQuestionEntity
import com.adzenglish.adzenglish.models.local.room.entity.Lessons
import com.adzenglish.adzenglish.models.local.room.entity.LevelEntity
import com.adzenglish.adzenglish.models.local.room.entity.QuestionsEntity
import com.adzenglish.adzenglish.models.local.room.entity.RuleEntity
import com.adzenglish.adzenglish.models.local.room.entity.ThemeEntity
import com.adzenglish.adzenglish.models.local.room.entity.WordThemeEntity
import com.adzenglish.adzenglish.ui.learn.checkpoint.Checkpoint
import com.adzenglish.adzenglish.ui.learn.checkpoint.Checkpoints
import com.adzenglish.adzenglish.ui.learn.checkpoint.Practice
import com.adzenglish.adzenglish.ui.learn.grammar.QuestionSound
import com.adzenglish.adzenglish.ui.learn.quicktask.QuickTask
import com.adzenglish.adzenglish.ui.setting.remind.AlarmBroadcastReceiver
import com.adzenglish.adzenglish.utils.Constants
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random


class Repository @Inject constructor(
    private val context: Context,
    private val levelDao: LevelDao,
    private val themeDao: ThemeDao,
    private val dictDao: DictDao,
    private val wordThemeDao: WordThemeDao,
    private val lessonDao: LessonDao,
    private val dayInfoDao: DayInfoDao,
    private val ruleDao: RuleDao,
    private val questionDao: QuestionDao,
    private val lessonQuestionDao: LessonQuestionDao
) {

    private fun insertRuleToRoom(rule: RuleEntity) {
        ruleDao.insertRule(rule)
    }

    private fun insertLevelToRoom(level: LevelEntity) {
        levelDao.insertLevel(level)
    }

    fun getAllLevel() = levelDao.getAllLevel()

    suspend fun updateLevelIsSelectedById(levelId: Int, isSelected: Int) {
        levelDao.updateLevelIsSelectedById(levelId, isSelected)
    }

    suspend fun resetAllLevel() {
        levelDao.resetAllLevel()
    }

    fun resetDay() = dayInfoDao.resetDay()


    suspend fun updateIsFinishPartVocabulary(levelId: Int, isFinishPartVocabulary: Boolean) {
        lessonDao.updateIsFinishPartVocabulary(levelId, isFinishPartVocabulary)
    }

    suspend fun updateIsFinishPartGrammar(levelId: Int, isFinishPartGrammar: Boolean) {
        lessonDao.updateIsFinishPartGrammar(levelId, isFinishPartGrammar)
    }

    suspend fun updateIsFinishPartPractice(levelId: Int, isFinishPartPractice: Boolean) {
        lessonDao.updateIsFinishPartPractice(levelId, isFinishPartPractice)
    }

    suspend fun setCheckpointLearned(isLearn: Boolean, levelId: Int, levelPosition: Int) {
        lessonDao.setCheckpointLearned(isLearn, levelId, levelPosition)
    }

    private fun insertQuestion(question: QuestionsEntity) {
        questionDao.insertQuestion(question)
    }

    private fun insertLessonQuestion(question: LessonQuestionEntity) {
        lessonQuestionDao.insertLessonQuestion(question)
    }

    fun getListDataTheme() = themeDao.getAllTheme()


    suspend fun updateTheme(item: ThemeEntity) {
        themeDao.updateTheme(item.id, item.category)
    }

    fun insertLevelToRoom() {
        val listLevel: ArrayList<LevelEntity> = getListLevelFromJson()
        listLevel.forEach { levelEntity -> insertLevelToRoom(levelEntity) }
    }


    fun insertRuleToRoom() {
        val listRule: ArrayList<RuleEntity> = getListRule()
        listRule.forEach { ruleEntity -> insertRuleToRoom(ruleEntity) }
    }

    fun insertQuestionToRoom() {
        val questions: ArrayList<QuestionsEntity> = getListQuestionFromJson()
        questions.forEach { question -> insertQuestion(question) }
    }

    fun getQuestionByRuleId(ruleId: Int) = questionDao.getQuestionByRuleId(ruleId)

    fun insertLessonQuestionToRoom() {
        val lessonQuestions: ArrayList<LessonQuestionEntity> = getListLessonQuestionFromJson()
        lessonQuestions.forEach { lessonQuestion -> insertLessonQuestion(lessonQuestion) }
    }

    fun getLessonQuestionByLessonId(lessonId: Int) =
        lessonQuestionDao.getLessonQuestionByRuleId(lessonId)

    fun getListDataThemeBySelect() = themeDao.getListThemeBySelect()
    fun insertTheme() {
        try {
            getJsonFromAssets("wordtheme_set.json")?.let {
                JSONObject(it).let { obj ->
                    val levelArray = obj.getJSONArray("data")
                    for (i in 0 until levelArray.length()) {
                        val theme = levelArray.getJSONObject(i)
                        val id = theme.getInt("id")
                        val title = theme.getString("title")
                        val image = theme.getString("image")
                        val position = theme.getInt("position")
                        val category = theme.getInt("category")
                        val backgroundColor = theme.getString("bg_color")
                        val createdAt = theme.getString("created_at")
                        val updatedAt = theme.getString("updated_at")
                        val item = ThemeEntity(
                            id,
                            title,
                            image,
                            position,
                            category,
                            backgroundColor,
                            createdAt,
                            updatedAt
                        )
                        themeDao.insertTheme(item)
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    fun insertDict() {
        try {
            getJsonFromAssets("dict.json")?.let {
                JSONObject(it).let { obj ->
                    val levelArray = obj.getJSONArray("data")
                    for (i in 0 until levelArray.length()) {
                        val dict = levelArray.getJSONObject(i)
                        val image = dict.getString("image")
                        val id = dict.getInt("id")
                        val lessonId = dict.getInt("lesson_id")
                        val lessonPosition = dict.getInt("lesson_position")
                        val wordThemeId = dict.getInt("word_theme_id")
                        val wordEn = dict.getString("word_en")
                        val transcription = dict.getString("transcription")
                        val wordRu = dict.getString("word_ru")
                        val form2 = dict.getString("form_2")
                        val form3 = dict.getString("form_3")
                        val exampleEn = dict.getString("example_en")
                        val exampleRu = dict.getString("example_ru")
                        val translationWrong = dict.getString("translation_wrong")
                        val createdAt = dict.getString("created_at")
                        val updatedAt = dict.getString("updated_at")
                        val sound = dict.getString("sound")
                        val visibility = dict.getInt("visibility")
                        val item = DictEntity(
                            id,
                            lessonId,
                            lessonPosition,
                            wordThemeId,
                            wordEn,
                            transcription,
                            wordRu,
                            form2,
                            form3,
                            exampleEn,
                            exampleRu,
                            translationWrong,
                            createdAt,
                            updatedAt,
                            sound,
                            image,
                            visibility
                        )
                        dictDao.insertDict(item)
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    fun insertWordTheme() {
        try {
            getJsonFromAssets("word_theme.json")?.let {
                JSONObject(it).let { obj ->
                    val levelArray = obj.getJSONArray("data")
                    for (i in 0 until levelArray.length()) {
                        val wordTheme = levelArray.getJSONObject(i)
                        val id = wordTheme.getInt("id")
                        val setId = wordTheme.getInt("set_id")
                        val title = wordTheme.getString("title")
                        val description = wordTheme.getString("description")
                        val image = wordTheme.getString("image")
                        val headerImage = wordTheme.getString("header_image")
                        val isHidden = wordTheme.getInt("is_hidden")
                        val priority = wordTheme.getInt("priority")
                        val position = wordTheme.getInt("position")
                        val createdAt = wordTheme.getString("created_at")
                        val updatedAt = wordTheme.getString("updated_at")
                        val item = WordThemeEntity(
                            id,
                            setId,
                            title,
                            description,
                            image,
                            headerImage,
                            isHidden,
                            priority,
                            position,
                            createdAt,
                            updatedAt,
                        )
                        wordThemeDao.insertWordTheme(item)
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun getListLesson(): List<Lessons> {
        val lessonsList: ArrayList<Lessons> = ArrayList()
        try {
            val obj = JSONObject(getJsonFromAssets("lessons.json")!!)
            val levelArray = obj.getJSONArray("data")
            for (i in 0 until levelArray.length()) {
                val lessons = levelArray.getJSONObject(i)
                val id = lessons.getInt("id")
                val levelId = lessons.getInt("level_id")
                val levelPosition = lessons.getInt("level_position")
                val themeId = lessons.getInt("theme_id")
                val number = lessons.getInt("number")
                val numberUser = lessons.getInt("number_user")
                val title = lessons.getString("title")
                val isPremium = lessons.getInt("is_premium")
                val image = lessons.getString("image")
                val type = lessons.getInt("type")
                val lesson = Lessons(
                    id = id,
                    levelId = levelId,
                    levelPosition = levelPosition,
                    themeId = themeId,
                    number = number,
                    numberUser = numberUser,
                    title = title,
                    isPremium = isPremium,
                    image = image,
                    type = type
                )
                lessonsList.add(lesson)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return lessonsList
    }

    fun getListPractice(): List<Lessons> {
        val practicesList: ArrayList<Lessons> = ArrayList()
        try {
            val obj = JSONObject(getJsonFromAssets("practice.json")!!)
            val levelArray = obj.getJSONArray("data")
            for (i in 0 until levelArray.length()) {
                val practice = levelArray.getJSONObject(i)
                val id = practice.getInt("id")
                val levelId = practice.getInt("level_id")
                val levelPosition = practice.getInt("level_position")
                val title = practice.getString("title")
                val header = practice.getString("header")
                val description = practice.getString("description")
                val isPremium = practice.getInt("is_premium")
                val type = practice.getInt("type")

                val lesson = Lessons(
                    id = id,
                    levelId = levelId,
                    levelPosition = levelPosition,
                    title = title,
                    header = header,
                    description = description,
                    isPremium = isPremium,
                    type = type
                )
                practicesList.add(lesson)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return practicesList
    }

    fun getALlLesson(levelId: Int) = lessonDao.getAllLesson(levelId)
    suspend fun insertLesson(lessons: Lessons) {
        lessonDao.insertLesson(lessons)
    }

    fun getLevelById(isSelected: Int) = levelDao.getLevelById(isSelected)

    fun getRuleByLessonId(lessonId: Int) = ruleDao.getRuleByLessonId(lessonId)
    fun getListCheckPoint(): List<Lessons> {
        val checkpoints: ArrayList<Lessons> = ArrayList()
        try {
            val obj = JSONObject(getJsonFromAssets("checkpoint.json")!!)
            val levelArray = obj.getJSONArray("data")
            for (i in 0 until levelArray.length()) {
                val checkpoint = levelArray.getJSONObject(i)
                val id = checkpoint.getInt("id")
                val levelId = checkpoint.getInt("level_id")
                val levelPosition = checkpoint.getInt("level_position")
                val number = checkpoint.getInt("number")
                val isPremium = checkpoint.getInt("is_premium")
                val type = checkpoint.getInt("type")

                val lesson = Lessons(
                    id = id,
                    levelId = levelId,
                    levelPosition = levelPosition,
                    number = number,
                    isPremium = isPremium,
                    type = type
                )
                checkpoints.add(lesson)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return checkpoints
    }

    private fun getListLevelFromJson(): ArrayList<LevelEntity> {
        val levelList: ArrayList<LevelEntity> = ArrayList()
        try {
            val obj = JSONObject(getJsonFromAssets("level.json")!!)
            val levelArray = obj.getJSONArray("data")
            for (i in 0 until levelArray.length()) {
                val levels = levelArray.getJSONObject(i)
                val id = levels.getInt("id")
                val position = levels.getInt("position")
                val title = levels.getString("title")
                val description = levels.getString("description")
                val isSelected = levels.getInt("is_selected")
                val lessonsCount = levels.getInt("lessons_count")
                val img = levels.getString("image_url")
                val level =
                    LevelEntity(id, position, title, description, lessonsCount, isSelected, img)
                levelList.add(level)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return levelList
    }

    private fun getListRule(): ArrayList<RuleEntity> {
        val ruleLList: ArrayList<RuleEntity> = ArrayList()
        try {
            val obj = JSONObject(getJsonFromAssets("rule.json")!!)
            val ruleArray = obj.getJSONArray("data")
            for (i in 0 until ruleArray.length()) {
                val rules = ruleArray.getJSONObject(i)
                val id = rules.getInt("id")
                val lessonId = rules.getInt("lesson_id")
                val lessonPosition = rules.getInt("lesson_position")
                val number = rules.getInt("number")
                val rule = rules.getString("rule")
                val createdAt = rules.getString("created_at")
                val updatedAt = rules.getString("updated_at")
                val level = RuleEntity(
                    id,
                    lessonId,
                    lessonPosition,
                    number,
                    rule,
                    createdAt,
                    updatedAt,
                    isLearning = false,
                    isStudying = false
                )
                ruleLList.add(level)
            }

        } catch (e: JSONException) {
            Log.d(TAG, "insertRuleToRoom: JSONException $e")

            e.printStackTrace()
        }
        return ruleLList
    }

    private fun getListQuestionFromJson(): ArrayList<QuestionsEntity> {
        val questions: ArrayList<QuestionsEntity> = ArrayList()
        try {
            val objQuestion = JSONObject(getJsonFromAssets("question.json")!!)
            val questionArray = objQuestion.getJSONArray("data")
            for (i in 0 until questionArray.length()) {
                val question = questionArray.getJSONObject(i)
                val id = question.getInt("id")
                val ruleId = question.getInt("rule_id")
                val rulePosition = question.getInt("rule_position")
                val task = question.getString("task")
                val answer = question.getString("answer")
                val wrong = question.getString("wrong")
                val questionEntity = QuestionsEntity(id, ruleId, rulePosition, task, answer, wrong)
                questions.add(questionEntity)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return questions
    }

    private fun getListLessonQuestionFromJson(): ArrayList<LessonQuestionEntity> {
        val lessonQuestions: ArrayList<LessonQuestionEntity> = ArrayList()
        try {
            val objQuestion = JSONObject(getJsonFromAssets("lesson_question.json")!!)
            val lessonQuestionArray = objQuestion.getJSONArray("data")
            for (i in 0 until lessonQuestionArray.length()) {
                val question = lessonQuestionArray.getJSONObject(i)
                val id = question.getInt("id")
                val lessonId = question.getInt("lesson_id")
                val ruleId = question.getInt("rule_id")
                val trainerPosition = question.getInt("trainer_position")
                val type = question.getInt("type")
                val task = question.getString("task")
                val answer = question.getString("answer")
                val wrong = question.getString("wrong")
                val questionEntity = LessonQuestionEntity(
                    id,
                    lessonId,
                    ruleId,
                    trainerPosition,
                    type,
                    task,
                    answer,
                    wrong
                )
                lessonQuestions.add(questionEntity)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return lessonQuestions
    }

    fun getListQuickTaskFromJson(): ArrayList<QuickTask> {
        val quickTasks: ArrayList<QuickTask> = ArrayList()
        try {
            val objQuickTask = JSONObject(getJsonFromAssets("quick_task.json")!!)
            val lessonQuestionArray = objQuickTask.getJSONArray("data")
            for (i in 0 until lessonQuestionArray.length()) {
                val quickTask = lessonQuestionArray.getJSONObject(i)
                val id = quickTask.getInt("id")
                val lessonId = quickTask.getInt("lesson_id")
                val ruleId = quickTask.getInt("rule_id")
                val poolNumber = quickTask.getInt("pool_number")
                val question = quickTask.getString("question")
                val translation = quickTask.getString("translation")
                val answerWrong = quickTask.getString("answer_wrong")
                val isPremium = quickTask.getInt("is_premium")
                val sound = quickTask.getString("sound")
                val questionEntity = QuickTask(
                    id,
                    lessonId,
                    ruleId,
                    poolNumber,
                    question,
                    translation,
                    answerWrong,
                    isPremium,
                    sound
                )
                quickTasks.add(questionEntity)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return quickTasks
    }

    private fun getJsonFromAssets(fileName: String): String? {
        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        try {
            val myUsersJSONFile = context.assets.open(fileName)
            val size = myUsersJSONFile.available()
            val buffer = ByteArray(size)
            myUsersJSONFile.read(buffer)
            myUsersJSONFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return json
    }

    fun getAllDictByThemeId(id: Int) = dictDao.getListDict(id)

    fun getWordThemeBySetId(id: Int) = wordThemeDao.getWordThemeBySetId(id)
    fun updateDictToRoomByLearning(item: DictEntity) =
        dictDao.updateDictToRoomByLearning(item.id, item.isLeaning, item.createdAt)

    fun updateDictToRoomByAddWord(item: DictEntity) =
        dictDao.updateDictToRoomByAddWord(item.id, item.isAnswer)

    fun getDictByLessonId(lessonId: Int) = dictDao.getDictByLessonId(lessonId)
    fun insertDay(dayInfos: DayInfo) = dayInfoDao.insertDay(dayInfos)
    fun getListDayInfo(): List<DayInfo> {
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val daysOfWeek = ArrayList<DayInfo>(Constants.INDEX_7)
        for (i in Constants.INDEX_0..Constants.INDEX_6) {
            val dayName = dayFormat.format(calendar.time)
            val date = dateFormat.format(calendar.time)
            daysOfWeek.add(DayInfo(i, dayName, date))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return daysOfWeek.toList()
    }

    fun getDayInfo(date: String): DayInfo? = dayInfoDao.getDayInfo(date)
    fun getNumberDay(day: String?): Int {
        var number = 0
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = day?.let { dateFormat.parse(it) }
        date?.let {
            number = getDaysDifference(currentDate, date)
        }
        return number
    }

    private fun getDaysDifference(currentDate: Date, yesterdayDate: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.setTime(currentDate)
        val yesterdayCalendar = Calendar.getInstance()
        yesterdayCalendar.setTime(yesterdayDate)
        return calendar.get(Calendar.DAY_OF_YEAR) - yesterdayCalendar.get(Calendar.DAY_OF_YEAR)
    }

    fun upDateWordTheme(wordTheme: WordThemeEntity) = wordThemeDao.upDateWordTheme(wordTheme.id)
    fun getWordThemeById(id: Int) = wordThemeDao.getWordThemeById(id)
    fun getListWord() = dictDao.getListWord(true)
    fun insertDayToRom(date: String) = dayInfoDao.insertDay(getDayInfoInsertRom(date))
    private fun getDayInfoInsertRom(date: String): DayInfo {
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EE", Locale.getDefault())
        val dayName = dayFormat.format(calendar.time)
        return DayInfo(0, dayName, date)
    }

    fun updateDict(dictEntity: DictEntity) = dictDao.updateDict(dictEntity.id, dictEntity.exampleEn)
    fun getLevelFromRoom() = levelDao.getLevel(Constants.INDEX_1)
    fun getDeviceId(): String {
        return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
    }

    fun getAccount(id: String, callBack: (Account?) -> Unit) {
        Firebase.firestore.collection(Constants.COLLECTION_USER).document(id)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    callBack.invoke(null)
                } else {
                    if (querySnapshot != null && querySnapshot.exists()) {
                        val account = querySnapshot.toObject(Account::class.java)
                        callBack(account)
                    } else {
                        callBack.invoke(null)
                    }
                }
            }
    }

    fun getListAccounts(listAccount: MutableLiveData<ArrayList<Account>>) {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection(Constants.COLLECTION_USER)
        collectionRef.orderBy("point", Direction.DESCENDING)
            .limit(15)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) return@addSnapshotListener
                if (querySnapshot != null) {
                    val updatedList = ArrayList<Account>()
                    for (document in querySnapshot) {
                        val account = document.toObject(Account::class.java)
                        if (account.name != "") updatedList.add(account)
                    }
                    listAccount.postValue(updatedList)
                }
            }
    }

    fun saveAccount(account: Account, callBack: (Boolean) -> Unit) {
        Firebase.firestore.collection(Constants.COLLECTION_USER).document(account.androidId)
            .set(account)
            .addOnSuccessListener {
                callBack.invoke(true)
            }.addOnFailureListener {
                callBack.invoke(false)
            }
    }

    fun updateAccount(account: Account, callBack: (Boolean) -> Unit) {
        Firebase.firestore.collection(Constants.COLLECTION_USER).document(account.androidId)
            .update("name", account.name, "imageView", account.imageView)
            .addOnSuccessListener {
                callBack.invoke(true)
            }.addOnFailureListener {
                callBack.invoke(false)
            }
    }

    fun updateCoinToFireBase(coin: Int, androidId: String?, callBack: (Boolean) -> Unit) {
        val db = Firebase.firestore.collection(Constants.COLLECTION_USER)
            .document(androidId ?: getDeviceId())
        updateCoin(db, coin, callBack)
    }

    fun updateCoin(remainCoin: Int, event: (Boolean) -> Unit) {
        val db = Firebase.firestore.collection(Constants.COLLECTION_USER).document(getDeviceId())
        db.get().addOnSuccessListener { querySnapshot ->
            if (querySnapshot.exists()) {
                val account = querySnapshot.toObject(Account::class.java)
                if (account != null) updateCoin(db, remainCoin + account.gold, event)
                 else setAccount(db, remainCoin, event)
            } else{
                setAccount(db, remainCoin, event)
            }
        }.addOnFailureListener {
            event.invoke(false)
        }
    }

    fun setNewAccount(account: Account, callBack: (Boolean) -> Unit) {
        Firebase.firestore.collection(Constants.COLLECTION_USER).document(getDeviceId())
            .set(account).addOnSuccessListener {
            callBack.invoke(true)
        }.addOnFailureListener {
            callBack.invoke(false)
        }
    }

    private fun setAccount(db: DocumentReference, remainCoin: Int, event: (Boolean) -> Unit) {
        val account = Account(
            getIdRandom(),
            getDeviceId(),
            context.getString(R.string.anonymous_users),
            remainCoin,
            "",0
        )
        db.set(account).addOnSuccessListener {
            event.invoke(true)
        }.addOnFailureListener {
            event.invoke(false)
        }
    }

    private fun updateCoin(db: DocumentReference, remainCoin: Int, event: (Boolean) -> Unit) {
        db.update("gold", remainCoin)
            .addOnSuccessListener {
                event.invoke(true)
            }.addOnFailureListener {
                event.invoke(false)
            }
    }

    fun resetData(deviceId: String) {
        Firebase.firestore.collection(Constants.COLLECTION_USER).document(deviceId)
            .update(
                "name", "",
                "id", getIdRandom(),
                "imageView", "",
                "point", 0
            )
    }

    fun getIdRandom(): Int {
        return Random.nextInt(10000000, 99999999)
    }

    fun resetDict() {
        dictDao.resetData("", false)
    }

    fun resetWordTheme() {
        wordThemeDao.resetData()
    }

    fun startAlarmManager(newHour: Int, newMinute: Int) {
        val calendar = Calendar.getInstance()
        calendar[android.icu.util.Calendar.MINUTE] = newMinute
        calendar[android.icu.util.Calendar.HOUR_OF_DAY] = newHour
        calendar[android.icu.util.Calendar.SECOND] = Constants.INDEX_0
        calendar[android.icu.util.Calendar.MILLISECOND] = Constants.INDEX_0
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Constants.INDEX_0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun updateGold(point: Int, deviceId: String) {
        Firebase.firestore.collection(Constants.COLLECTION_USER).document(deviceId).get()
            .addOnSuccessListener { item ->
                val newData = item?.toObject(Account::class.java)
                newData?.let {
                    val pointNew = point + newData.point
                    Firebase.firestore.collection(Constants.COLLECTION_USER).document(deviceId)
                        .update("point", pointNew)
                }
            }
            .addOnFailureListener {

            }
    }

    fun getListQuestionPractice(practice: Int): ArrayList<PracticeQuestion> {
        val practiceQuestions: ArrayList<PracticeQuestion> = arrayListOf()
        try {
            getJsonFromAssets("practice_question.json")?.let {
                JSONObject(it).let { obj ->
                    val practiceArray = obj.getJSONArray("data")
                    for (i in 0 until practiceArray.length()) {
                        val practiceQuestion = practiceArray.getJSONObject(i)
                        val item =
                            Gson().fromJson(
                                practiceQuestion.toString(),
                                PracticeQuestion::class.java
                            )
                        if (item.practice_id == practice) {
                            practiceQuestions.add(item)
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return practiceQuestions
    }

    fun getQuestSoundByQuesId(questId: Int): QuestionSound? {
        var quesSounds: QuestionSound? = null
        try {
            getJsonFromAssets("question_sound.json")?.let {
                JSONObject(it).let { obj ->
                    val quesSoundArray = obj.getJSONArray("data")
                    for (i in 0 until quesSoundArray.length()) {
                        val quesSound = quesSoundArray.getJSONObject(i)
                        val item =
                            Gson().fromJson(quesSound.toString(), QuestionSound::class.java)
                        if (item.question_id == questId) {
                            quesSounds = item
                            break
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return quesSounds
    }

    fun getPractice(lessonPosition: Int, levelId: Int): Practice? {
        var practice: Practice? = null
        try {
            getJsonFromAssets("practice.json")?.let {
                JSONObject(it).let { obj ->
                    val practiceArray = obj.getJSONArray("data")
                    for (i in 0 until practiceArray.length()) {
                        val practiceQuestion = practiceArray.getJSONObject(i)
                        val item =
                            Gson().fromJson(practiceQuestion.toString(), Practice::class.java)
                        if (item.level_position == lessonPosition && item.level_id == levelId) {
                            practice = item
                            break
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return practice
    }

    fun getListCheckpoint(lessonPosition: Int, levelId: Int): Checkpoints? {
        var checkpoints: Checkpoints? = null
        try {
            getJsonFromAssets("checkpoint.json")?.let {
                JSONObject(it).let { obj ->
                    val practiceArray = obj.getJSONArray("data")
                    for (i in 0 until practiceArray.length()) {
                        val practiceQuestion = practiceArray.getJSONObject(i)
                        val item =
                            Gson().fromJson(practiceQuestion.toString(), Checkpoints::class.java)
                        if (item.level_position == lessonPosition && item.level_id == levelId) {
                            checkpoints = item
                            break
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return checkpoints
    }

    fun getListCheckpointQuest(checkpointId: Int): ArrayList<Checkpoint> {
        val checkpoints: ArrayList<Checkpoint> = arrayListOf()
        try {
            getJsonFromAssets("checkpoint_question.json")?.let {
                JSONObject(it).let { obj ->
                    val practiceArray = obj.getJSONArray("data")
                    for (i in 0 until practiceArray.length()) {
                        val practiceQuestion = practiceArray.getJSONObject(i)
                        val item =
                            Gson().fromJson(practiceQuestion.toString(), Checkpoint::class.java)
                        if (item.checkpoint_id == checkpointId) {
                            checkpoints.add(item)
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return checkpoints
    }

    fun getListRuleByLearning() = ruleDao.getListRuleByLearning(isLearning = true)
    fun getLessonById(id: Int): Lessons = lessonDao.getLessonById(id)
    fun getLessonLearned(learned: Boolean): List<Lessons> = lessonDao.getLessonLearned(learned)
    fun updateAtDict(dict: DictEntity, date: String) = dictDao.updateAtDict(dict.id, date)
    fun updateRule(id: Int, learning: Boolean, isStudying: Boolean, createdAt: String) =
        ruleDao.updateRule(id, learning, isStudying, createdAt)

    fun getListQuestion(id: Int) = questionDao.getQuestionByRuleId(id)
    fun updateRuleToRoom(rule: RuleEntity) = ruleDao.updateRuleToRoom(rule.id, rule.isStudying)
    fun getListGrammarStudying(studying: Boolean) = ruleDao.getListGrammarStudying(studying)
    fun getListGrammarLearned(studying: Boolean) = ruleDao.getListGrammarLearned(studying, true)
    fun getAllDict() = dictDao.getAllDict()
    fun getListTheme() = themeDao.getListTheme()
}