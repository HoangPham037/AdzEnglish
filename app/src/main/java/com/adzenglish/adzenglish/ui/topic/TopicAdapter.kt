package com.adzenglish.adzenglish.ui.topic

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemSelectedTopicBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.room.entity.ThemeEntity
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.goneIf
import com.adzenglish.adzenglish.viewmodel.TopicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TopicAdapter(val viewModel: TopicViewModel, val event: (ThemeEntity?) -> Unit) :
    BaseRecyclerAdapter<ThemeEntity, TopicAdapter.ViewHolder>() {
    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_selected_topic
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<ThemeEntity>(binding) {
        override fun bind(itemData: ThemeEntity?) {
            super.bind(itemData)
            if (binding is ItemSelectedTopicBinding) {
                itemData?.let {
                    updateUi(binding, it)
                    binding.tvTitleTopic.text = itemData.title
                    binding.space.goneIf(adapterPosition != listItem.size - Constants.INDEX_1)
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        val totalDict: Int
                        val totalDictLeaning: Int
                        val list: ArrayList<DictEntity> = arrayListOf()
                        viewModel.repository.getWordThemeBySetId(itemData.id).forEach { wordTheme ->
                            list.addAll(viewModel.repository.getAllDictByThemeId(wordTheme.id))
                        }
                        totalDict = list.size
                        totalDictLeaning = list.count { item -> item.isLeaning }
                        withContext(Dispatchers.Main) {
                            binding.progressBar.max = totalDict
                            binding.progressBar.progress = totalDictLeaning
                            binding.tvLearnedWords.text =
                                String.format("${totalDictLeaning}/${totalDict} từ")
                        }
                    }
                    itemView.setOnClickListener { clickView(binding, itemData) }
                    binding.btnSelect.setOnClickListener { clickView(binding, itemData) }
                    binding.ivTopic.setImageBitmap(
                        BitmapFactory.decodeStream(
                            itemView.context.assets.open(
                                "image/theme/${itemData.id}.png"
                            )
                        )
                    )
                }
            }
        }

        private fun clickView(binding: ItemSelectedTopicBinding, itemData: ThemeEntity) {
            if (itemData.category == Constants.INDEX_0) itemData.category =
                Constants.INDEX_1 else itemData.category = Constants.INDEX_0
            if (listItem.count { it.category == Constants.INDEX_1 } == Constants.INDEX_0) {
                event.invoke(null)
            } else {
                event.invoke(itemData)
                updateUi(binding, itemData)
            }
        }

        private fun updateUi(binding: ItemSelectedTopicBinding, itemData: ThemeEntity) {
            binding.btnSelect.text =
                String.format(if (itemData.category == Constants.INDEX_1) "Đã chọn" else "Chọn")
            binding.btnSelect.setBackgroundResource(if (itemData.category == Constants.INDEX_1) R.drawable.bg_select else R.drawable.bg_select_false)
            binding.btnSelect.setTextColor(
                if (itemData.category == Constants.INDEX_1) Color.WHITE else binding.root.context.getColor(
                    R.color.green_01
                )
            )
        }
    }
}