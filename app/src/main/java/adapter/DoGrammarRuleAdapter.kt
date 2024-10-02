package adapter

import android.util.Log
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemDoGrammarRuleBinding
import com.adzenglish.adzenglish.models.local.room.entity.RuleEntity
import com.adzenglish.adzenglish.utils.Utils
import com.adzenglish.adzenglish.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DoGrammarRuleAdapter(
    val viewModel: ViewModel,
    val event: (Int, Int, Int) -> Unit,
    val eventFeedback: () -> Unit
) : BaseRecyclerAdapter<RuleEntity, DoGrammarRuleAdapter.DoGrammarRuleViewHolder>() {
    inner class DoGrammarRuleViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<RuleEntity>(binding) {
        private var doGrammarAdapter: DoGrammarAdapter? = null

        override fun bind(itemData: RuleEntity?) {
            super.bind(itemData)
            if (binding is ItemDoGrammarRuleBinding) {
                itemData?.let { ruleResult ->
                    doGrammarAdapter = DoGrammarAdapter(viewModel, { pos ->
                        binding.recyclerViewDropAnswer.smoothScrollToPosition(pos + 1)
                        doGrammarAdapter?.listItem?.size?.let {
                            event.invoke(
                                pos,
                                adapterPosition,
                                it
                            )
                        }
                    }, {
                        eventFeedback.invoke()
                    })
                    binding.recyclerViewDropAnswer.setHasFixedSize(true)
                    binding.recyclerViewDropAnswer.adapter = doGrammarAdapter
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        val listGrammar = viewModel.repository.getQuestionByRuleId(ruleResult.id)
                        Log.d("checkListGrammar", "bind: listGrammar = $listGrammar")
                        withContext(Dispatchers.Main) {
                            doGrammarAdapter?.submitList(listGrammar)
                        }
                    }
                    binding.tvGrammar.text =
                        Utils.setBoldAndBackgroundSpannable(
                            ruleResult.rule,
                            itemView.context
                        )

                }

            }
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_do_grammar_rule
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoGrammarRuleViewHolder {
        return DoGrammarRuleViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}