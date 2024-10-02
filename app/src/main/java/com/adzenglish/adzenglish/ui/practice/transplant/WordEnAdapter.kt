package com.adzenglish.adzenglish.ui.practice.transplant

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.media.MediaManager
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemTransplantFromBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.click

class WordEnAdapter(val isPlay: Boolean, val event: (DictEntity) -> Unit) :
    BaseRecyclerAdapter<DictEntity, WordEnAdapter.ViewHolder>() {
    var isAnswerEn: Boolean? = null
    var isClick: Boolean = true
    var dictEn: DictEntity? = null
    var id: String? = null
    var listSelected: ArrayList<Int> = arrayListOf()
    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_transplant_from
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<DictEntity>(binding) {
        @SuppressLint("NotifyDataSetChanged")
        override fun bind(itemData: DictEntity?) {
            super.bind(itemData)
            if (binding is ItemTransplantFromBinding) {
                itemData?.let {
                    if (itemData.id.toString() == id) {
                        when (isAnswerEn) {
                            null -> updateUi(binding, R.drawable.bg_click, R.color.blue_02)
                            true -> updateUi(
                                binding,
                                R.drawable.bg_result_true_word,
                                R.color.green_01
                            )

                            false -> updateUi(
                                binding,
                                R.drawable.bg_result_false_word,
                                R.color.red_01
                            )
                        }
                    } else {
                        binding.tvWord.setBackgroundResource(R.drawable.bg_result)
                        val boolean = listSelected.indexOf(itemData.id) == -Constants.INDEX_1
                        binding.tvWord.setTextColor(binding.root.context.getColor(if (boolean) R.color.blue_01 else R.color.gray_02))
                    }

                    binding.tvWord.text = itemData.wordEn
                    binding.tvWord.click {
                        if (!isClick) return@click
                        val boolean = listSelected.indexOf(itemData.id) == -Constants.INDEX_1
                        if (!boolean) return@click
                        dictEn = itemData
                        id = if (id == null) itemData.id.toString() else null
                        id?.let {
                            if (isPlay) MediaManager.getInstance()?.playWithPath(itemData.sound)
                        }
                        event.invoke(itemData)
                        notifyDataSetChanged()
                    }
                }
            }
        }

        private fun updateUi(binding: ItemTransplantFromBinding, backGround: Int, color: Int) {
            binding.tvWord.setBackgroundResource(backGround)
            binding.tvWord.setTextColor(binding.root.context.getColor(color))
        }
    }
}