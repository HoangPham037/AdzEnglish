package com.adzenglish.adzenglish.ui.intro

import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentIntroMainBinding
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.viewmodel.IntroMainVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroMainFragment : BaseFragmentWithBinding<FragmentIntroMainBinding>() {
    private val introMainVM: IntroMainVM by activityViewModels()
    override fun getViewBinding(inflater: LayoutInflater): FragmentIntroMainBinding {
        return FragmentIntroMainBinding.inflate(layoutInflater)
    }

    override fun init() {
        showFragment(IntroTwoFragment::class.java, null, false)
    }

    override fun initData() {
        introMainVM.progressSetupData.observe(viewLifecycleOwner) {
            binding.progress.progress = it
        }
    }

    override fun initAction() {
        introMainVM.updateProgress(Constants.INDEX_12)
    }
}