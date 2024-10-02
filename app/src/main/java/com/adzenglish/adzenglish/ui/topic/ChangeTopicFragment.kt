package com.adzenglish.adzenglish.ui.topic

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentChangeTopicBinding
import com.adzenglish.adzenglish.models.local.room.entity.ThemeEntity
import com.adzenglish.adzenglish.utils.extension.goneIf
import com.adzenglish.adzenglish.viewmodel.TopicViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeTopicFragment : BaseFragmentWithBinding<FragmentChangeTopicBinding>() {
    private var isSelected = false
    private var adapter: TopicAdapter? = null
    private val viewModel: TopicViewModel by viewModels()
    private var listTmp: List<ThemeEntity> = arrayListOf()
    override fun init() {
        adapter = TopicAdapter(viewModel) { theme ->
            updateUi(theme)
        }
        binding.rcvTopic.adapter = adapter
    }

    private fun updateUi(theme: ThemeEntity?) {
        if (theme == null) toast("Bạn cần chọn tối thiểu một chủ đề !") else viewModel.updateTheme(
            theme
        )
    }

    private fun setUpTextTopic(list: List<ThemeEntity>) {
        binding.tvSelectedTopic.text =
            String.format("Bạn đã chọn ${viewModel.getSelectTopic(list)} chủ đề")
    }

    override fun initData() {
        viewModel.getListTheme { list ->
            listTmp = list
            setUpTextTopic(list)
            adapter?.submitList(list)
        }
    }

    override fun initAction() {
        binding.btnContinue.setOnClickListener { onBackPressed() }
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.tvSelectedTopic.setOnClickListener { showTopicSelect() }
    }

    private fun showTopicSelect() {
        isSelected = !isSelected
        adapter?.submitList(if (isSelected) viewModel.getListSelect(listTmp) else listTmp)
        binding.ivClose.goneIf(!isSelected)
        binding.tbSelect.setBackgroundResource(if (isSelected) R.drawable.bg_topic_true else R.drawable.bg_topic)
        binding.tvSelectedTopic.setTextColor(requireContext().getColor(if (isSelected) R.color.white else R.color.green_01))
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentChangeTopicBinding {
        return FragmentChangeTopicBinding.inflate(layoutInflater)
    }
}