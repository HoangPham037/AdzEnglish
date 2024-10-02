package adapter

import android.content.ClipData
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemTextBinding
import com.adzenglish.adzenglish.ui.learn.grammar.DragListener
import com.adzenglish.adzenglish.ui.learn.grammar.Listener

class TextAdapter(private val listener: Listener?) :
    BaseRecyclerAdapter<String, TextAdapter.TextViewHolder>(), View.OnTouchListener {
    inner class TextViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<String>(binding) {
        override fun bind(itemData: String?) {
            super.bind(itemData)
            if (binding is ItemTextBinding) {
                binding.tvName.text = itemData
                binding.frameLayoutItem.tag = adapterPosition
                binding.frameLayoutItem.setOnTouchListener(this@TextAdapter)
                binding.frameLayoutItem.setOnDragListener(listener?.let { DragListener(it) })
            }
        }
    }

    val dragInstance: DragListener?
        get() = if (listener != null) {
            DragListener(listener)
        } else {
            Log.e(javaClass::class.simpleName, "Listener not initialized")
            null
        }

    fun updateList(newList: List<String>) {
        this.listItem = newList
    }

    fun getList() : List<String>{
        return listItem
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(v)
                v?.startDragAndDrop(data, shadowBuilder, v, 0)
                return true
            }
        }
        return false
    }
}