package com.adzenglish.adzenglish.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.rgb
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.adzenglish.adzenglish.R

class CustomCircleProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0

    private val mStartAngle = -90f
    private var mSweepAngle = 0f
    private val mMaxSweepAngle = 360f
    private var mStrokeWidth: Int
    private var mAnimationDuration: Int
    private var mMaxProgress: Int
    private var mDrawText = true
    private var mRoundedCorners = true
    private var mProgressColor: Int
    private var mTextColor = Color.parseColor("#20306C")

    private val mPaint: Paint

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomProgressBar,
            defStyleAttr, 0).apply {
            try {
                mStrokeWidth =getDimensionPixelSize(R.styleable.CustomProgressBar_mStrokeWidth,10)
                mAnimationDuration=getInteger(R.styleable.CustomProgressBar_mAnimationDuration,100)
                mMaxProgress = getInteger(R.styleable.CustomProgressBar_mMaxProgress,100)
                mProgressColor = getColor(R.styleable.CustomProgressBar_mProgressColor,rgb(250,0,0))
            } finally {
                recycle()
            }
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initMeasurements()
        drawOutlineArc(canvas)

        if (mDrawText) {
            drawText(canvas)
        }
    }

    private fun initMeasurements() {
        mViewWidth = width
        mViewHeight = height
    }

    private fun drawOutlineArc(canvas: Canvas) {
        val diameter1 = (Math.min(mViewWidth, mViewHeight) - mStrokeWidth * 2)+1
        val outerOval1 = RectF(mStrokeWidth.toFloat(), mStrokeWidth.toFloat(), diameter1.toFloat(), diameter1.toFloat())

        mPaint.color = rgb(238,238,238)
        mPaint.strokeWidth = mStrokeWidth.toFloat()
        mPaint.isAntiAlias = true

        mPaint.strokeCap = if (mRoundedCorners) Paint.Cap.ROUND else Paint.Cap.BUTT
        mPaint.style = Paint.Style.STROKE
        canvas.drawArc(outerOval1, -90f+mSweepAngle, 360f-mSweepAngle, false, mPaint)

        val diameter = Math.min(mViewWidth, mViewHeight) - mStrokeWidth * 2
        val outerOval = RectF(mStrokeWidth.toFloat(), mStrokeWidth.toFloat(), diameter.toFloat(), diameter.toFloat())

        mPaint.color = mProgressColor
        mPaint.strokeWidth = mStrokeWidth.toFloat()
        mPaint.isAntiAlias = true
        mPaint.strokeCap = if (mRoundedCorners) Paint.Cap.ROUND else Paint.Cap.BUTT
        mPaint.style = Paint.Style.STROKE
        canvas.drawArc(outerOval, mStartAngle, mSweepAngle, false, mPaint)
    }

    private fun drawText(canvas: Canvas) {
        mPaint.textSize = (Math.min(mViewWidth, mViewHeight) / 5f) - 30f
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.color = mTextColor
        mPaint.style = Paint.Style.FILL

        val xPos =   canvas.width / 2 - 30f
        val yPos =   (canvas.height / 2 - (mPaint.descent() + mPaint.ascent()) / 2).toInt() -25f

        canvas.drawText(
            String.format("%s", "${calcProgressFromSweepAngle(mSweepAngle)}%"),
            xPos,
            yPos,
            mPaint
        )
    }

    private fun calcSweepAngleFromProgress(progress: Float): Float {
        return mMaxSweepAngle / mMaxProgress * progress
    }

    private fun calcProgressFromSweepAngle(sweepAngle: Float): Int {
        return (sweepAngle * mMaxProgress / mMaxSweepAngle).toInt()
    }

    fun setProgress(progress: Float) {
        val animator = ValueAnimator.ofFloat(mSweepAngle, calcSweepAngleFromProgress(progress))
        animator.interpolator = DecelerateInterpolator()
        animator.duration = mAnimationDuration.toLong()
        animator.addUpdateListener { valueAnimator ->
            mSweepAngle = valueAnimator.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    fun setProgressColor(color: Int) {
        mProgressColor = color
        invalidate()
    }

    fun setProgressWidth(width: Int) {
        mStrokeWidth = width
        invalidate()
    }

    fun setTextColor(color: Int) {
        mTextColor = color
        invalidate()
    }

}