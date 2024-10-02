package com.adzenglish.adzenglish.adapter


import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemKeyboardBinding
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.setInVisibility

class KeyboardAdapter(private var world: String, private val event: (String) -> Unit) :
    BaseRecyclerAdapter<KeyboardModel, KeyboardAdapter.KeyBoardViewHolder>() {
    inner class KeyBoardViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<KeyboardModel>(binding) {
        override fun bind(itemData: KeyboardModel?) {
            super.bind(itemData)
            if (binding is ItemKeyboardBinding) {
                with(binding) {
                    setupInitialView()
                    if (world.lowercase().contains(itemData?.label.toString())) {
                        binding.tvKey.apply {
                            isEnabled = true
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.blue_02))
                            setBackgroundResource(R.drawable.bgr_item_keyboard_show)
                            if (world.isNotEmpty()) {
                                itemData?.let { setupClickListeners(it) }
                            }
                        }
                    }
                }
            }
        }

        private fun TextView.setupClickListeners(
            item: KeyboardModel,
        ) {
            click {
                if (item.label == world[Constants.INDEX_0].toString()
                        .lowercase()
                ) {
                    item.label.let { label -> event.invoke(label) }
                    setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.gray_01
                        )
                    )
                    setBackgroundResource(R.drawable.bgr_item_keyboard_hide)
                    world = if (world.length > Constants.INDEX_1) {
                        world.substring(Constants.INDEX_1)
                    } else {
                        ""
                    }
                    notifyItemChanged(adapterPosition)
                } else {
                    setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.red
                        )
                    )
                    setBackgroundResource(R.drawable.bgr_item_keyboard_error)
                    handler.postDelayed({
                        setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.blue_02
                            )
                        )
                        setBackgroundResource(R.drawable.bgr_item_keyboard_show)
                    }, 500)
                }
            }
        }

        private fun ItemKeyboardBinding.setupInitialView() {
            tvKey.apply {
                text = itemData?.label
                isEnabled = false
            }
            binding.root.setInVisibility(itemData?.label != "1")

        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_keyboard
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyBoardViewHolder {
        return KeyBoardViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}