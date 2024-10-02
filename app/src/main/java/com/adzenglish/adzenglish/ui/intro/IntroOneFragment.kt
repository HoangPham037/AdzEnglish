package com.adzenglish.adzenglish.ui.intro

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentIntroOneBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class IntroOneFragment : BaseFragmentWithBinding<FragmentIntroOneBinding>() {
    private val viewMode: ViewModel by viewModels()

    @Inject
    lateinit var preferences: Preferences
    override fun getViewBinding(inflater: LayoutInflater): FragmentIntroOneBinding {
        return FragmentIntroOneBinding.inflate(layoutInflater)
    }

    override fun init() {
        preferences.setInt(Constants.KEY_ID_LEVEL, Constants.INDEX_1)
        preferences.setString(Constants.KEY_FIRST_TIME, Constants.KEY_FIRST_TIME)
    }

    override fun initData() {
        viewMode.updateLevelIsSelectedById(Constants.INDEX_1, Constants.INDEX_1)
    }

    override fun initAction() {
        binding.tvLestGo.click {
            viewMode.updateLevelIsSelectedById(Constants.INDEX_1, Constants.INDEX_1)
            openFragment(IntroMainFragment::class.java, null, false)
        }
    }
}