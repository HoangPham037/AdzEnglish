package com.adzenglish.adzenglish.ui.practice.word

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.adzenglish.adzenglish.ui.practice.transplant.WordAdapter

class RecyclerViewItemTouchHelper(
    dragDirs: Int,
    swipeDirs: Int,
    val listener: ItemTouchHelpListener
) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwiped(viewHolder)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val foreGroundView = (viewHolder as WordAdapter.WordViewHolder).layoutForeground
        getDefaultUIUtil().onDraw(
            c,
            recyclerView,
            foreGroundView,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        val foreGroundView = (viewHolder as WordAdapter.WordViewHolder).layoutForeground
        getDefaultUIUtil().clearView(foreGroundView)
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        viewHolder?.let {
            val foreGroundView =
                (viewHolder as WordAdapter.WordViewHolder).layoutForeground
            getDefaultUIUtil().onDrawOver(
                c,
                recyclerView,
                foreGroundView,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int
    ) {
        viewHolder?.let {
            val foreGroundView =
                (viewHolder as WordAdapter.WordViewHolder).layoutForeground
            getDefaultUIUtil().onSelected(foreGroundView)
        }
    }
}