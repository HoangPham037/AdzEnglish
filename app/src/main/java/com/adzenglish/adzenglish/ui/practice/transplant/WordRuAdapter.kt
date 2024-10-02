package com.adzenglish.adzenglish.ui.practice.transplant

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemTransplantFromBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.click

class WordRuAdapter(val event: (DictEntity) -> Unit) :
    BaseRecyclerAdapter<DictEntity, WordRuAdapter.ViewHolder>() {
    var isAnswerRu: Boolean? = null
    var id: String? = null
    var isClick: Boolean = true
    var dictRu: DictEntity? = null
    var listSelected: ArrayList<Int> = arrayListOf()

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_transplant_from
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<DictEntity>(binding) {
        override fun bind(itemData: DictEntity?) {
            super.bind(itemData)
            if (binding is ItemTransplantFromBinding) {
                itemData?.let {
                    if (itemData.id.toString() == id) {
                        when (isAnswerRu) {
                            null -> updateUi(binding, R.drawable.bg_click, R.color.blue_01)
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
                        val boolean = listSelected.indexOf(itemData.id) == -Constants.INDEX_1
                        updateUi(
                            binding,
                            R.drawable.bg_result,
                            if (boolean) R.color.blue_01 else R.color.gray_02
                        )
                    }
                    binding.tvWord.text = itemData.wordRu
                    binding.root.click {
                        clickView(itemData)
                    }
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun clickView(itemData: DictEntity) {
            if (!isClick) return
            val boolean = listSelected.indexOf(itemData.id) == -Constants.INDEX_1
            if (!boolean) return
            dictRu = itemData
            id = if (id == null) itemData.id.toString() else null
            event.invoke(itemData)
            notifyDataSetChanged()
        }

        private fun updateUi(binding: ItemTransplantFromBinding, backGround: Int, color: Int) {
            binding.tvWord.setBackgroundResource(backGround)
            binding.tvWord.setTextColor(binding.root.context.getColor(color))
        }
    }
}