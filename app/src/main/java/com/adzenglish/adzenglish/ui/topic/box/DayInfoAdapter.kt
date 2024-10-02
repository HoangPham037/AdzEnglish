package com.adzenglish.adzenglish.ui.topic.box

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemCalendarBinding
import com.adzenglish.adzenglish.models.local.room.entity.DayInfo
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.goneIf
import com.adzenglish.adzenglish.viewmodel.TopicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DayInfoAdapter(val viewModel: TopicViewModel) :
    BaseRecyclerAdapter<DayInfo, DayInfoAdapter.ViewHolder>() {
    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_calendar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<DayInfo>(binding) {
        override fun bind(itemData: DayInfo?) {
            super.bind(itemData)
            if (binding is ItemCalendarBinding) {
                itemData?.let {
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        val boolean = viewModel.repository.getDayInfo(itemData.date) == null
                        viewModel.viewModelScope.launch(Dispatchers.Main) {
                            binding.tvDay.text = it.dayName
                            binding.tvDate.text = it.date.split("/")[Constants.INDEX_0]
                            binding.ivCheckDay.goneIf(boolean)
                            if (!boolean) binding.tvDate.setBackgroundResource(R.drawable.bg_day)
                        }
                    }
                    itemView.setOnClickListener {
                        viewModel.viewModelScope.launch(Dispatchers.IO) {
                            val boolean = viewModel.repository.getDayInfo(itemData.date) == null
                            if (boolean) viewModel.insertDay(itemData)
                        }
                    }
                }
            }
        }
    }
}