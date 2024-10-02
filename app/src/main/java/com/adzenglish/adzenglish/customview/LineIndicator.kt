package com.adzenglish.adzenglish.customview

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class LineIndicator : ItemDecoration() {
    private val colorActive = Color.parseColor("#B7D678")
    private val colorInactive = Color.parseColor("#E9E9E9")
    private val indicatorHeight = (DP * 16).toInt()
    private val indicatorStrokeWidth = DP * 4
    private val indicatorItemLength = DP * 16
    private val indicatorItemPadding = DP * 6
    private val interpolator: Interpolator = AccelerateDecelerateInterpolator()
    private val paint = Paint()

    init {
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = indicatorStrokeWidth
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val itemCount = parent.adapter!!.itemCount
        val totalLength = indicatorItemLength * itemCount
        val paddingBetweenItems = Math.max(0, itemCount - 1) * indicatorItemPadding
        val indicatorTotalWidth = totalLength + paddingBetweenItems
        val indicatorStartX = (parent.width - indicatorTotalWidth) / 2f
        val indicatorPosY = parent.height - indicatorHeight / 2f

        drawDisableLine(c, indicatorStartX, indicatorPosY, itemCount)

        val layoutManager = parent.layoutManager as LinearLayoutManager?
        val activePosition = layoutManager!!.findFirstCompletelyVisibleItemPosition()
        if (activePosition == RecyclerView.NO_POSITION) {
            return
        }

        val activeChild = layoutManager.findViewByPosition(activePosition)
        val left = activeChild!!.left
        val width = activeChild.width

        val progress = interpolator.getInterpolation(left * -1 / width.toFloat())
        drawHighlightLine(c, indicatorStartX, indicatorPosY, activePosition, progress, itemCount)
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
            c.drawLine(start, indicatorPosY, start + indicatorItemLength, indicatorPosY, paint)
            start += itemWidth
        }
    }

    private fun drawHighlightLine(
        c: Canvas, indicatorStartX: Float, indicatorPosY: Float,
        highlightPosition: Int, progress: Float, itemCount: Int
    ) {
        paint.color = colorActive

        val itemWidth = indicatorItemLength + indicatorItemPadding
        if (progress.toInt() == 0) {
            val highlightStart = indicatorStartX + itemWidth * highlightPosition
            c.drawLine(
                highlightStart, indicatorPosY,
                highlightStart + indicatorItemLength, indicatorPosY, paint
            )
        } else {
            var highlightStart = indicatorStartX + itemWidth * highlightPosition
            val partialLength = indicatorItemLength * progress
            c.drawLine(
                highlightStart + itemWidth , indicatorPosY,
                highlightStart + partialLength, indicatorPosY, paint
            )
            if (highlightPosition < itemCount - 1) {
                highlightStart += itemWidth
                c.drawLine(
                    highlightStart, indicatorPosY,
                    highlightStart + partialLength, indicatorPosY, paint
                )
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = indicatorHeight
    }

    companion object {
        private val DP = Resources.getSystem().displayMetrics.density
    }
}
