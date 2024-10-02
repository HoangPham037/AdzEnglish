package com.adzenglish.adzenglish.ui.learn

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.adapter.DoConversionAdapter
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentDoConversionBinding
import com.adzenglish.adzenglish.models.local.PracticeQuestion
import com.adzenglish.adzenglish.ui.learn.result.ResultFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.formatToString
import com.adzenglish.adzenglish.viewmodel.DoConversionVM
import com.adzenglish.adzenglish.viewmodel.MainActivityVM
import com.adzenglish.adzenglish.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoConversionFragment : BaseFragmentWithBinding<FragmentDoConversionBinding>() {

    private val doConversionVM : DoConversionVM by viewModels()
    private val mainActivityVM: MainActivityVM by activityViewModels()
    private val viewModel : ViewModel by viewModels()
    private var doConversionAdapter : DoConversionAdapter ? = null
    private var lessonPosition: Int?=null
    private var levelId: Int?=null

    private var handlerTimer = Handler(Looper.getMainLooper())
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
    override fun getViewBinding(inflater: LayoutInflater): FragmentDoConversionBinding {
       return FragmentDoConversionBinding.inflate(layoutInflater)
    }

    override fun init() {
        lessonPosition = arguments?.getInt(Constants.KEY_LESSON_POSITION)
        levelId = arguments?.getInt(Constants.KEY_LEVEL_ID)
        setupAdapter()
    }

    override fun initData() {
        //do nothing
    }

    override fun initAction() {
        binding.layoutToolBar.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupAdapter() {
        startTime = System.currentTimeMillis()
        handlerTimer.post(updateTimeTask)
        doConversionAdapter = DoConversionAdapter(doConversionVM) { pos ->
            doConversionAdapter?.listItem?.size?.let {totalItem ->
                if (pos < totalItem-1 ) {
                    binding.layoutProgress.progressBar.max = totalItem
                    binding.layoutProgress.progressBar.progress = pos + 1
                    binding.recyclerViewConversion.scrollToPosition(pos + 1)
                }else {
                    if(lessonPosition != null && levelId !=null) {
                        viewModel.setCheckpointLearned(
                            true,
                            levelId!!,
                            lessonPosition!!
                        )
                        viewModel.updateIsFinishPartVocabulary(levelId!!, true)
                    }
                    mainActivityVM.setNavigateFragment(3)
                    doConversionAdapter?.let {adapter ->
                        navigateResultFragment(adapter.listItem)
                    }

                }
            }
        }
        binding.recyclerViewConversion.adapter = doConversionAdapter
        if (lessonPosition != null && levelId!= null) {
            val practice = viewModel.getPractice(lessonPosition!!, levelId!!)
            val practices = practice?.id?.let { viewModel.getListQuestionPractice(it) }
            doConversionAdapter?.submitList(practices)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        handlerTimer.removeCallbacks(updateTimeTask)
        doConversionAdapter = null
    }

    private fun navigateResultFragment(listItem: List<PracticeQuestion>) {
        handlerTimer.removeCallbacks(updateTimeTask)
        val totalRightAnswer = listItem.filter { it.isAnswer == true }
        val completionRate =
            ((totalRightAnswer.size / listItem.size.toFloat()) * 100).formatToString()
        val sumRightAwInSumQuestion =String.format("${totalRightAnswer.size}/${listItem.size}")
        Bundle().apply {
            putString(Constants.KEY_TOTAL_TIME, resultTime)
            putString(Constants.KEY_COMPLETION, completionRate)
            putString(Constants.KEY_TOTAL_RIGHT_ANSWER, sumRightAwInSumQuestion)
            putString(Constants.KEY_TYPE_FROM, Constants.KEY_FROM_DO_VOCABULARY)
            openFragment(ResultFragment::class.java, this, true)
        }
    }
}