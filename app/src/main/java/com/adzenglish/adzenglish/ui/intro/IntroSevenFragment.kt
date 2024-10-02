package com.adzenglish.adzenglish.ui.intro

import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentIntroSevenBinding
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.viewmodel.IntroMainVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroSevenFragment : BaseFragmentWithBinding<FragmentIntroSevenBinding>() {
    private val introMainVM: IntroMainVM by activityViewModels()

    override fun getViewBinding(inflater: LayoutInflater): FragmentIntroSevenBinding {
        return FragmentIntroSevenBinding.inflate(layoutInflater)
    }

    override fun init() {
        // do nothing
    }

    override fun initData() {
        // do nothing
    }

    override fun initAction() {
        binding.btnContinue.click {
            showFragment(IntroEightFragment::class.java, null, false)
            introMainVM.updateProgress(
                introMainVM.progressSetupData.value?.plus(Constants.INDEX_12) ?: Constants.INDEX_12
            )
        }
    }
}