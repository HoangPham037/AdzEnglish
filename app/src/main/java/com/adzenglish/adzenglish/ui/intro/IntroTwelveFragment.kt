package com.adzenglish.adzenglish.ui.intro

import android.view.LayoutInflater
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentIntroTwelveBinding
import com.adzenglish.adzenglish.ui.mainfragment.MainFragment
import com.adzenglish.adzenglish.utils.extension.click
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroTwelveFragment : BaseFragmentWithBinding<FragmentIntroTwelveBinding>() {
    override fun getViewBinding(inflater: LayoutInflater): FragmentIntroTwelveBinding {
        return FragmentIntroTwelveBinding.inflate(layoutInflater)
    }

    override fun init() {
            // do nothing
    }

    override fun initData() {
             // do nothing
    }

    override fun initAction() {
        binding.tvContinue.click {
            openFragment(MainFragment::class.java, null, true)
        }
    }
}