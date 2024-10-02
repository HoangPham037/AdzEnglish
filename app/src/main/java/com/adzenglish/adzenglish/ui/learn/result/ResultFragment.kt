package com.adzenglish.adzenglish.ui.learn.result

import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.base.media.MediaManager
import com.adzenglish.adzenglish.databinding.FragmentResultBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.learn.grammar.IntroGrammarFragment
import com.adzenglish.adzenglish.ui.learn.practice.IntroPracticeLessonFragment
import com.adzenglish.adzenglish.ui.topic.box.BoxNineFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.viewmodel.MainActivityVM
import com.adzenglish.adzenglish.viewmodel.TopicViewModel
import com.adzenglish.adzenglish.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ResultFragment : BaseFragmentWithBinding<FragmentResultBinding>() {
    private val viewModel: TopicViewModel by viewModels()
    private val viewModels: ViewModel by viewModels()
    private val mainActivityVM: MainActivityVM by activityViewModels()
    private var typeFrom: String? = null

    @Inject
    lateinit var preferences: Preferences
    override fun getViewBinding(inflater: LayoutInflater): FragmentResultBinding {
        return FragmentResultBinding.inflate(layoutInflater)
    }

    override fun init() {
        val isPlay = preferences.getBoolean(Constants.KEY_OPEN_SOUND)
        if (isPlay) viewModel.viewModelScope.launch(Dispatchers.IO) {
            try {
                MediaManager.getInstance()?.playAssetSound(requireContext(), Constants.KEY_SOUND_WIN)
            }catch (e:IllegalStateException) {
                println("error IllegalStateException: $e")
            }
        }
    }

    override fun initData() {
        val lessonId = preferences.getInt(Constants.KEY_LESSON_ID)
        arguments?.let { bundle ->
            binding.tvCompletionRate.text = bundle.getString(Constants.KEY_COMPLETION)
            binding.tvTotalTime.text = bundle.getString(Constants.KEY_TOTAL_TIME)
            binding.tvTotalRightAnswer.text = bundle.getString(Constants.KEY_TOTAL_RIGHT_ANSWER)
            typeFrom = bundle.getString(Constants.KEY_TYPE_FROM)
            when (typeFrom) {
                Constants.KEY_FROM_DO_VOCABULARY -> {
                    viewModels.updateIsFinishPartVocabulary(lessonId, true)
                    preferences.setInt(Constants.KEY_PART_LEARNING, Constants.KEY_PART_DO_GRAMMAR)
                }

                Constants.KEY_FROM_DO_GRAMMAR -> {
                    viewModels.updateIsFinishPartGrammar(lessonId, true)
                    preferences.setInt(Constants.KEY_PART_LEARNING, Constants.KEY_PART_DO_PRACTICE)
                }
            }
        }

        if (isNetworkAvailable()) {
            val deviceId = viewModel.repository.getDeviceId()
            setupTextGold(Constants.INDEX_15.toString())
            viewLifecycleOwner.lifecycleScope.launch {
                delay(200)
                viewModels.updateGold(Constants.INDEX_15, deviceId)
            }
        } else {
            setupTextGold("?")
        }
    }

    override fun initAction() {
        binding.imgClose.click {
            onBackPressed()
        }
        binding.tvAction.click {
            val currentDate = viewModel.getDate()
            val dayAttendance = preferences.getString(Constants.KEY_ATTENDANCE)
            if (dayAttendance == null || dayAttendance != currentDate) {
                openFragment(BoxNineFragment::class.java, null, true)
                preferences.setString(Constants.KEY_ATTENDANCE, currentDate)
            } else {
                navigateFragment()
            }
        }
    }

    private fun navigateFragment() {
        mainActivityVM.navigateFragment.value.let { tag ->
            when (tag) {
                Constants.INDEX_1 -> openFragment(IntroGrammarFragment::class.java, null, true)
                Constants.INDEX_2 -> openFragment(
                    IntroPracticeLessonFragment::class.java,
                    null,
                    true
                )

                Constants.INDEX_3, Constants.INDEX_4 -> onBackPressed()
            }
        }
    }


    private fun setupTextGold(gold: String) {
        binding.tvGold.text = gold
    }
}