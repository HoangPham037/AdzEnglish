package adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemQuickTaskBinding
import com.adzenglish.adzenglish.ui.learn.quicktask.QuickTask
import com.adzenglish.adzenglish.utils.Utils

class DoQuickTaskAdapter(val fragment: Fragment, private val event: (QuickTask) -> Unit) :
    BaseRecyclerAdapter<QuickTask, DoQuickTaskAdapter.DoQuickTaskViewHolder>() {
    inner class DoQuickTaskViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<QuickTask>(binding) {
        override fun bind(itemData: QuickTask?) {
            super.bind(itemData)
            if (binding is ItemQuickTaskBinding) {
                if (adapterPosition == 0) {
                    itemData?.question?.let {
                        binding.tvContent.text = Utils.setBackgroundSpannableQuickTask(
                            it,
                            fragment.requireContext()
                        )
                    }
                    binding.root.background =
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.bg_item_quick_task_first
                        )
                    if (itemData != null) {
                        event.invoke(itemData)
                    }
                } else {
                    itemData?.question?.let {
                        binding.tvContent.text = Utils.setTextColorSpannableQuickTask(
                            it,
                            fragment.requireContext()
                        )
                    }
                    binding.root.background =
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.bg_item_quick_task_other
                        )
                }
            }
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_quick_task
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoQuickTaskViewHolder {
        return DoQuickTaskViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}