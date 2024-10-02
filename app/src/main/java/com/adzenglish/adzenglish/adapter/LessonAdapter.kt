package com.adzenglish.adzenglish.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemLessonHomesBinding
import com.adzenglish.adzenglish.databinding.ItemLessonHomesCheckpointBinding
import com.adzenglish.adzenglish.databinding.ItemLessonHomesPracticeBinding
import com.adzenglish.adzenglish.models.local.room.entity.Lessons
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.formatString
import com.adzenglish.adzenglish.utils.extension.invisible
import com.adzenglish.adzenglish.utils.extension.visible


class LessonAdapter(
    private val event: (Lessons?) -> Unit,
    private val eventLearAgain: () -> Unit
) :
    BaseRecyclerAdapter<Lessons, BaseViewHolder<Lessons>>() {

    companion object {
        private const val TYPE_LESSON = 0
        private const val TYPE_PRACTICE = 1
        private const val TYPE_CHECK_POINT = 2
    }

    inner class LessonViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<Lessons>(binding) {
        override fun bind(itemData: Lessons?) {
            super.bind(itemData)
            if (binding is ItemLessonHomesBinding) {
                binding.apply {
                    setupInitialView()
                    itemData?.let { lesson ->
                        if (canClickItem(adapterPosition)) {
                            tvAction.click {
                                event.invoke(lesson)
                            }
                        } else {
                            tvAction.click {
                                event.invoke(null)
                            }
                        }

                        updateUI()
                        tvLessonNumber.text = lesson.numberUser?.formatString("Lesson")
                        tvTitleLesson.text = lesson.title
                    }
                }
            }
        }
        private fun canClickItem(position: Int): Boolean {
            return position == 0 || listItem[position - 1].isFinishPartVocabulary == true || listItem[position-1].isLearned == true
        }

        private fun ItemLessonHomesBinding.setupInitialView() {
            imgStarLeft.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_left_gray))
            imgStarCenter.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_center_gray))
            imgStarRight.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_right_gray))
            val layoutParams = tvLessonNumber.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(0,0,0,-40)
            tvLessonNumber.layoutParams = layoutParams
            tvDescLesson.invisible()
            tvAction.text = ContextCompat.getString(itemView.context, R.string.txt_start)
        }
        private fun ItemLessonHomesBinding.updateUI() {
            if (itemData?.isFinishPartVocabulary == true) {
                imgStarLeft.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_left_shadow))
            }
            if (itemData?.isFinishPartGrammar == true) {
                imgStarLeft.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_left_shadow))
                imgStarCenter.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_center_shadow))
            }
            if (itemData?.isFinishPartPractice == true) {
                imgStarLeft.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_left_shadow))
                imgStarCenter.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_center_shadow))
                imgStarRight.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_right_shadow))
                tvAction.text = ContextCompat.getString(itemView.context, R.string.txt_try_again)
                val layoutParams = tvLessonNumber.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0,0,0,0)
                tvLessonNumber.layoutParams = layoutParams
                tvDescLesson.visible()
                tvAction.click {
                    eventLearAgain.invoke()
                }
            }
        }
    }

    inner class PracticeViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<Lessons>(binding) {
        override fun bind(itemData: Lessons?) {
            super.bind(itemData)
            if (binding is ItemLessonHomesPracticeBinding) {
                binding.apply {
                    setupInitialView()
                    itemData?.let { lessons ->
                        if (lessons.isLearned == true) {
                            updateView()
                        }
                        tvLessonNumber.text = lessons.header
                        tvTitleLesson.text = lessons.description
                        if(canClickItem(adapterPosition)) {
                            tvAction.click {
                                event.invoke(lessons)
                            }
                        }else {
                            tvAction.click {
                                event.invoke(null)
                            }
                        }
                    }

                }
            }
        }
        private fun canClickItem(position: Int): Boolean {
            return listItem[position - 1].isFinishPartVocabulary == true || listItem[position-1].isLearned == true
        }

        private fun ItemLessonHomesPracticeBinding.setupInitialView() {
            imgStarLeft.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_left_gray))
            imgStarCenter.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_center_gray))
            imgStarRight.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_right_gray))
        }

        private fun ItemLessonHomesPracticeBinding.updateView() {
            imgStarLeft.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_left_shadow))
            imgStarCenter.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_center_shadow))
            imgStarRight.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_right_shadow))
        }
    }

    inner class CheckpointViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<Lessons>(binding) {
        override fun bind(itemData: Lessons?) {
            super.bind(itemData)
            if (binding is ItemLessonHomesCheckpointBinding) {
                binding.apply {
                    setupInitialView()
                    itemData?.let { lessons ->
                        if (lessons.isLearned == true) {
                            updateView()
                        }
                        tvLessonNumber.text = lessons.number?.formatString("Checkpoint")
                        tvTitleLesson.text =
                            itemView.context.resources.getText(R.string.txt_assessing_lang_learn)
                        if(canClickItem(adapterPosition)) {
                            tvAction.click {
                                event.invoke(lessons)
                            }
                        }else {
                            tvAction.click {
                                event.invoke(null)
                            }
                        }

                    }
                }
            }
        }
        private fun canClickItem(position: Int): Boolean {
            return listItem[position - 1].isFinishPartVocabulary == true || listItem[position-1].isLearned == true
        }

        private fun ItemLessonHomesCheckpointBinding.setupInitialView() {
            imgStarLeft.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_left_gray))
            imgStarCenter.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_center_gray))
            imgStarRight.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_right_gray))
        }

        private fun ItemLessonHomesCheckpointBinding.updateView() {
            imgStarLeft.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_left_shadow))
            imgStarCenter.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_center_shadow))
            imgStarRight.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_right_shadow))
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return when (viewType) {
            TYPE_LESSON -> R.layout.item_lesson_homes
            TYPE_PRACTICE -> R.layout.item_lesson_homes_practice
            TYPE_CHECK_POINT -> R.layout.item_lesson_homes_checkpoint
            else -> {
                throw Exception("Error get item layout resource")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Lessons> {
        return when (viewType) {
            TYPE_LESSON -> {
                LessonViewHolder(getViewHolderDataBinding(parent, viewType))
            }

            TYPE_PRACTICE -> {
                PracticeViewHolder(getViewHolderDataBinding(parent, viewType))
            }

            TYPE_CHECK_POINT -> {
                CheckpointViewHolder(getViewHolderDataBinding(parent, viewType))
            }

            else -> {
                throw Exception("Error reading holder type")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = listItem[position]
        return when (item.type) {
            1 -> TYPE_LESSON
            2 -> TYPE_PRACTICE
            3 -> TYPE_CHECK_POINT
            else -> {
                throw Exception("Error reading item type")
            }
        }
    }

}