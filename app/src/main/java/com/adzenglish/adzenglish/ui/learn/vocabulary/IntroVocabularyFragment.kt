package com.adzenglish.adzenglish.ui.learn.vocabulary

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentWordZeroBinding
import com.adzenglish.adzenglish.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroVocabularyFragment : BaseFragmentWithBinding<FragmentWordZeroBinding>() {
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    override fun init() {
        Utils.startAnimation(binding.imgStarLeft)
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            openFragment(DoExerciseVocabularyFragment::class.java, null, true)
        }
        handler?.postDelayed(runnable!!, 2800)
    }

    override fun initData() {
        //do nothing
    }

    override fun initAction() {
        //do nothing
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentWordZeroBinding {
        return FragmentWordZeroBinding.inflate(layoutInflater)
    }

    override fun onStop() {
        super.onStop()
        runnable?.let { runnable -> handler?.removeCallbacks(runnable) }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (runnable != null && handler != null) {
            runnable = null
            handler = null
        }

    }
}

