package com.adzenglish.adzenglish.ui.intro

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentIntroEightBinding
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.viewmodel.IntroMainVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroEightFragment : BaseFragmentWithBinding<FragmentIntroEightBinding>() {
    private val introMainVM: IntroMainVM by activityViewModels()
    private var index = Constants.INDEX_0


    override fun init() {
        binding.lnOne.setOnClickListener { updateUI(it) }
        binding.lnTwo.setOnClickListener { updateUI(it) }
        binding.lnFour.setOnClickListener { updateUI(it) }
        binding.lnThree.setOnClickListener { updateUI(it) }
    }

    private fun updateUI(view: View) {
        index++
        binding.tvContinue.setTextColor(Color.WHITE)
        binding.lnOne.setBackgroundColor(Color.WHITE)
        binding.lnTwo.setBackgroundColor(Color.WHITE)
        binding.lnFour.setBackgroundColor(Color.WHITE)
        binding.lnThree.setBackgroundColor(Color.WHITE)
        binding.tvContinue.setBackgroundResource(R.drawable.bg_text_view)
        view.setBackgroundColor(requireContext().getColor(R.color.green_02))
    }

    override fun initData() {
        introMainVM.updateProgress(
            introMainVM.progressSetupData.value?.plus(Constants.INDEX_12) ?: Constants.INDEX_12
        )
    }

    override fun initAction() {
        binding.tvContinue.click {
            if (index > Constants.INDEX_0) openFragment(IntroNineFragment::class.java, null, false)
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentIntroEightBinding {
        return FragmentIntroEightBinding.inflate(layoutInflater)
    }
}