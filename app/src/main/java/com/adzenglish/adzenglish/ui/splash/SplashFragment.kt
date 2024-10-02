package com.adzenglish.adzenglish.ui.splash

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentSplashBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.intro.IntroOneFragment
import com.adzenglish.adzenglish.ui.mainfragment.MainFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.Constants.INDEX_3
import com.adzenglish.adzenglish.viewmodel.SplashVM
import com.adzenglish.adzenglish.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragmentWithBinding<FragmentSplashBinding>() {
    private val viewModel: ViewModel by viewModels()

    @Inject
    lateinit var preferences: Preferences
    private val splashViewModel: SplashVM by viewModels()
    override fun getViewBinding(inflater: LayoutInflater): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(layoutInflater)
    }

    override fun init() {
        val boolean = preferences.getString(Constants.KEY_FIRST_TIME) == null
        if (boolean) {
            preferences.setInt(Constants.KEY_INVESTIGATION, INDEX_3)
            preferences.setInt(Constants.KEY_HOUR, Constants.INDEX_19)
            preferences.setInt(Constants.KEY_MINUTE, Constants.INDEX_0)
            preferences.setInt(Constants.KEY_LESSON_ID, Constants.LESSON_ID_DEFAULT)
            preferences.setBoolean(Constants.KEY_OPEN_SOUND, true)
            viewModel.initData {
                openFragment(IntroOneFragment::class.java, null, false)
            }
        } else {
            splashViewModel.navigateScreen.observe(viewLifecycleOwner) {
                openFragment(MainFragment::class.java, null, true)
            }
            splashViewModel.startDelay()
        }
    }

    override fun initData() {
        // do nothing
    }

    override fun initAction() {
        // do nothing
    }
}