package com.adzenglish.adzenglish.ui.intro

import android.graphics.Color
import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentIntroTwoBinding
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import com.adzenglish.adzenglish.utils.Utils
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.visible
import com.adzenglish.adzenglish.viewmodel.IntroMainVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroTwoFragment : BaseFragmentWithBinding<FragmentIntroTwoBinding>() {
    private val introMainVM: IntroMainVM by activityViewModels()
    override fun getViewBinding(inflater: LayoutInflater): FragmentIntroTwoBinding {
        return FragmentIntroTwoBinding.inflate(layoutInflater)
    }

    override fun init() {
        introMainVM.setIndex()
        val adapter = IntroAdapter {
            updateUi(it.size > Constants.INDEX_0)
            when (introMainVM.index) {
                Constants.INDEX_1 -> showFragment()
                Constants.INDEX_4 -> showFragment()
            }
        }
        val adapterTheme = IntroThemeAdapter {
            updateUi(introMainVM.getTotalTheme(it) > Constants.INDEX_0)
        }
        when (introMainVM.index) {
            Constants.INDEX_1 -> showData(Utils.listQuestionOne, adapter)
            Constants.INDEX_2 -> {
                binding.tvContinue.visible()
                showData(Utils.listQuestionTwo, adapter)
            }

            Constants.INDEX_3 -> initDataTheme(adapterTheme)
            Constants.INDEX_4 -> showData(Utils.listQuestionFour, adapter)
            Constants.INDEX_5 -> {
                binding.tvContinue.visible()
                binding.description.visible()
                showData(Utils.listQuestionFive, adapter)
            }
        }
        binding.tvContinue.click {
            when (introMainVM.index) {
                Constants.INDEX_3 -> addThemeToRoom(adapterTheme)
                else -> nextFragment(adapter.listItemSelect.size)
            }
        }
    }

    private fun initDataTheme(adapterTheme: IntroThemeAdapter) {
        binding.tvContinue.visible()
        updateUi(true)
        introMainVM.listTheme.observe(viewLifecycleOwner) { dataStatusResult ->
            when (dataStatusResult.status) {
                DataStatus.Status.ERROR -> toast(dataStatusResult.message.toString())
                DataStatus.Status.SUCCESS -> adapterTheme.submitList(dataStatusResult.data?.sortedBy { it.position })
            }
            binding.rcvIntro.adapter = adapterTheme
        }
    }

    private fun addThemeToRoom(adapterTheme: IntroThemeAdapter) {
        introMainVM.upDateThemeToRoom(adapterTheme.listItem)
        nextFragment(introMainVM.getTotalTheme(adapterTheme.listItem))
    }

    private fun nextFragment(size: Int) {
        if (size > Constants.INDEX_0) showFragment()
    }

    private fun updateUi(boolean: Boolean) {
        binding.tvContinue.setTextColor(if (boolean) Color.WHITE else requireContext().getColor(R.color.gray_02))
        binding.tvContinue.setBackgroundResource(if (boolean) R.drawable.bg_text_view else R.drawable.bg_text_continue)
    }

    private fun showFragment() {
        upDateProgress()
        showFragment(
            if (introMainVM.index == Constants.INDEX_5) IntroSevenFragment::class.java else IntroTwoFragment::class.java,
            null,
            false
        )
    }

    private fun upDateProgress() {
        introMainVM.updateProgress(
            introMainVM.progressSetupData.value?.plus(Constants.INDEX_12) ?: Constants.INDEX_12
        )
    }

    private fun showData(listQuestion: ArrayList<String>, adapter: IntroAdapter) {
        val tvTitle = listQuestion[Constants.INDEX_0]
        binding.tvTitle.text = tvTitle
        listQuestion.remove(tvTitle)
        binding.rcvIntro.adapter = adapter
        adapter.submitList(listQuestion)
    }

    override fun initData() {
// do nothing
    }

    override fun initAction() {
// do nothing
    }
}