package com.adzenglish.adzenglish.ui.topic.box

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemNewWordsBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.goneIf


class BoxAdapter(val event: () -> Unit) :
    BaseRecyclerAdapter<DictEntity, BoxAdapter.ViewHolder>() {
    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_new_words
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<DictEntity>(binding) {
        override fun bind(itemData: DictEntity?) {
            super.bind(itemData)
            if (binding is ItemNewWordsBinding) {
                itemData?.let {
                    binding.tvVni.text = it.wordRu
                    binding.tvNewWords.text = it.wordEn
                    binding.space.goneIf(adapterPosition != listItem.size - Constants.INDEX_1)
                    binding.ivCheckAdd.setImageResource(if (it.isAnswer) R.drawable.ic_check_false else R.drawable.ic_check_true)
                    itemView.setOnClickListener { _ ->
                        it.isAnswer = (!it.isAnswer)
                        notifyItemChanged(adapterPosition)
                        event.invoke()
                    }
                }
            }
        }
    }
}