package com.adzenglish.adzenglish.ui.practice.grammar

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemPracticeGrammarBinding
import com.adzenglish.adzenglish.models.local.room.entity.RuleEntity
import com.adzenglish.adzenglish.viewmodel.PracticeVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GrammarAdapter(val viewModel: PracticeVM, val event: (RuleEntity) -> Unit) :
    BaseRecyclerAdapter<RuleEntity, GrammarAdapter.GrammarViewHolder>() {
    private var listRuleGroup: Map<String, List<RuleEntity>> = mapOf()
    private var adapterGrammarDetail: GrammarDetailAdapter? = null
    override fun getItemCount() = listRuleGroup.size

    @SuppressLint("NotifyDataSetChanged")
    override fun submitList(list: List<RuleEntity>?) {
        list?.groupBy { it.lessonId.toString() }?.let { listNew ->
            listRuleGroup = listNew
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: GrammarViewHolder, position: Int) {
        holder.bind(listRuleGroup.keys.toMutableList()[position])
    }

    inner class GrammarViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<RuleEntity>(binding) {
        fun bind(key: String) {
            if (binding is ItemPracticeGrammarBinding) {
                val listItemRule = listRuleGroup.getValue(key).toMutableList()
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    val lesson = viewModel.repository.getLessonById(key.toInt())
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        binding.tvTitle.text = lesson.title
                    }
                }
                adapterGrammarDetail = GrammarDetailAdapter(viewModel, event)
                binding.rcvGrammar.adapter = adapterGrammarDetail
                adapterGrammarDetail?.submitList(listItemRule)
            }
        }
    }


    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_practice_grammar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrammarViewHolder {
        return GrammarViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}

