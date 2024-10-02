package com.adzenglish.adzenglish.ui.intro


import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentIntroNineBinding
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.viewmodel.IntroMainVM

class IntroNineFragment : BaseFragmentWithBinding<FragmentIntroNineBinding>() {
    private val introViewModel: IntroMainVM by activityViewModels()

    override fun getViewBinding(inflater: LayoutInflater): FragmentIntroNineBinding {
        return FragmentIntroNineBinding.inflate(layoutInflater)
    }

    override fun init() {
        binding.customCircleProgressBar.apply {
            setProgressWidth(Constants.INDEX_60)
            setProgressColor(ContextCompat.getColor(requireContext(), R.color.yellow))
        }
    }

    override fun initData() {
        introViewModel.progress.observe(viewLifecycleOwner) {
            binding.customCircleProgressBar.setProgress(it.toFloat())
            if (it == Constants.INDEX_100) {
                Handler(Looper.getMainLooper()).postDelayed({
                    openFragment(IntroTwelveFragment::class.java, null, false)
                }, 500)
            }
        }
        introViewModel.startProgressAnimation()
    }

    override fun initAction() {
        // do nothing
    }
}