package com.adzenglish.adzenglish.ui.practice.grammar

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemGrammarDetailBinding
import com.adzenglish.adzenglish.models.local.room.entity.QuestionsEntity
import com.adzenglish.adzenglish.models.local.room.entity.RuleEntity
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.Utils
import com.adzenglish.adzenglish.utils.extension.goneIf
import com.adzenglish.adzenglish.viewmodel.PracticeVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GrammarDetailAdapter(val viewModel: PracticeVM, val event: (RuleEntity) -> Unit) :
    BaseRecyclerAdapter<RuleEntity, GrammarDetailAdapter.ViewHolder>() {
    private var id = -1
    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_grammar_detail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<RuleEntity>(binding) {
        @SuppressLint("NotifyDataSetChanged")
        override fun bind(itemData: RuleEntity?) {
            super.bind(itemData)
            if (binding is ItemGrammarDetailBinding) {
                itemData?.let {
                    binding.btnExample.goneIf(id != itemData.id)
                    binding.constraintExample.goneIf(id != itemData.id)
                    binding.tvRule.text =
                        String.format("Quy tắc ${adapterPosition + Constants.INDEX_1}")
                    binding.tvGrammar.text =
                        Utils.setBoldAndBackgroundSpannable(itemData.rule, itemView.context)
                    binding.btnExample.text =
                        if (!itemData.isStudying) "Đưa trở về bài học" else "Đánh dấu đã học"
                    binding.ivCheck.setImageResource(if (itemData.isStudying) R.drawable.ic_check_false else R.drawable.ic_check_true)
                    binding.ivShowExample.setImageResource(if (id != itemData.id) R.drawable.ic_show_example else R.drawable.ic_hidde_example)
                    binding.btnExample.setBackgroundResource(if (!itemData.isStudying) R.drawable.bg_example_learned else R.drawable.bg_example_studying)
                    binding.ivShowExample.setOnClickListener {
                        id = if (id == itemData.id) -1 else itemData.id
                        notifyDataSetChanged()
                        viewModel.viewModelScope.launch(Dispatchers.IO) {
                            val listQuestion: List<QuestionsEntity> =
                                viewModel.getListQuestion(itemData.id)
                            viewModel.viewModelScope.launch(Dispatchers.Main) {
                                val tvExampleOneEn =
                                    getTextView(listQuestion[Constants.INDEX_0].task , ";")
                                val tvExampleTwoEn =
                                    getTextView(listQuestion[Constants.INDEX_1].task, ";")
                                val tvExampleOneRu =
                                    getTextView(listQuestion[Constants.INDEX_0].answer, "+")
                                val tvExampleTwoRu =
                                    getTextView(listQuestion[Constants.INDEX_1].answer, "+")
                                binding.tvExampleOne.text =
                                    String.format("$tvExampleOneRu ($tvExampleOneEn)")
                                binding.tvExampleTwo.text =
                                    String.format("$tvExampleTwoRu ($tvExampleTwoEn)")
                            }
                        }
                    }
                    binding.btnExample.setOnClickListener {
                        event.invoke(itemData)
                    }
                }
            }
        }

        private fun getTextView(textView: String , type : String): String {
            return textView.split(type).joinToString(" ") { it.split("/")[Constants.INDEX_0] }
        }
    }
}