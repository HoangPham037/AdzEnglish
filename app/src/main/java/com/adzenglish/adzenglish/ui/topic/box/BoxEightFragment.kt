package com.adzenglish.adzenglish.ui.topic.box

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentBoxEightBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.viewmodel.TopicViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BoxEightFragment : BaseFragmentWithBinding<FragmentBoxEightBinding>() {
    @Inject
    lateinit var preferences: Preferences
    private val viewModel: TopicViewModel by viewModels()
    override fun init() {
        // do nothing
    }

    override fun initData() {
        arguments?.let { bundle ->
            val jsonString = bundle.getString(Constants.KET_LIST_DICT)
            jsonString?.let {
                val gson = Gson()
                val dictEntities = gson.fromJson(jsonString, Array<DictEntity>::class.java).toList()
                viewModel.updateDictToRoomByAddWord(dictEntities)
                binding.tvTitleContent.text =
                    String.format("Bạn đã thêm ${viewModel.getTotalWord(dictEntities)} từ mới vào sổ tay")
            }
        }
    }

    override fun initAction() {
        binding.btnContinue.setOnClickListener {
            val currentDate = viewModel.getDate()
            val dayAttendance = preferences.getString(Constants.KEY_ATTENDANCE)
            if (dayAttendance == null || dayAttendance != currentDate) {
                Bundle().apply {
                    putString(Constants.KEY_OPEN_SOUND, Constants.KEY_OPEN_SOUND)
                    openFragment(BoxNineFragment::class.java, this, true)
                }
                preferences.setString(Constants.KEY_ATTENDANCE, currentDate)
            } else {
                onBackPressed()
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentBoxEightBinding {
        return FragmentBoxEightBinding.inflate(layoutInflater)
    }
}