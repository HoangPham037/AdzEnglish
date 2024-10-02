package com.adzenglish.adzenglish.ui.intro

import android.graphics.Color
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemIntroBinding
import com.adzenglish.adzenglish.models.local.room.entity.ThemeEntity
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.goneIf


class IntroThemeAdapter(private val event: (List<ThemeEntity>) -> Unit) :
    BaseRecyclerAdapter<ThemeEntity, IntroThemeAdapter.ViewHolder>() {
    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_intro
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<ThemeEntity>(binding) {
        override fun bind(itemData: ThemeEntity?) {
            super.bind(itemData)
            if (binding is ItemIntroBinding) {
                itemData?.let {
                    binding.tvAnswer.text = itemData.title
                    val boolean = itemData.category == Constants.INDEX_1
                    binding.space.goneIf(adapterPosition != listItem.size - Constants.INDEX_1)
                    binding.tvAnswer.setTextColor(
                        if (boolean) Color.WHITE else binding.root.context.getColor(
                            R.color.blue_01
                        )
                    )
                    binding.tvAnswer.setBackgroundColor(if (boolean) binding.root.context.getColor(R.color.green_02) else Color.WHITE)
                    binding.tvAnswer.setOnClickListener {
                        updateUi(itemData, adapterPosition)
                    }
                }
            }
        }

        private fun updateUi(it: ThemeEntity, position: Int) {
            if (it.category == Constants.INDEX_0) it.category = Constants.INDEX_1 else it.category =
                Constants.INDEX_0
            event.invoke(listItem)
            notifyItemChanged(position)
        }
    }
}