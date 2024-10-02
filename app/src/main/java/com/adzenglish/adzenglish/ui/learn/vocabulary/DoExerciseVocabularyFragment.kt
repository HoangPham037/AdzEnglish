package com.adzenglish.adzenglish.ui.learn.vocabulary

import com.adzenglish.adzenglish.adapter.DoExerciseAdapter
import com.adzenglish.adzenglish.adapter.DoExerciseAdapter.Companion.TYPE_WRITE_ANSWER
import com.adzenglish.adzenglish.adapter.TypeClick
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentDoExerciseVocabularyBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.topic.box.BottomSheetDialogFrg
import com.adzenglish.adzenglish.ui.topic.box.BoxSixFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import com.adzenglish.adzenglish.utils.extension.formatToString
import com.adzenglish.adzenglish.viewmodel.TopicViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class DoExerciseVocabularyFragment :
    BaseFragmentWithBinding<FragmentDoExerciseVocabularyBinding>() {
    private val viewModel: TopicViewModel by viewModels()
    private lateinit var doExerciseAdapter: DoExerciseAdapter
    private var dialog: BottomSheetDialogFrg? = null
    private var handlerTimer = Handler(Looper.getMainLooper())
    private var startTime: Long = 0L
    private var resultTime: String? = "00:00"
    @Inject
    lateinit var preferences: Preferences

    private val updateTimeTask = object : Runnable {
        override fun run() {
            val elapsedTime = System.currentTimeMillis() - startTime
            val seconds = (elapsedTime / 1000).toInt() % 60
            val minutes = (elapsedTime / 1000).toInt() / 60
            resultTime = String.format("$minutes:$seconds")
            handlerTimer.postDelayed(this, 1000)
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentDoExerciseVocabularyBinding {
        return FragmentDoExerciseVocabularyBinding.inflate(layoutInflater)
    }

    override fun init() {
        dialog =  BottomSheetDialogFrg { boolean ->
            feedBack(boolean)
        }
        startTime = System.currentTimeMillis()
        handlerTimer.post(updateTimeTask)
        val isSound = preferences.getBoolean(Constants.KEY_OPEN_SOUND)
        doExerciseAdapter = DoExerciseAdapter(viewModel, isSound, { pos, typeClick ->
            when (typeClick) {
                TypeClick.IN_CHOOSE_ANSWER -> {
                    if (pos < doExerciseAdapter.listItem.size - 1) {
                        binding.layoutProgress.progressBar.progress = pos + 1
                        binding.rcvDoExercise.scrollToPosition(pos + 1)
                    } else {
                        doExerciseAdapter.updateViewType(TYPE_WRITE_ANSWER)
                        binding.rcvDoExercise.scrollToPosition(0)
                    }
                }

                TypeClick.IN_WRITE_ANSWER -> {
                    if (pos < doExerciseAdapter.listItem.size - 1) {
                        binding.layoutProgress.progressBar.progress = (doExerciseAdapter.listItem.size + 1) + pos
                        binding.rcvDoExercise.scrollToPosition(pos + 1)
                    } else {
                        navigateToBoxSixFragment(doExerciseAdapter.listItem)
                    }
                }
            }
        },{
            dialog?.show(childFragmentManager, DoExerciseVocabularyFragment::class.java.name)
        })
        binding.rcvDoExercise.adapter = doExerciseAdapter
    }

    override fun initData() {
        val lessonId = preferences.getInt(Constants.KEY_LESSON_ID)
        viewModel.listDictByLessonId.observe(viewLifecycleOwner) { dataStatusResult ->
            when (dataStatusResult.status) {
                DataStatus.Status.SUCCESS -> {
                    dataStatusResult?.data?.let { listDict ->
                        doExerciseAdapter.submitList(listDict)
                        binding.layoutProgress.progressBar.max = listDict.size * 2
                    }
                    binding.layoutProgress.progressBar.progress = 0
                }

                DataStatus.Status.ERROR -> {
                    toast(dataStatusResult.message.toString())
                }
            }
        }
       viewModel.getDictByLessonId(lessonId)
    }

    override fun initAction() {
        binding.layoutToolBar.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handlerTimer.removeCallbacks(updateTimeTask)
        dialog = null
    }

    private fun navigateToBoxSixFragment(listItem: List<DictEntity>) {
        handlerTimer.removeCallbacks(updateTimeTask)
        val totalRightAnswer = listItem.filter { it.isAnswer }
        val completionRate =
            ((totalRightAnswer.size / listItem.size.toFloat()) * 100).formatToString()
        val sumRightAwInSumQuestion =String.format("${totalRightAnswer.size}/${listItem.size}")
        Bundle().apply {
            val gson = Gson()
            val jsonString = gson.toJson(listItem)
            putString(Constants.KET_LIST_DICT, jsonString)
            putString(Constants.KEY_TOTAL_TIME, resultTime)
            putString(Constants.KEY_COMPLETION, completionRate)
            putString(Constants.KEY_TOTAL_RIGHT_ANSWER, sumRightAwInSumQuestion)
            putString(Constants.KEY_TYPE_FROM, Constants.KEY_FROM_DO_VOCABULARY)
            openFragment(BoxSixFragment::class.java, this, true)
        }
    }
    private fun feedBack(boolean: Boolean) {
        if (boolean) toast(requireContext().getString(R.string.txt_feed_back_error))
    }
}