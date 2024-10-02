package com.adzenglish.adzenglish.ui.practice.skill

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.adapter.KeyboardAdapter
import com.adzenglish.adzenglish.adapter.KeyboardModel
import com.adzenglish.adzenglish.base.media.MediaManager
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemChooseAnswerSkillBinding
import com.adzenglish.adzenglish.databinding.ItemListenAnswerSkillBinding
import com.adzenglish.adzenglish.databinding.ItemWriteAnswerSkillBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.Utils
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.gone
import com.adzenglish.adzenglish.utils.extension.visible

class SkillAdapter(
    val isPlay: Boolean, private val event: (Int, TypeClickSkill) -> Unit
) : BaseRecyclerAdapter<DictEntity, BaseViewHolder<DictEntity>>() {
    private lateinit var keyboardAdapter: KeyboardAdapter
    private var viewType = Constants.INDEX_1
    private var isEn = true
    private var listTmp: ArrayList<DictEntity> = arrayListOf()

    companion object {
        const val TYPE_WRITE_ANSWER = Constants.INDEX_2
        const val TYPE_CHOOSE_ANSWER = Constants.INDEX_1
        const val TYPE_LISTEN_ANSWER = Constants.INDEX_3
    }

    inner class ChooseAnswerViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<DictEntity>(binding) {
        override fun bind(itemData: DictEntity?) {
            super.bind(itemData)
            if (binding is ItemChooseAnswerSkillBinding) {
                with(binding) {
                    setupInitialView()
                    itemData?.let { item ->
                        if (isPlay) MediaManager.getInstance()?.playWithPath(item.sound)
                        listTmp = listItem.toMutableList() as ArrayList
                        listTmp.sortedBy { it.id }
                        listTmp.remove(item)
                        listTmp.shuffle()
                        val answers =
                            if (isEn) arrayListOf(
                                item.wordRu,
                                listTmp[Constants.INDEX_0].wordRu
                            ) else arrayListOf(
                                item.wordEn,
                                listTmp[Constants.INDEX_0].wordEn
                            )
                        listTmp.add(item)
                        listTmp.sortedBy { it.id }
                        answers.shuffle()
                        setupViewData(item, answers)
                        setupClickListeners(item, answers, this)
                        listItem.toMutableList().add(item)
                        ivFeedBack.click {
                            event.invoke(adapterPosition, TypeClickSkill.FEED_BACK)
                        }
                        ivSound.click {
                            openSound(item.sound)
                        }
                    }
                }
            }
        }

        private fun ItemChooseAnswerSkillBinding.setupInitialView() {
            btnNext.gone()
            tvAnswerOne.visible()
            tvAnswerTwo.visible()
            constraintResult.gone()
        }

        private fun ItemChooseAnswerSkillBinding.setupViewData(
            item: DictEntity,
            answers: ArrayList<String>
        ) {
            tvQuestion.text = if (isEn) item.wordEn else item.wordRu
            tvAnswerOne.text = answers[Constants.INDEX_0]
            tvAnswerTwo.text = answers[Constants.INDEX_1]
        }

        private fun ItemChooseAnswerSkillBinding.setupClickListeners(
            item: DictEntity,
            answers: List<String>,
            binding: ItemChooseAnswerSkillBinding
        ) {
            tvAnswerOne.setupClickView(item, answers[Constants.INDEX_0], binding)
            tvAnswerTwo.setupClickView(item, answers[Constants.INDEX_1], binding)
            btnNext.click { event.invoke(adapterPosition, TypeClickSkill.CLICK_NEXT) }
        }

        private fun TextView.setupClickView(
            item: DictEntity, answer: String, binding: ItemChooseAnswerSkillBinding
        ) {
            click {
                val isCorrect = answer == if (isEn) item.wordRu else item.wordEn
                if (isPlay) MediaManager.getInstance()?.playAssetSound(
                    itemView.context,
                    if (isCorrect) Constants.KEY_SOUND_CORRECT else Constants.KEY_SOUND_WRONG
                )
                updateResultView(isCorrect, item, binding)
                item.isLeaning = true
                item.isAnswer = isCorrect
                event.invoke(adapterPosition, TypeClickSkill.CLICK_ANSWER)
            }
        }

        private fun updateResultView(
            isCorrect: Boolean, item: DictEntity, binding: ItemChooseAnswerSkillBinding
        ) {
            with(binding) {
                btnNext.visible()
                tvAnswerOne.gone()
                tvAnswerTwo.gone()
                constraintResult.visible()
                tvTitleQuestion.text =
                    if (isCorrect) itemView.context.resources.getText(R.string.txt_true_answer) else itemView.context.resources.getText(
                        R.string.txt_right_answer
                    )
                tvResult.text = String.format("%s - %s", item.wordEn, item.wordRu)
                ivSound.setImageResource(if (isCorrect) R.drawable.ic_sound else R.drawable.ic_sound_false)
                tvResult.setTextColor(itemView.context.getColor(if (isCorrect) R.color.blue_02 else R.color.red))
                ivFeedBack.setImageResource(if (isCorrect) R.drawable.ic_feedback else R.drawable.ic_feedback_false)
                tvTitleQuestion.setTextColor(itemView.context.getColor(if (isCorrect) R.color.blue_02 else R.color.red))
                constraintResult.setBackgroundResource(if (isCorrect) R.drawable.bg_result_true else R.drawable.bg_result_false)
            }
        }
    }

    private fun openSound(sound: String) {
        MediaManager.getInstance()?.playWithPath(sound)
    }

    inner class ListenAnswerViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<DictEntity>(binding) {
        override fun bind(itemData: DictEntity?) {
            super.bind(itemData)
            if (binding is ItemListenAnswerSkillBinding) {
                with(binding) {
                    setupInitialView()
                    itemData?.let { item ->
                        openSound(item.sound)
                        listTmp = listItem.toMutableList() as ArrayList
                        listTmp.sortedBy { it.id }
                        listTmp.remove(item)
                        listTmp.shuffle()
                        val answers =
                            if (isEn) arrayListOf(
                                item.wordRu,
                                listTmp[Constants.INDEX_0].wordRu
                            ) else arrayListOf(
                                item.wordEn,
                                listTmp[Constants.INDEX_0].wordEn
                            )
                        listTmp.add(item)
                        listTmp.sortedBy { it.id }
                        answers.shuffle()
                        setupViewData(answers)
                        setupClickListeners(item, answers, this)
                        listItem.toMutableList().add(item)
                        ivListenAnswer.click {
                            openSound(item.sound)
                        }
                        ivFeedBack.click {
                            event.invoke(adapterPosition, TypeClickSkill.FEED_BACK)
                        }
                        ivSound.click {
                            openSound(item.sound)
                        }
                    }
                }
            }
        }

        private fun ItemListenAnswerSkillBinding.setupClickListeners(
            item: DictEntity,
            answers: ArrayList<String>,
            binding: ItemListenAnswerSkillBinding
        ) {
            tvAnswerOne.setupClickView(item, answers[Constants.INDEX_0], binding)
            tvAnswerTwo.setupClickView(item, answers[Constants.INDEX_1], binding)
            btnNext.click { event.invoke(adapterPosition, TypeClickSkill.CLICK_NEXT) }
        }

        private fun TextView.setupClickView(
            item: DictEntity, answer: String, binding: ItemListenAnswerSkillBinding
        ) {
            click {
                val isCorrect = answer ==  item.wordEn
                if (isPlay) MediaManager.getInstance()?.playAssetSound(
                    itemView.context,
                    if (isCorrect) Constants.KEY_SOUND_CORRECT else Constants.KEY_SOUND_WRONG
                )
                updateResultView(isCorrect, item, binding)
                item.isLeaning = true
                item.isAnswer = isCorrect
                event.invoke(adapterPosition, TypeClickSkill.CLICK_ANSWER)
            }
        }

        private fun updateResultView(
            isCorrect: Boolean,
            item: DictEntity,
            binding: ItemListenAnswerSkillBinding
        ) {
            with(binding) {
                btnNext.visible()
                tvAnswerOne.gone()
                tvAnswerTwo.gone()
                constraintResult.visible()
                tvResult.text = String.format("%s - %s", item.wordEn, item.wordRu)
                tvTitleQuestion.text =
                    if (isCorrect) itemView.context.resources.getText(R.string.txt_true_answer) else itemView.context.resources.getText(
                        R.string.txt_right_answer
                    )
                ivSound.setImageResource(if (isCorrect) R.drawable.ic_sound else R.drawable.ic_sound_false)
                tvResult.setTextColor(itemView.context.getColor(if (isCorrect) R.color.blue_02 else R.color.red))
                ivFeedBack.setImageResource(if (isCorrect) R.drawable.ic_feedback else R.drawable.ic_feedback_false)
                tvTitleQuestion.setTextColor(itemView.context.getColor(if (isCorrect) R.color.blue_02 else R.color.red))
                constraintResult.setBackgroundResource(if (isCorrect) R.drawable.bg_result_true else R.drawable.bg_result_false)
            }
        }

        private fun ItemListenAnswerSkillBinding.setupViewData(answers: ArrayList<String>) {
            tvAnswerOne.text = answers[Constants.INDEX_0]
            tvAnswerTwo.text = answers[Constants.INDEX_1]
        }

        private fun ItemListenAnswerSkillBinding.setupInitialView() {
            btnNext.gone()
            tvAnswerOne.visible()
            tvAnswerTwo.visible()
            constraintResult.gone()
        }
    }

    inner class WriteAnswerViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<DictEntity>(binding) {
        override fun bind(itemData: DictEntity?) {
            super.bind(itemData)
            if (binding is ItemWriteAnswerSkillBinding) {
                val listWorldClick = StringBuilder("")
                binding.apply {
                    setupInitialView(listWorldClick)
                    itemData?.let { dict ->
                        setupViewData(dict)
                        setupKeyboardAdapter(listWorldClick, dict)
                        ivFeedBack.click {
                            event.invoke(adapterPosition, TypeClickSkill.FEED_BACK)
                        }
                        ivSound.click {
                            openSound(dict.sound)
                        }
                    }
                }
            }
        }

        private fun ItemWriteAnswerSkillBinding.setupInitialView(listWorldClick: StringBuilder) {
            tvCustomLineText.setTextReceived(listWorldClick.toString())
            constraintResult.gone()
            btnNext.isEnabled = false
        }

        private fun ItemWriteAnswerSkillBinding.setupViewData(dict: DictEntity) {
            tvWord.text = dict.wordRu
            tvCustomLineText.setText(dict.wordEn)
            btnNext.setTextColor(itemView.context.getColor(R.color.gray_02))
            btnNext.setBackgroundResource(R.drawable.bg_text_continue)
        }

        private fun ItemWriteAnswerSkillBinding.setupKeyboardAdapter(
            listWorldClick: StringBuilder,
            dict: DictEntity
        ) {
            keyboardAdapter = KeyboardAdapter(itemData?.wordEn!!.replace(" ", "")) { world ->
                listWorldClick.append(world)
                tvCustomLineText.setTextReceived(listWorldClick.toString())
                if (listWorldClick.toString() == dict.wordEn.replace(" ", "")) {
                    if (isPlay) MediaManager.getInstance()?.playWithPath(dict.sound)
                    binding.apply {
                        constraintResult.visible()
                        tvTitleQuestion.text =
                            itemView.context.resources.getText(R.string.txt_true_answer)
                        tvResult.text = String.format("%s - %s", dict.wordEn, dict.wordRu)
                        event.invoke(adapterPosition, TypeClickSkill.CLICK_ANSWER)
                        btnNext.setupViewAndActionButton()
                    }
                }
            }
            recyclerView.layoutManager = getLayoutManager(itemView.context)
            recyclerView.adapter = keyboardAdapter
            val listKeyboardModel = ArrayList<KeyboardModel>()
            Utils.charList.forEach { listKeyboardModel.add(KeyboardModel(it.toString())) }
            keyboardAdapter.submitList(listKeyboardModel)
        }

        private fun Button.setupViewAndActionButton() {
            apply {
                isEnabled = true
                setTextColor(itemView.context.getColor(R.color.white))
                setBackgroundResource(R.drawable.bg_text_view)
                click {
                    event.invoke(adapterPosition, TypeClickSkill.CLICK_NEXT)
                }
            }
        }
    }

    private fun getLayoutManager(cxt: Context): GridLayoutManager {
        val layoutManager = GridLayoutManager(cxt, Constants.INDEX_20)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return Utils.map[position % Utils.map.size]
            }
        }
        return layoutManager
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return when (viewType) {
            TYPE_WRITE_ANSWER -> R.layout.item_write_answer_skill
            TYPE_LISTEN_ANSWER -> R.layout.item_listen_answer_skill
            else -> R.layout.item_choose_answer_skill
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<DictEntity> {
        return when (viewType) {
            TYPE_WRITE_ANSWER -> WriteAnswerViewHolder(getViewHolderDataBinding(parent, viewType))
            TYPE_LISTEN_ANSWER -> ListenAnswerViewHolder(getViewHolderDataBinding(parent, viewType))
            else -> ChooseAnswerViewHolder(getViewHolderDataBinding(parent, viewType))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (viewType) {
            1 -> TYPE_CHOOSE_ANSWER
            3 -> TYPE_LISTEN_ANSWER
            else -> TYPE_WRITE_ANSWER
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateViewType(newViewType: Int) {
        viewType = newViewType
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTypeEnVi(isEn: Boolean) {
        this.isEn = isEn
        notifyDataSetChanged()
    }
}

enum class TypeClickSkill {
    CLICK_ANSWER, CLICK_NEXT, FEED_BACK
}