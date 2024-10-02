package com.adzenglish.adzenglish.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class NoTouchRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return false
    }
}
