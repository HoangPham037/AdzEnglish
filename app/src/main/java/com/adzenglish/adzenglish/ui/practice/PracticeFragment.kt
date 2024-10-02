package com.adzenglish.adzenglish.ui.practice

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentPracticeBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.room.entity.RuleEntity
import com.adzenglish.adzenglish.ui.practice.grammar.PracticeGrammarFragment
import com.adzenglish.adzenglish.ui.practice.skill.SkillFragment
import com.adzenglish.adzenglish.ui.practice.word.PracticeWordFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import com.adzenglish.adzenglish.viewmodel.PracticeVM
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PracticeFragment : BaseFragmentWithBinding<FragmentPracticeBinding>() {
    private val viewModel: PracticeVM by viewModels()
    override fun init() {
        viewModel.listWord.observe(viewLifecycleOwner) { dataStatusResult ->
            when (dataStatusResult.status) {
                DataStatus.Status.ERROR -> toast(dataStatusResult.message.toString())
                DataStatus.Status.SUCCESS -> dataStatusResult.data?.let { updateUi(it) }
            }
        }
        viewModel.listRule.observe(viewLifecycleOwner) { dataStatusResult ->
            when (dataStatusResult.status) {
                DataStatus.Status.ERROR -> toast(dataStatusResult.message.toString())
                DataStatus.Status.SUCCESS -> dataStatusResult.data?.let { updateUiRule(it) }
            }
        }
        setupTime()
    }

    private fun updateUiRule(listRule: List<RuleEntity>) {
        val date = viewModel.getDate()
        binding.tvTotalWordRule.text = listRule.size.toString()
        binding.tvTotalNewRules.text = listRule.count { it.createdAt == date }.toString()
        binding.tvTotalPracticeRule.text = listRule.count { it.updatedAt == date }.toString()
    }

    private fun setupTime() {
        binding.tvTime.text = String.format("${viewModel.getTime()} tiếng")
    }

    private fun updateUi(list: List<DictEntity>) {
        val date = viewModel.getDate()
        val tvContentReviewWord = viewModel.getWord(list)
        binding.tvTotalWord.text = String.format("${list.size}")
        binding.tvTotalWordNew.text = String.format("${list.count { it.createdAt == date }}")
        binding.tvTotalWordReview.text = String.format("${list.count { it.updatedAt == date }}")
        binding.tvContentReviewWord.text = tvContentReviewWord.ifEmpty {requireContext().getString(R.string.txt_unavailable) }
        binding.tvReviewWord.setTextColor(requireContext().getColor(if (tvContentReviewWord.isEmpty()) R.color.gray_02 else R.color.blue_02))
        binding.tvContentReviewWord.setTextColor(requireContext().getColor(if (tvContentReviewWord.isEmpty()) R.color.gray_02 else R.color.orange))
    }

    override fun initData() {
        viewModel.getListWord()
        viewModel.getListRule()
    }

    override fun initAction() {
        binding.word.setOnClickListener {
            showPracticeWord()
        }
        binding.reviewWord.setOnClickListener {
            shopSkillFragment()
        }
        binding.rule.setOnClickListener {
            showPracticeGrammar()
        }
    }

    private fun showPracticeGrammar() {
        viewModel.listRule.value?.data?.let { list ->
            if (list.isEmpty()) return
            Bundle().apply {
                putString(Constants.KEY_LIST_RULE, list.size.toString())
                openFragment(PracticeGrammarFragment::class.java, this, true)
            }
        }
    }

    private fun shopSkillFragment() {
        viewModel.listWord.value?.data?.let { list ->
            if (list.isEmpty()) return
            Bundle().apply {
                val jsonString = Gson().toJson(list)
                putString(Constants.KEY_LIST_DICT_SKILL, jsonString)
                openFragment(SkillFragment::class.java, this, true)
            }
        }
    }

    private fun showPracticeWord() {
        Bundle().apply {
            val jsonString = Gson().toJson(viewModel.listWord.value)
            putString(Constants.KEY_LIST_WORD, jsonString)
            openFragment(PracticeWordFragment::class.java, this, true)
        }
    }


    override fun getViewBinding(inflater: LayoutInflater): FragmentPracticeBinding {
        return FragmentPracticeBinding.inflate(layoutInflater)
    }
}