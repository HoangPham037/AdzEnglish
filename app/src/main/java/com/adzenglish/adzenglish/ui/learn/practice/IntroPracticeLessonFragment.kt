package com.adzenglish.adzenglish.ui.learn.practice

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentIntroPracticeLessonBinding
import com.adzenglish.adzenglish.utils.Utils

class IntroPracticeLessonFragment : BaseFragmentWithBinding<FragmentIntroPracticeLessonBinding>() {
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    override fun getViewBinding(inflater: LayoutInflater): FragmentIntroPracticeLessonBinding {
        return FragmentIntroPracticeLessonBinding.inflate(layoutInflater)
    }

    override fun init() {
        Utils.startAnimation(binding.imgStarRight)
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            openFragment(DoPracticesFragment::class.java, null, true)
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