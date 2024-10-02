package com.adzenglish.adzenglish.ui.learn.quicktask

import adapter.DoQuickTaskAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.base.media.MediaManager
import com.adzenglish.adzenglish.databinding.FragmentQuickTaskBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.learn.result.ResultFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.gone
import com.adzenglish.adzenglish.utils.extension.onAnimationEnd
import com.adzenglish.adzenglish.viewmodel.MainActivityVM
import com.adzenglish.adzenglish.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuickTaskFragment : BaseFragmentWithBinding<FragmentQuickTaskBinding>() {
    private val viewModel: ViewModel by viewModels()
    private lateinit var doQuickTaskAdapter: DoQuickTaskAdapter
    private var listQuickTask = ArrayList<QuickTask>()
    private var listItem = ArrayList<QuickTask>()
    private val mainActivityVM: MainActivityVM by activityViewModels()

    @Inject
    lateinit var preferences: Preferences
    override fun getViewBinding(inflater: LayoutInflater): FragmentQuickTaskBinding {
        return FragmentQuickTaskBinding.inflate(layoutInflater)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun init() {
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_view)
        val fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_view)
        doQuickTaskAdapter = DoQuickTaskAdapter(this) { quickTask ->
            val regex = "\\(([^)]+)\\)".toRegex()
            val matchResult = quickTask.question.let { regex.find(it) }
            if (matchResult != null) {
                val matchedString = matchResult.groupValues[1]
                val answers = getShuffledAnswers(matchedString, quickTask.answerWrong)
                binding.answerOne.text = answers[0]
                binding.answerTwo.text = answers[1]
                binding.answerOne.click {
                    if (matchedString.contains(binding.answerOne.text.toString())) {
                        if (listQuickTask.size > 0) {
                            handleListItem(fadeIn, fadeOut, quickTask)
                        } else {
                            navigateResult()
                        }
                    } else {
                        binding.imgStateAnswer.setImageResource(R.drawable.ic_wrong)
                        startAnimAnswer(fadeIn, fadeOut)
                    }
                }
                binding.answerTwo.click {
                    if (matchedString.contains(binding.answerTwo.text.toString())) {
                        if (listQuickTask.size > 0) {
                            handleListItem(fadeIn, fadeOut, quickTask)
                        } else {
                            navigateResult()
                        }
                    } else {
                        binding.imgStateAnswer.setImageResource(R.drawable.ic_wrong)
                        startAnimAnswer(fadeIn, fadeOut)
                    }
                }
            }
        }
        binding.rcvQuickTask.layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true
        }
        binding.rcvQuickTask.adapter = doQuickTaskAdapter
    }

    private fun handleListItem(
        fadeIn: Animation,
        fadeOut: Animation,
        quickTask: QuickTask
    ) {
        binding.imgStateAnswer.setImageResource(R.drawable.ic_exactly)
        startAnimAnswer(fadeIn, fadeOut)
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            if (quickTask.sound.isNotEmpty()) {
                MediaManager.getInstance()?.playWithPath(quickTask.sound)
            }
        }
        val item = listQuickTask.removeAt(Constants.INDEX_0)
        listItem.add(0, item)
        doQuickTaskAdapter.notifyDataSetChanged()
        binding.rcvQuickTask.scrollToPosition(Constants.INDEX_0)
    }

    private fun navigateResult() {
        mainActivityVM.setNavigateFragment(Constants.INDEX_3)
        Bundle().apply {
            putString(Constants.KEY_TOTAL_TIME, "01:00")
            putString(Constants.KEY_COMPLETION, "100%")
            putString(
                Constants.KEY_TOTAL_RIGHT_ANSWER,
                "10/10"
            )
            openFragment(ResultFragment::class.java, this, true)
        }
    }

    private fun startAnimAnswer(
        fadeIn: Animation,
        fadeOut: Animation
    ) {
        fadeIn.onAnimationEnd { binding.imgStateAnswer.startAnimation(fadeOut) }
        fadeOut.onAnimationEnd { binding.imgStateAnswer.gone() }
        binding.imgStateAnswer.startAnimation(fadeIn)
    }


    private fun getShuffledAnswers(matchedString: String, answerWrong: String): List<String> {
        return listOf(matchedString, answerWrong).shuffled()
    }

    override fun initData() {
        listQuickTask = viewModel.getListQuickTaskFromJson(
            preferences.getInt(Constants.KEY_LESSON_ID),
            Constants.INDEX_1
        ) as ArrayList<QuickTask>
        if (listQuickTask.size != 0) {
            listItem.add(listQuickTask.removeAt(Constants.INDEX_0))
            doQuickTaskAdapter.submitList(listItem)
        } else {
            navigateResult()
        }
    }

    override fun initAction() {
        //do nothing
    }

}