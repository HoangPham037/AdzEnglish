package com.adzenglish.adzenglish.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.text.style.ReplacementSpan
import com.adzenglish.adzenglish.R

class CustomDottedUnderlineSpan(
    context: Context,
    color: Int,
    private val spannedText: String
) : ReplacementSpan() {
    private val p: Paint = Paint()
    private var mWidth: Int = 0
    private var mSpanLength: Float = 0f
    private var mLengthIsCached = false

    private val mStrokeWidth: Float = context.resources.getDimension(R.dimen._1dp)
    private val mDashPathEffect: Float = context.resources.getDimension(R.dimen._2dp)
    private val mOffsetY: Float = context.resources.getDimension(R.dimen._3dp)

    init {
        p.color = color
        p.style = Paint.Style.STROKE
        p.pathEffect = DashPathEffect(floatArrayOf(mDashPathEffect, mDashPathEffect), 0f)
        p.strokeWidth = mStrokeWidth
    }

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        mWidth = paint.measureText(text, start, end).toInt()
        return mWidth
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
        if (!mLengthIsCached) {
            mSpanLength = paint.measureText(spannedText)
            mLengthIsCached = true
        }

        val path = Path()
        path.moveTo(x, y + mOffsetY)
        path.lineTo(x + mSpanLength, y + mOffsetY)
        canvas.drawPath(path, this.p)
    }
}
