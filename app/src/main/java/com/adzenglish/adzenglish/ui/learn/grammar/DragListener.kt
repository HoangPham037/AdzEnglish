package com.adzenglish.adzenglish.ui.learn.grammar

import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.adzenglish.adzenglish.R
import adapter.TextAdapter
import android.annotation.SuppressLint

class DragListener internal constructor(private val listener: Listener) :
    View.OnDragListener {
    private var isDropped = false
    @SuppressLint("NotifyDataSetChanged")
    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DROP -> {
                isDropped = true
                var positionTarget: Int
                val viewSource = event.localState as View?
                val viewId = v.id
                val frameLayoutItem = R.id.frame_layout_item
                val recyclerView1 = R.id.recycler_view_1
                val recyclerView2 = R.id.recycler_view_2
                when (viewId) {
                    frameLayoutItem,recyclerView1, recyclerView2 -> {
                        val target: RecyclerView
                        when (viewId) {
                            recyclerView1 -> target =
                                v.rootView.findViewById<View>(recyclerView1) as RecyclerView

                            recyclerView2 -> target =
                                v.rootView.findViewById<View>(recyclerView2) as RecyclerView

                            else -> {
                                target = v.parent as RecyclerView
                                positionTarget = v.tag as Int
                            }
                        }
                        positionTarget = if (viewId !in listOf(
                                frameLayoutItem,
                                recyclerView1,
                                recyclerView2
                            )
                        ) {
                            v.tag as Int
                        } else {
                            -1
                        }
                        if (viewSource != null) {
                            val source = viewSource.parent as RecyclerView
                            val adapterSource = source.adapter as TextAdapter?
                            val positionSource = viewSource.tag as Int
                            val list: String? = adapterSource?.getList()?.get(positionSource)
                            val listSource = adapterSource?.getList()?.toMutableList()?.apply {
                                removeAt(positionSource)
                            }
                            listSource?.let { adapterSource.updateList(it) }
                            adapterSource?.notifyDataSetChanged()
                            val adapterTarget = target.adapter as TextAdapter?
                            val customListTarget = adapterTarget?.getList()?.toMutableList()
                            if (positionTarget >= 0) {
                                list?.let { customListTarget?.add(positionTarget, it) }
                            } else {
                                list?.let { customListTarget?.add(it) }
                            }
                            customListTarget?.let { adapterTarget.updateList(it) }

                            adapterTarget?.notifyDataSetChanged()

                            if (source.id == recyclerView2 && (adapterSource?.itemCount ?: 0) > 0) {
                                if (customListTarget != null) {
                                    listener.setEmptyList(
                                        View.VISIBLE,
                                        recyclerView2,
                                        customListTarget
                                    )
                                }
                            }

                            if (source.id == recyclerView1 && (adapterSource?.itemCount ?: 0) > 0) {
                                if (customListTarget != null) {
                                    listener.setEmptyList(
                                        View.VISIBLE,
                                        recyclerView1,
                                        customListTarget
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
        if (!isDropped && event.localState != null) {
            (event.localState as View).visibility = View.VISIBLE
        }
        return true
    }
}