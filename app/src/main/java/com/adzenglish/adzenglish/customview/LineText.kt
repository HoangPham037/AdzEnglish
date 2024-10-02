package com.adzenglish.adzenglish.customview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.adzenglish.adzenglish.R

@SuppressLint("CustomViewStyleable", "Recycle")
class LineText(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textWith : Float
    private var text: String
    private var textReceived :String = ""
    private val indicatorHeight = (DP * 16).toInt()
    private val indicatorItemPadding = DP * 6
    private val indicatorStrokeWidth = DP * 4
    private val colorInactive = Color.parseColor("#E9E9E9")
    private val indicatorItemLength = DP * 16
    private val paint = Paint()
    private val mTextPaint = Paint()

    init {
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = indicatorStrokeWidth
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomLineText)
        textPaint.color = typedArray.getColor(R.styleable.CustomLineText_mTextColor, Color.BLACK)
        textPaint.textSize = typedArray.getDimension(R.styleable.CustomLineText_mTextSize, 16f)
        textWith = typedArray.getDimension(R.styleable.CustomLineText_mTextWidth, 100f)
        text = typedArray.getString(R.styleable.CustomLineText_mText).toString()
        mTextPaint.apply {
            isAntiAlias = true
            textSize = DP * 20
            color = Color.BLACK
            typeface = ResourcesCompat.getFont(context, R.font.plusjakartasans_bold)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val textResult = text.replace(" ","")
        val itemCount = textResult.length
        val totalLength = indicatorItemLength * itemCount
        val paddingBetweenItems = Math.max(0, itemCount - 1) * indicatorItemPadding
        val indicatorTotalWidth = totalLength + paddingBetweenItems
        val indicatorStartX = (width - indicatorTotalWidth) / 2f
        val indicatorPosY = height - indicatorHeight / 2f
        drawText(canvas, indicatorStartX, textReceived)
        drawDisableLine(canvas, indicatorStartX, indicatorPosY, itemCount)
    }

    private fun drawDisableLine(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        itemCount: Int
    ) {
        paint.color = colorInactive
        val itemWidth = indicatorItemLength + indicatorItemPadding
        var start = indicatorStartX
        for (i in 0 until itemCount) {
            c.drawLine(start, indicatorPosY + 10f, start + indicatorItemLength, indicatorPosY + 10f, paint)
            start += itemWidth
        }
    }

    private fun drawText(
        c: Canvas,
        indicatorStartX: Float,
        text: String
    ) {
        paint.color = Color.parseColor("#818181")
        val itemWidth = indicatorItemLength + indicatorItemPadding
        var start = indicatorStartX + 7f
        text.forEach {
            c.drawText(
                it.toString(),
                start,
                height / 2f + getTextHeight(text, mTextPaint) / 2f,
                mTextPaint
            )
            start += itemWidth
        }
    }
    private fun getTextHeight(text: String, paint: Paint): Float {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height().toFloat()
    }
    fun setText(text: String) {
        this.text = text
    }

    fun setTextReceived(text:String){
        this.textReceived = text
        invalidate()
    }
    companion object {
        private val DP = Resources.getSystem().displayMetrics.density
    }
}