package com.adzenglish.adzenglish.ui.setting.level

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemLevelSettingBinding
import com.adzenglish.adzenglish.models.local.room.entity.LevelEntity
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.gone
import com.adzenglish.adzenglish.viewmodel.SettingVM


class SettingLevelAdapter(var viewModel: SettingVM, val event: (Int) -> Unit) :
    BaseRecyclerAdapter<LevelEntity, SettingLevelAdapter.ViewHolder>() {
    private var level: LevelEntity? = null
    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_level_setting
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<LevelEntity>(binding) {
        @SuppressLint("NotifyDataSetChanged")
        override fun bind(itemData: LevelEntity?) {
            super.bind(itemData)
            if (binding is ItemLevelSettingBinding) {
                itemData?.let {
                    binding.tvName.text = itemData.title
                    binding.tvRatio.text = String.format("Tổng số ${itemData.lessonsCount}")
                    if (itemData.isSelected == Constants.INDEX_1) level = itemData
                    when (adapterPosition) {
                        Constants.INDEX_0 -> binding.ivMeda.gone()
                        Constants.INDEX_1 -> binding.ivMeda.setImageResource(R.drawable.ic_bronze_medal)
                        Constants.INDEX_2 -> binding.ivMeda.setImageResource(R.drawable.ic_silver_medal)
                        Constants.INDEX_3 -> binding.ivMeda.setImageResource(R.drawable.ic_gold_medal)
                    }
                    binding.ivCheck.setImageResource(if (itemData.isSelected != Constants.INDEX_1) R.drawable.ic_check_false else R.drawable.ic_check_true)
                    itemView.click {
                        if (level?.id == itemData.id) return@click
                        level?.let {
                            viewModel.updateLevelIsSelectedById(it.id, Constants.INDEX_0)
                        }
                        level = itemData
                        event.invoke(itemData.id)
                    }
                }
            }
        }
    }
}