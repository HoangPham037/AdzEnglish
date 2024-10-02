package com.adzenglish.adzenglish.ui.intro

import android.graphics.Color
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemIntroBinding
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.goneIf


class IntroAdapter(private val event: (ArrayList<String>) -> Unit) :
    BaseRecyclerAdapter<String, IntroAdapter.ViewHolder>() {
    var listItemSelect: ArrayList<String> = arrayListOf()
    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_intro
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<String>(binding) {
        override fun bind(itemData: String?) {
            super.bind(itemData)
            if (binding is ItemIntroBinding) {
                itemData?.let {
                    binding.tvAnswer.text = itemData
                    binding.space.goneIf(adapterPosition != listItem.size - Constants.INDEX_1)
                    binding.tvAnswer.setOnClickListener {
                        updateUi(binding, itemData)
                    }
                }
            }
        }

        private fun updateUi(binding: ItemIntroBinding, itemData: String) {
            val boolean = listItemSelect.indexOf(itemData) == -Constants.INDEX_1
            if (boolean) listItemSelect.add(itemData) else listItemSelect.remove(itemData)
            event.invoke(listItemSelect)
            binding.tvAnswer.setTextColor(
                if (boolean) Color.WHITE else binding.root.context.getColor(
                    R.color.gray_02
                )
            )
            binding.tvAnswer.setBackgroundColor(if (boolean) binding.root.context.getColor(R.color.green_02) else Color.WHITE)
        }
    }
}