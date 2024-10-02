package com.adzenglish.adzenglish.ui.learn.checkpoint

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemCheckpointBinding
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.gone
import com.adzenglish.adzenglish.utils.extension.visible

class DoCheckPointAdapter(private val event: (Int) -> Unit) :
    BaseRecyclerAdapter<Checkpoint, DoCheckPointAdapter.DoCheckpointViewHolder>() {
    inner class DoCheckpointViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<Checkpoint>(binding) {
        override fun bind(itemData: Checkpoint?) {
            super.bind(itemData)
            if (binding is ItemCheckpointBinding) {
                with(binding) {
                    setupInitialView()
                    itemData?.let {
                        tvQuestion.text = it.question
                        tvAnswerRight.setupClickView(it, 1, this)
                        tvAnswerWrong.setupClickView(it, 0, this)
                        btnCheckAnswer.click {
                            event.invoke(adapterPosition)
                        }
                    }
                }
            }
        }

        private fun ItemCheckpointBinding.setupInitialView() {
            btnCheckAnswer.setBackgroundResource(R.drawable.bg_text_continue)
            imgStateAnswer.gone()
            layoutChooseAnswer.visible()
            btnCheckAnswer.isEnabled = false
        }

        private fun TextView.setupClickView(
            item: Checkpoint,
            answer: Int,
            binding: ItemCheckpointBinding
        ) {
            click {
                val isCorrect = (answer == item.answer)
                updateResultView(isCorrect, binding)
                item.isAnswer = isCorrect
                item.isLearn = true
                binding.btnCheckAnswer.isEnabled = true
                binding.btnCheckAnswer.setBackgroundResource(R.drawable.bg_text_view)
                binding.btnCheckAnswer.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
            }
        }

        private fun updateResultView(
            isCorrect: Boolean,
            binding: ItemCheckpointBinding
        ) {
            val srcImg = if (isCorrect) R.drawable.ic_exactly else R.drawable.ic_wrong
            binding.imgStateAnswer.visible()
            binding.imgStateAnswer.setImageResource(srcImg)
            binding.layoutChooseAnswer.gone()
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_checkpoint
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoCheckpointViewHolder {
        return DoCheckpointViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}