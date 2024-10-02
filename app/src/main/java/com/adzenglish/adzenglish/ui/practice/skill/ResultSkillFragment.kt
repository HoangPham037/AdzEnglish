package com.adzenglish.adzenglish.ui.practice.skill

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.base.media.MediaManager
import com.adzenglish.adzenglish.databinding.FragmentResultSkillBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.formatToString
import com.adzenglish.adzenglish.viewmodel.PracticeVM
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ResultSkillFragment : BaseFragmentWithBinding<FragmentResultSkillBinding>() {
    private var adapter: ResultWordAdapter? = null
    private val viewModel: PracticeVM by viewModels()

    @Inject
    lateinit var preferences: Preferences
    override fun init() {
        val isPlay = preferences.getBoolean(Constants.KEY_OPEN_SOUND)
        if (isPlay) MediaManager.getInstance()
            ?.playAssetSound(requireContext(), Constants.KEY_SOUND_WIN)
    }

    override fun initData() {
        arguments?.let { bundle ->
            binding.tvTotalTime.text = bundle.getString(Constants.KEY_TOTAL_TIME)
            bundle.getString(Constants.KET_LIST_DICT)?.let { jsonString ->
                val dictEntities =
                    Gson().fromJson(jsonString, Array<DictEntity>::class.java).toList()
                val totalRightAnswer = dictEntities.count { it.isAnswer }
                val totalQuest = dictEntities.size
                binding.tvCompletionRate.text =
                    ((totalRightAnswer / totalQuest.toFloat()) * 100).formatToString()
                binding.tvTotalRightAnswer.text = String.format("$totalRightAnswer/$totalQuest")
                binding.tvTitle.text = String.format("${dictEntities.size} từ đã học")
                adapter = ResultWordAdapter(dictEntities)
                binding.rcvLearnedWord.adapter = adapter
                dictEntities.forEach { item ->
                    viewModel.updateAtDict(item)
                }
            }
        }
        if (isNetworkAvailable()) {
            val deviceId = viewModel.repository.getDeviceId()
            viewLifecycleOwner.lifecycleScope.launch {
                delay(200)
                viewModel.updateGold(Constants.INDEX_15, deviceId)
            }
            setupTextGold(Constants.INDEX_15.toString())
        } else {
            setupTextGold("?")
        }
    }

    private fun setupTextGold(gold: String) {
        binding.tvTotalGold.text = gold
    }

    override fun initAction() {
        binding.btnContinue.setOnClickListener {
            onBackPressed()
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentResultSkillBinding {
        return FragmentResultSkillBinding.inflate(layoutInflater)
    }
}