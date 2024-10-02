package com.adzenglish.adzenglish.ui.intro

import android.view.LayoutInflater
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentIntroThirteenBinding
import com.adzenglish.adzenglish.ui.mainfragment.MainFragment
import com.adzenglish.adzenglish.utils.extension.click
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroThirteenFragment : BaseFragmentWithBinding<FragmentIntroThirteenBinding>() {
    override fun getViewBinding(inflater: LayoutInflater): FragmentIntroThirteenBinding {
        return FragmentIntroThirteenBinding.inflate(layoutInflater)
    }

    override fun init() {
// do nothing
    }

    override fun initData() {
// do nothing
    }

    override fun initAction() {
        binding.btnStart.click {
            openFragment(MainFragment::class.java, null, false)
        }
    }
}