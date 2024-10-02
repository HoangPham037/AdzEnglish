package com.adzenglish.adzenglish.ui.learn.grammar

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentIntroGrammarBinding
import com.adzenglish.adzenglish.utils.Utils

class IntroGrammarFragment : BaseFragmentWithBinding<FragmentIntroGrammarBinding>() {
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    override fun getViewBinding(inflater: LayoutInflater): FragmentIntroGrammarBinding {
        return FragmentIntroGrammarBinding.inflate(layoutInflater)
    }

    override fun init() {
        Utils.startAnimation(binding.imgStarCenter)
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            openFragment(DoGrammarFragment::class.java, null, true)
        }
        handler?.postDelayed(runnable!!, 2800)
    }

    override fun initData() {
        //do nothing
    }

    override fun initAction() {
        //do nothing
    }
    override fun onStop() {
        super.onStop()
        runnable?.let {runnable-> handler?.removeCallbacks(runnable) }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (runnable != null && handler != null) {
            runnable = null
            handler = null
        }
    }
}