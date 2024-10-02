package com.adzenglish.adzenglish.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import com.adzenglish.adzenglish.R
import kotlin.math.roundToInt

class BackgroundDrawableSpan(private val context: Context, private val backgroundColor: Int,private val textColor: Int) : ReplacementSpan() {
    private val paddingX = 10
    private val paddingY = 8

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {

        val rect = RectF(x - paddingX, top.toFloat() - paddingY, x + measureText(paint, text, start, end) + paddingX, bottom.toFloat() + paddingY)
        paint.color = backgroundColor
        canvas.drawRoundRect(
            rect,
            context.resources.getDimensionPixelSize(R.dimen._8dp).toFloat(),
            context.resources.getDimensionPixelSize(R.dimen._8dp).toFloat(),
            paint
        )
        paint.color = textColor
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }


    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return measureText(paint, text, start, end).roundToInt()
    }

    private fun measureText(paint: Paint, text: CharSequence, start: Int, end: Int): Float {
        return paint.measureText(text, start, end)
    }
}