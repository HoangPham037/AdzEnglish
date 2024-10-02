package com.adzenglish.adzenglish.ui.learn.checkpoint

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentCheckPointBinding
import com.adzenglish.adzenglish.ui.learn.result.ResultFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.formatToString
import com.adzenglish.adzenglish.viewmodel.MainActivityVM
import com.adzenglish.adzenglish.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckPointFragment : BaseFragmentWithBinding<FragmentCheckPointBinding>() {
    private var doCheckPointAdapter: DoCheckPointAdapter? = null
    private val viewModel: ViewModel by viewModels()
    private val mainActivityVM: MainActivityVM by activityViewModels()
    private var lessonPosition: Int? = null
    private var levelId: Int? = null
    private val handlerTimer = Handler(Looper.getMainLooper())
    private var startTime: Long = 0L
    private var resultTime: String? = "00:00"

    private val updateTimeTask = object : Runnable {
        override fun run() {
            val elapsedTime = System.currentTimeMillis() - startTime
            val seconds = (elapsedTime / 1000).toInt() % 60
            val minutes = (elapsedTime / 1000).toInt() / 60
            resultTime = String.format("$minutes:$seconds")
            handlerTimer.postDelayed(this, 1000)
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentCheckPointBinding {
        return FragmentCheckPointBinding.inflate(layoutInflater)
    }

    override fun init() {
        startTime = System.currentTimeMillis()
        handlerTimer.post(updateTimeTask)
        lessonPosition = arguments?.getInt(Constants.KEY_LESSON_POSITION)
        levelId = arguments?.getInt(Constants.KEY_LEVEL_ID)
        doCheckPointAdapter = DoCheckPointAdapter { pos ->
            doCheckPointAdapter?.listItem?.size?.let {
                val answerRight =
                    doCheckPointAdapter?.listItem?.filter { checkpoint -> checkpoint.isAnswer && checkpoint.isLearn }?.size
                val totalQuest = doCheckPointAdapter?.listItem?.size?.toFloat()
                val sumRightAwInSumQuestion = String.format("${answerRight}/${totalQuest?.toInt()}")
                if (pos < it - 1) {
                    binding.recyclerViewCheckpoint.smoothScrollToPosition(pos + 1)
                    binding.layoutProgress.progressBar.progress = pos + 1
                    binding.tvAnswerRight.text =
                        doCheckPointAdapter?.listItem?.filter { checkpoint -> checkpoint.isAnswer && checkpoint.isLearn }?.size.toString()
                    binding.tvWrongAnswer.text =
                        doCheckPointAdapter?.listItem?.filter { checkpoint -> !checkpoint.isAnswer && checkpoint.isLearn }?.size.toString()
                } else {
                    if(lessonPosition != null && levelId !=null) {
                        viewModel.setCheckpointLearned(
                            true,
                            levelId!!,
                            lessonPosition!!
                        )
                        viewModel.updateIsFinishPartVocabulary(levelId!!, true)
                    }
                    val completionRate = totalQuest?.let { it1 -> answerRight?.div(it1) }
                        ?.times(100)?.formatToString()
                    mainActivityVM.setNavigateFragment(Constants.INDEX_3)
                    Bundle().apply {
                        putString(Constants.KEY_TOTAL_TIME, resultTime)
                        putString(Constants.KEY_COMPLETION, completionRate)
                        putString(Constants.KEY_TOTAL_RIGHT_ANSWER, sumRightAwInSumQuestion)
                        openFragment(ResultFragment::class.java, this, true)
                    }
                }
            }
        }
        binding.recyclerViewCheckpoint.adapter = doCheckPointAdapter
    }

    override fun initData() {
        if (lessonPosition != null && levelId != null) {
            val checkpoints = viewModel.getListCheckpoint(lessonPosition!!, levelId!!)
            val checkpointQuest =
                checkpoints?.id?.let { viewModel.getListCheckpointQuest(it) }?.take(10)
            doCheckPointAdapter?.submitList(checkpointQuest)
            checkpointQuest?.size?.let {
                binding.layoutProgress.progressBar.max = it
            }
        }
    }

    override fun initAction() {
        binding.layoutToolBar.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handlerTimer.removeCallbacks(updateTimeTask)
    }

}