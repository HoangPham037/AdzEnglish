package com.adzenglish.adzenglish.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.media.MediaManager
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemConversionBinding
import com.adzenglish.adzenglish.models.local.PracticeQuestion
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.gone
import com.adzenglish.adzenglish.utils.extension.visible
import com.adzenglish.adzenglish.viewmodel.DoConversionVM

class DoConversionAdapter(
    private val doConversionVM: DoConversionVM,
    private val event: (Int) -> Unit
) :
    BaseRecyclerAdapter<PracticeQuestion, DoConversionAdapter.DoConversionViewHolder>() {
    inner class DoConversionViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<PracticeQuestion>(binding) {
        override fun bind(itemData: PracticeQuestion?) {
            super.bind(itemData)
            if (binding is ItemConversionBinding) {
                var isTurnOffSound = false
                with(binding) {
                    setupInitialView()
                    itemData?.let { practiceQuestion ->
                        val answers = getShuffledAnswers(practiceQuestion)
                        setupViewData(practiceQuestion, answers)
                        setupClickListeners(practiceQuestion, answers, this)
                    }
                    tvChangeType.click {
                        if (isTurnOffSound) {
                            tvQuestion.gone()
                            ivListenAnswer.visible()
                            isTurnOffSound = !isTurnOffSound
                            tvChangeType.text = itemView.context.getText(R.string.txt_turn_of_sound)
                        } else {
                            tvQuestion.visible()
                            ivListenAnswer.gone()
                            tvChangeType.text = itemView.context.getText(R.string.txt_turn_on_sound)
                            isTurnOffSound = !isTurnOffSound
                        }
                    }
                    ivListenAnswer.click {
                       if (itemData?.sound?.isNotEmpty() == true) {
                           itemData.sound.let { sound ->
                               MediaManager.getInstance()?.playWithPath(sound)
                           }
                       }
                    }
                }
            }
        }

        private fun ItemConversionBinding.setupClickListeners(
            item: PracticeQuestion,
            answers: List<String>,
            binding: ItemConversionBinding
        ) {

            layoutCheckAnswer.tvAnswerOne.setupClickView(item, answers[0], binding)
            layoutCheckAnswer.tvAnswerTwo.setupClickView(item, answers[1], binding)
            layoutCheckAnswer.layoutBtnNext.btnNext.click {
                event.invoke(
                    adapterPosition
                )
            }
        }

        private fun TextView.setupClickView(
            item: PracticeQuestion,
            answer: String,
            binding: ItemConversionBinding
        ) {
            click {
                val isCorrect = doConversionVM.checkAnswer(answer, item)
                updateResultView(isCorrect, item, binding)
                item.isAnswer = isCorrect
            }
        }

        private fun updateResultView(
            isCorrect: Boolean,
            item: PracticeQuestion,
            binding: ItemConversionBinding
        ) {
            with(binding.layoutCheckAnswer) {
                layoutBtnNext.btnNext.visible()
                tvAnswerOne.gone()
                tvAnswerTwo.gone()
                layoutStateAnswer.constraintResult.visible()
                val colorRes = if (isCorrect) R.color.blue_02 else R.color.red
                val resultText =
                    if (isCorrect) itemView.context.resources.getText(R.string.txt_true_answer) else itemView.context.resources.getText(
                        R.string.txt_right_answer
                    )
                with(layoutStateAnswer) {
                    tvTitleQuestion.text = resultText
                    tvTitleQuestion.setTextColor(itemView.context.getColor(colorRes))
                    tvResult.setTextColor(itemView.context.getColor(colorRes))
                    ivSound.setImageResource(if (isCorrect) R.drawable.ic_sound else R.drawable.ic_sound_false)
                    tvResult.text = item.ru_1
                    ivFeedBack.setImageResource(if (isCorrect) R.drawable.ic_feedback else R.drawable.ic_feedback_false)
                    constraintResult.setBackgroundResource(if (isCorrect) R.drawable.bg_result_true else R.drawable.bg_result_false)
                }
            }
        }

        private fun ItemConversionBinding.setupViewData(
            practiceQuestion: PracticeQuestion,
            answers: List<String>
        ) {
            tvChangeType.text = itemView.context.getText(R.string.txt_turn_of_sound)
            tvQuestion.text = practiceQuestion.en_1
            layoutCheckAnswer.tvAnswerOne.text = answers[0]
            layoutCheckAnswer.tvAnswerTwo.text = answers[1]
        }

        private fun ItemConversionBinding.setupInitialView() {
            with(layoutCheckAnswer) {
                layoutBtnNext.btnNext.gone()
                tvAnswerOne.visible()
                tvAnswerTwo.visible()
                layoutStateAnswer.constraintResult.gone()
            }
        }

        private fun getShuffledAnswers(item: PracticeQuestion): List<String> {
            return listOf(item.ru_1, item.ru_2).shuffled()
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_conversion
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoConversionViewHolder {
        return DoConversionViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}