package com.adzenglish.adzenglish.ui.learn.quicktask

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.IntroQuickTaskFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroQuickTaskFragment : BaseFragmentWithBinding<IntroQuickTaskFragmentBinding>() {
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    override fun init() {
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            openFragment(QuickTaskFragment::class.java, null, true)
        }
        handler?.postDelayed(runnable!!, 2800)
    }

    override fun initData() {
        //do nothing
    }

    override fun initAction() {
        //do nothing
    }

    override fun getViewBinding(inflater: LayoutInflater): IntroQuickTaskFragmentBinding {
        return IntroQuickTaskFragmentBinding.inflate(layoutInflater)
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

