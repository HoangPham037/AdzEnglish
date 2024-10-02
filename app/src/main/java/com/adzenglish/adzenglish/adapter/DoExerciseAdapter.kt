package com.adzenglish.adzenglish.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.media.MediaManager
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemChooseAnswerBinding
import com.adzenglish.adzenglish.databinding.ItemWriteAnswerBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.gone
import com.adzenglish.adzenglish.utils.extension.visible
import com.adzenglish.adzenglish.viewmodel.TopicViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DoExerciseAdapter(
    private val viewModel: TopicViewModel,
    private val isSound: Boolean,
    private val event: (Int, TypeClick) -> Unit,
    private val eventFeedback: () -> Unit
) :
    BaseRecyclerAdapter<DictEntity, BaseViewHolder<DictEntity>>() {

    private lateinit var keyboardAdapter: KeyboardAdapter
    private var viewType = 1

    companion object {
        const val TYPE_CHOOSE_ANSWER = 1
        const val TYPE_WRITE_ANSWER = 2
    }

    inner class ChooseAnswerViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<DictEntity>(binding) {
        override fun bind(itemData: DictEntity?) {
            super.bind(itemData)
            if (binding is ItemChooseAnswerBinding) {
                with(binding) {
                    setupInitialView()
                    itemData?.let { item ->
                        val answers = getShuffledAnswers(item)
                        setupViewData(item, answers, isSound)
                        setupClickListeners(item, answers, this)
                    }
                }
            }
        }

        private fun ItemChooseAnswerBinding.setupInitialView() {
            with(layoutCheckAnswer) {
                layoutBtnNext.btnNext.gone()
                tvAnswerOne.visible()
                tvAnswerTwo.visible()
                layoutStateAnswer.constraintResult.gone()
            }
        }

        private fun getShuffledAnswers(item: DictEntity): List<String> {
            return listOf(item.translationWrong.split("/")[0], item.wordRu).shuffled()
        }

        private fun ItemChooseAnswerBinding.setupViewData(
            item: DictEntity,
            answers: List<String>,
            isSound: Boolean
        ) {
            if (isSound) {
                if (item.sound.isNotEmpty()) MediaManager.getInstance()?.playWithPath(item.sound)
            }
            tvWord.text = item.wordEn
            layoutCheckAnswer.tvAnswerOne.text = answers[0]
            layoutCheckAnswer.tvAnswerTwo.text = answers[1]
            Glide.with(itemView.context).load(item.image)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(false)
                .placeholder(R.drawable.ic_image_default)
                .error(R.drawable.ic_img_error)
                .into(imgQuestion)
        }

        private fun ItemChooseAnswerBinding.setupClickListeners(
            item: DictEntity,
            answers: List<String>,
            binding: ItemChooseAnswerBinding
        ) {
            layoutCheckAnswer.tvAnswerOne.setupClickView(item, answers[0], binding)
            layoutCheckAnswer.tvAnswerTwo.setupClickView(item, answers[1], binding)
            layoutCheckAnswer.layoutBtnNext.btnNext.click {
                event.invoke(
                    adapterPosition,
                    TypeClick.IN_CHOOSE_ANSWER
                )
            }
        }

        private fun TextView.setupClickView(
            item: DictEntity,
            answer: String,
            binding: ItemChooseAnswerBinding
        ) {
            click {
                val isCorrect = viewModel.checkAnswer(answer, item)
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    try {
                        MediaManager.getInstance()?.playAssetSound(
                            itemView.context,
                            if (isCorrect) Constants.KEY_SOUND_CORRECT else Constants.KEY_SOUND_WRONG
                        )
                    }catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
                updateResultView(isCorrect, item, binding)
                item.isLeaning = true
                item.isAnswer = isCorrect
            }
        }

        private fun updateResultView(
            isCorrect: Boolean,
            item: DictEntity,
            binding: ItemChooseAnswerBinding
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
                    ivSound.setOnClickListener { MediaManager.getInstance()?.playWithPath(item.sound) }
                    ivFeedBack.setOnClickListener {eventFeedback.invoke()}
                    tvTitleQuestion.text = resultText
                    tvTitleQuestion.setTextColor(itemView.context.getColor(colorRes))
                    tvResult.setTextColor(itemView.context.getColor(colorRes))
                    ivSound.setImageResource(if (isCorrect) R.drawable.ic_sound else R.drawable.ic_sound_false)
                    tvResult.text = String.format("%s - %s", item.wordEn, item.wordRu)
                    ivFeedBack.setImageResource(if (isCorrect) R.drawable.ic_feedback else R.drawable.ic_feedback_false)
                    constraintResult.setBackgroundResource(if (isCorrect) R.drawable.bg_result_true else R.drawable.bg_result_false)
                }
            }
        }
    }

    inner class WriteAnswerViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<DictEntity>(binding) {
        override fun bind(itemData: DictEntity?) {
            super.bind(itemData)
            if (binding is ItemWriteAnswerBinding) {
                val listWorldClick = StringBuilder("")
                binding.apply {
                    setupInitialView(listWorldClick)
                    itemData?.let { dict ->
                        setupViewData(dict)
                        setupKeyboardAdapter(listWorldClick, dict)
                    }
                }
            }
        }

        private fun ItemWriteAnswerBinding.setupInitialView(listWorldClick: StringBuilder) {
            tvCustomLineText.setTextReceived(listWorldClick.toString())
            constraintResult.gone()
            btnNext.isEnabled = false
        }

        private fun ItemWriteAnswerBinding.setupViewData(dict: DictEntity) {
            tvWord.text = dict.wordRu
            tvCustomLineText.setText(dict.wordEn)
            btnNext.setTextColor(itemView.context.getColor(R.color.gray_02))
            btnNext.setBackgroundResource(R.drawable.bg_text_continue)
        }

        private fun ItemWriteAnswerBinding.setupKeyboardAdapter(
            listWorldClick: StringBuilder,
            dict: DictEntity
        ) {
            keyboardAdapter = KeyboardAdapter(itemData?.wordEn!!.replace(" ","")) { world ->
                listWorldClick.append(world)
                tvCustomLineText.setTextReceived(listWorldClick.toString())
                if (listWorldClick.toString().lowercase() == dict.wordEn.lowercase().replace(" ","")) {
                    MediaManager.getInstance()?.playWithPath(dict.sound)
                    binding.apply {
                        with(constraintResult) {
                            visible()
                            ivSound.setOnClickListener { MediaManager.getInstance()?.playWithPath(dict.sound) }
                            ivFeedBack.setOnClickListener {eventFeedback.invoke()}
                        }

                        tvTitleQuestion.text =
                            itemView.context.resources.getText(R.string.txt_true_answer)
                        tvResult.text =
                            String.format("%s - %s", dict.wordEn, dict.wordRu)
                        btnNext.setupViewAndActionButton()
                    }
                }
            }
            recyclerView.layoutManager = getLayoutManager(itemView.context)
            recyclerView.adapter = keyboardAdapter
            val charList = charArrayOf(
                'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
                '1', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', '1',
                '1', '1', 'z', 'x', 'c', 'v', 'b', 'n', 'm'
            )
            val listKeyboardModel = ArrayList<KeyboardModel>()
            charList.forEach {
                listKeyboardModel.add(KeyboardModel(it.toString()))
            }
            keyboardAdapter.submitList(listKeyboardModel)
        }

        private fun Button.setupViewAndActionButton() {
            apply {
                isEnabled = true
                setTextColor(itemView.context.getColor(R.color.white))
                setBackgroundResource(R.drawable.bg_text_view)
                click {
                    event.invoke(adapterPosition, TypeClick.IN_WRITE_ANSWER)
                }
            }
        }
    }

    private fun getLayoutManager(cxt: Context): GridLayoutManager {
        val layoutManager = GridLayoutManager(cxt, 20)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            var map = intArrayOf(
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1,
                2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2
            )

            override fun getSpanSize(position: Int): Int {
                return map[position % map.size]
            }
        }
        return layoutManager
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return when (viewType) {
            TYPE_CHOOSE_ANSWER -> R.layout.item_choose_answer
            TYPE_WRITE_ANSWER -> R.layout.item_write_answer
            else -> {
                throw Exception("Error reading holder type")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<DictEntity> {
        return when (viewType) {
            TYPE_CHOOSE_ANSWER -> ChooseAnswerViewHolder(getViewHolderDataBinding(parent, viewType))
            TYPE_WRITE_ANSWER -> WriteAnswerViewHolder(getViewHolderDataBinding(parent, viewType))
            else -> {
                throw Exception("Error reading holder type")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (viewType) {
            1 -> TYPE_CHOOSE_ANSWER
            2 -> TYPE_WRITE_ANSWER
            else -> TYPE_WRITE_ANSWER
        }
    }

    fun updateViewType(newViewType: Int) {
        viewType = newViewType
        notifyDataSetChanged()
    }
}

enum class TypeClick {
    IN_CHOOSE_ANSWER,
    IN_WRITE_ANSWER
}