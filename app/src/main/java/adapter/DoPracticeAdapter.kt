package adapter

import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.media.MediaManager
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.customview.CustomDottedUnderlineSpan
import com.adzenglish.adzenglish.databinding.ItemChooesAnswerBinding
import com.adzenglish.adzenglish.databinding.ItemDoPracticeWriteBinding
import com.adzenglish.adzenglish.databinding.ItemDropAnswerBinding
import com.adzenglish.adzenglish.models.local.room.entity.LessonQuestionEntity
import com.adzenglish.adzenglish.ui.learn.grammar.Listener
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.Utils
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.gone
import com.adzenglish.adzenglish.utils.extension.invisible
import com.adzenglish.adzenglish.utils.extension.onTextChanged
import com.adzenglish.adzenglish.utils.extension.visible
import com.adzenglish.adzenglish.viewmodel.ViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DoPracticeAdapter(
    val viewModel: ViewModel,
    val event: (Int) -> Unit
) :
    BaseRecyclerAdapter<LessonQuestionEntity, BaseViewHolder<LessonQuestionEntity>>() {
    companion object {
        const val TYPE_DROP = 1
        const val TYPE_CHOOSE = 2
        const val TYPE_WRITE = 3
    }

    inner class DoPracticeDropViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<LessonQuestionEntity>(binding), Listener {
        private val items2: MutableList<String> = mutableListOf()
        private var listWorkEn: ArrayList<String>? = null
        private var work: List<String>? = null
        override fun bind(itemData: LessonQuestionEntity?) {
            super.bind(itemData)
            if (binding is ItemDropAnswerBinding) {
                with(binding) {
                    setupInitialView()
                    itemData?.let { lessonQuestion ->
                        handlePhrase(lessonQuestion.task)
                    }
                    btnCheckAnswer.setupClickCheckAnswer(binding)
                    binding.layoutBtnNext.btnNext.setupClickNext(adapterPosition)
                }

            }
        }

        private fun Button.setupClickNext(position: Int) {
            click {
                event.invoke(position)
            }
        }

        private fun Button.setupClickCheckAnswer(
            binding: ItemDropAnswerBinding
        ) {
            click {
                val isCorrect = (work?.joinToString(" ") == itemData?.answer?.replace("+", " "))
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    MediaManager.getInstance()?.playAssetSound(
                        itemView.context,
                        if (isCorrect) Constants.KEY_SOUND_CORRECT else Constants.KEY_SOUND_WRONG
                    )
                }
                updateResultView(isCorrect, binding)
                updateBtnControl(binding)
            }
        }

        private fun updateBtnControl(binding: ItemDropAnswerBinding) {
            binding.btnCheckAnswer.gone()
            binding.layoutBtnNext.btnNext.visible()
        }

        private fun updateResultView(
            isCorrect: Boolean,
            binding: ItemDropAnswerBinding
        ) {
            with(binding.layoutStateAnswer) {
                constraintResult.visible()
                val colorRes = if (isCorrect) R.color.blue_02 else R.color.red
                val resultText =
                    if (isCorrect) itemView.context.resources.getText(R.string.txt_true_answer) else itemView.context.resources.getText(
                        R.string.txt_right_answer
                    )
                tvTitleQuestion.text = resultText
                tvTitleQuestion.setTextColor(itemView.context.getColor(colorRes))
                tvResult.setTextColor(itemView.context.getColor(colorRes))
                ivSound.setImageResource(if (isCorrect) R.drawable.ic_sound else R.drawable.ic_sound_false)
                tvResult.text = itemData?.answer?.replace("+", " ")
                ivFeedBack.setImageResource(if (isCorrect) R.drawable.ic_feedback else R.drawable.ic_feedback_false)
                constraintResult.setBackgroundResource(if (isCorrect) R.drawable.bg_result_true else R.drawable.bg_result_false)

            }
        }

        private fun ItemDropAnswerBinding.setupInitialView() {
            layoutStateAnswer.constraintResult.invisible()
            layoutBtnNext.btnNext.gone()
            btnCheckAnswer.apply {
                visible()
                text = itemView.context.resources.getText(R.string.txt_check_answer)
                setTextColor(itemView.context.getColor(R.color.gray_02))
                setBackgroundResource(R.drawable.bg_text_continue)
                isEnabled = false
            }
        }

        private fun ItemDropAnswerBinding.handlePhrase(input: String) {
            val listWorkVi = input.split("; ").map { it.split("/")[0] }
            val replacementMap = mapOf(
                Constants.KEY_DELETE_VALUE_PRACTICE to "",
                Constants.KEY_DELETE_VALUE_PRACTICE_2 to "are",
                Constants.KEY_DELETE_VALUE_PRACTICE_3 to "it",
                Constants.KEY_DELETE_VALUE_PRACTICE_4 to "I"
            )

            val cleanedInput = replacementMap.entries.fold(input) { acc, (key, value) ->
                if (acc.contains(key)) acc.replace(key, value) else acc
            }

            listWorkEn = cleanedInput.split("; ")
                .mapNotNull { it.split("/").getOrNull(1)?.split("-")?.getOrNull(0) }
                .toCollection(ArrayList())

            val outputWorkVi = listWorkVi.joinToString("-")
            val spannableStringBuilder = SpannableStringBuilder()
            val wordsVi = outputWorkVi.split("-")
            for (word in wordsVi) {
                val spannableString = SpannableString("$word ")
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(textView: View) {
                        Utils.showPopup(
                            tvDotedText, word, itemView.context, listWorkVi,
                            listWorkEn!!
                        )
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }
                spannableString.setSpan(
                    clickableSpan,
                    0,
                    word.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                val dottedUnderlineSpan =
                    CustomDottedUnderlineSpan(itemView.context, Color.GRAY, word)
                spannableString.setSpan(
                    dottedUnderlineSpan,
                    0,
                    word.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannableStringBuilder.append(spannableString)
            }

            tvDotedText.text = spannableStringBuilder
            tvDotedText.movementMethod = LinkMovementMethod.getInstance()
            tvDotedText.highlightColor = Color.TRANSPARENT
            layoutDropAnswer.recyclerView1.init(items2, JustifyContent.FLEX_START)
            itemData?.let { lessonQuestionEntity ->
                val inputs: List<String> =
                    StringBuilder(lessonQuestionEntity.answer.replace(";", "+")).append(
                        "+${
                            itemData?.wrong?.replace(
                                ";",
                                "+"
                            )
                        }"
                    )
                        .split("+").shuffled()
                layoutDropAnswer.recyclerView2.init(
                    inputs, JustifyContent.CENTER
                )
            }
        }

        private fun RecyclerView.init(list: List<String>, justifyContents: Int) {
            val layoutManager = FlexboxLayoutManager(itemView.context).apply {
                justifyContent = justifyContents
                alignItems = AlignItems.CENTER
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            this.layoutManager = layoutManager
            val adapter = TextAdapter(this@DoPracticeDropViewHolder)
            this.adapter = adapter
            this.setOnDragListener(adapter.dragInstance)
            adapter.submitList(list)

        }

        override fun setEmptyList(
            visibilityRecyclerview: Int,
            recyclerView: Int,
            works: List<String>
        ) {
            if (binding is ItemDropAnswerBinding) {
                binding.root.findViewById<RecyclerView>(recyclerView).visibility =
                    visibilityRecyclerview
                work = works
                if (work?.isNotEmpty() == true) {
                    binding.btnCheckAnswer.apply {
                        text = itemView.context.resources.getText(R.string.txt_check_answer)
                        setTextColor(itemView.context.getColor(R.color.white))
                        setBackgroundResource(R.drawable.bg_text_view)
                        isEnabled = true
                    }
                }
            }
        }
    }

    inner class DoPracticeChooseViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<LessonQuestionEntity>(binding) {
        private var listWorkEn: ArrayList<String>? = null
        override fun bind(itemData: LessonQuestionEntity?) {
            super.bind(itemData)
            if (binding is ItemChooesAnswerBinding) {
                with(binding) {
                    setupInitialView()
                    itemData?.let { lessonQuestion ->
                        handlePhrase(lessonQuestion.task)
                        val answers = getShuffledAnswers(lessonQuestion)
                        setupViewData(answers)
                        setupClickListeners(lessonQuestion, answers, this)
                    }
                }

            }
        }

        private fun ItemChooesAnswerBinding.handlePhrase(input: String) {
            val regexQuotesWorkVi = "\\(([^)]+)\\)".toRegex()
            val newInput = input.replace(regexQuotesWorkVi) {
                "${it.value}/..."
            }
            val listWorkVi = newInput.split("; ").map { it.split("/")[0] } as ArrayList<String>
            listWorkEn = newInput.split("; ") as ArrayList<String>
            val outputWorkVi = listWorkVi.joinToString(" ")
            val regex = "\\(.*?\\)".toRegex()
            val outputWorkViChanged = outputWorkVi.replace(regex, "...")
            val spannableStringBuilder = SpannableStringBuilder()
            val wordsVi = outputWorkViChanged.split(" ")
            for (word in wordsVi) {
                val spannableString = SpannableString("$word ")
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(textView: View) {
                        Utils.showPopup(
                            tvDotedText, word, itemView.context, wordsVi,
                            listWorkEn!!
                        )
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }
                spannableString.setSpan(
                    clickableSpan,
                    0,
                    word.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                val dottedUnderlineSpan =
                    CustomDottedUnderlineSpan(itemView.context, Color.GRAY, word)
                spannableString.setSpan(
                    dottedUnderlineSpan,
                    0,
                    word.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannableStringBuilder.append(spannableString)
            }

            tvDotedText.text = spannableStringBuilder
            tvDotedText.movementMethod = LinkMovementMethod.getInstance()
            tvDotedText.highlightColor = Color.TRANSPARENT
        }

        private fun getShuffledAnswers(item: LessonQuestionEntity): List<String> {
            return listOf(item.answer, item.wrong).shuffled()
        }

        private fun ItemChooesAnswerBinding.setupViewData(answers: List<String>) {
            layoutCheckAnswer.tvAnswerOne.text = answers[0]
            layoutCheckAnswer.tvAnswerTwo.text = answers[1]
        }

        private fun ItemChooesAnswerBinding.setupClickListeners(
            item: LessonQuestionEntity,
            answers: List<String>,
            binding: ItemChooesAnswerBinding
        ) {

            layoutCheckAnswer.tvAnswerOne.setupClickView(item, answers[0], binding)
            layoutCheckAnswer.tvAnswerTwo.setupClickView(item, answers[1], binding)
            layoutCheckAnswer.layoutBtnNext.btnNext.click {
                event.invoke(
                    adapterPosition
                )
            }
        }

        private fun ItemChooesAnswerBinding.setupInitialView() {
            with(layoutCheckAnswer) {
                layoutBtnNext.btnNext.gone()
                tvAnswerOne.visible()
                tvAnswerTwo.visible()
                layoutStateAnswer.constraintResult.gone()

            }
        }

        private fun TextView.setupClickView(
            item: LessonQuestionEntity,
            answer: String,
            binding: ItemChooesAnswerBinding
        ) {
            click {
                val isCorrect = (answer == item.answer)
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    MediaManager.getInstance()?.playAssetSound(
                        itemView.context,
                        if (isCorrect) Constants.KEY_SOUND_CORRECT else Constants.KEY_SOUND_WRONG
                    )
                }
                updateResultView(isCorrect, item, binding)
            }
        }

        private fun updateResultView(
            isCorrect: Boolean,
            item: LessonQuestionEntity,
            binding: ItemChooesAnswerBinding
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
                    tvResult.text = item.answer
                    ivFeedBack.setImageResource(if (isCorrect) R.drawable.ic_feedback else R.drawable.ic_feedback_false)
                    constraintResult.setBackgroundResource(if (isCorrect) R.drawable.bg_result_true else R.drawable.bg_result_false)
                }
            }
        }
    }

    inner class DoPracticeWriteViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<LessonQuestionEntity>(binding) {
        private var listWorkEn: ArrayList<String>? = null
        override fun bind(itemData: LessonQuestionEntity?) {
            super.bind(itemData)
            if (binding is ItemDoPracticeWriteBinding) {
                with(binding) {
                    setupInitialView()
                    itemData?.let {
                        handlePhrase(it.task)
                    }
                    binding.edtAnswer.onTextChanged { result ->
                        if (result.isNotEmpty()) {
                            binding.btnCheckAnswer.isEnabled = true
                            binding.btnCheckAnswer.apply {
                                text = itemView.context.resources.getText(R.string.txt_check_answer)
                                setTextColor(itemView.context.getColor(R.color.white))
                                setBackgroundResource(R.drawable.bg_text_view)
                                isEnabled = true
                                setupClickCheckAnswer(this@with, result)
                            }
                        }
                    }

                    binding.layoutBtnNext.btnNext.setupViewAndActionButton()
                }

            }
        }

        private fun Button.setupClickCheckAnswer(
            binding: ItemDoPracticeWriteBinding,
            result: String
        ) {
            click {
                val isCorrect = (result == itemData?.answer?.replace("+", " "))
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    MediaManager.getInstance()?.playAssetSound(
                        itemView.context,
                        if (isCorrect) Constants.KEY_SOUND_CORRECT else Constants.KEY_SOUND_WRONG
                    )
                }
                updateResultView(isCorrect, binding)
                updateBtnControl(binding)
            }
        }

        private fun updateBtnControl(binding: ItemDoPracticeWriteBinding) {
            binding.btnCheckAnswer.gone()
            binding.layoutBtnNext.btnNext.visible()
        }

        private fun ItemDoPracticeWriteBinding.handlePhrase(input: String) {
            val listWorkVi = input.split("; ").map { it.split("/")[0] }
            listWorkEn = input.split("; ").map { it.split("/")[1] }
                .map { it.split("-")[0] } as ArrayList<String>
            val outputWorkVi = listWorkVi.joinToString("-")
            val spannableStringBuilder = SpannableStringBuilder()
            val wordsVi = outputWorkVi.split("-")
            for (word in wordsVi) {
                val spannableString = SpannableString("$word ")
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(textView: View) {
                        Utils.showPopup(
                            tvDotedText, word, itemView.context, listWorkVi,
                            listWorkEn!!
                        )
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }
                spannableString.setSpan(
                    clickableSpan,
                    0,
                    word.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                val dottedUnderlineSpan =
                    CustomDottedUnderlineSpan(itemView.context, Color.GRAY, word)
                spannableString.setSpan(
                    dottedUnderlineSpan,
                    0,
                    word.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannableStringBuilder.append(spannableString)
            }

            tvDotedText.text = spannableStringBuilder
            tvDotedText.movementMethod = LinkMovementMethod.getInstance()
            tvDotedText.highlightColor = Color.TRANSPARENT
        }

        private fun ItemDoPracticeWriteBinding.setupInitialView() {
            layoutStateAnswer.constraintResult.invisible()
            layoutBtnNext.btnNext.gone()
            btnCheckAnswer.apply {
                visible()
                text = itemView.context.resources.getText(R.string.txt_check_answer)
                setTextColor(itemView.context.getColor(R.color.gray_02))
                setBackgroundResource(R.drawable.bg_text_continue)
                isEnabled = false
            }
        }

        private fun updateResultView(
            isCorrect: Boolean,
            binding: ItemDoPracticeWriteBinding
        ) {
            with(binding.layoutStateAnswer) {
                constraintResult.visible()
                val colorRes = if (isCorrect) R.color.blue_02 else R.color.red
                val resultText =
                    if (isCorrect) itemView.context.resources.getText(R.string.txt_true_answer) else itemView.context.resources.getText(
                        R.string.txt_right_answer
                    )
                tvTitleQuestion.text = resultText
                tvTitleQuestion.setTextColor(itemView.context.getColor(colorRes))
                tvResult.setTextColor(itemView.context.getColor(colorRes))
                ivSound.setImageResource(if (isCorrect) R.drawable.ic_sound else R.drawable.ic_sound_false)
                tvResult.text = itemData?.answer?.replace("+", " ")
                ivFeedBack.setImageResource(if (isCorrect) R.drawable.ic_feedback else R.drawable.ic_feedback_false)
                constraintResult.setBackgroundResource(if (isCorrect) R.drawable.bg_result_true else R.drawable.bg_result_false)

            }
        }

        private fun Button.setupViewAndActionButton() {
            apply {
                click {
                    event.invoke(adapterPosition)
                }
            }
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return when (viewType) {
            TYPE_DROP -> R.layout.item_drop_answer
            TYPE_CHOOSE -> R.layout.item_chooes_answer
            TYPE_WRITE -> R.layout.item_do_practice_write
            else -> {
                throw Exception("Error reading holder type")
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<LessonQuestionEntity> {
        return when (viewType) {
            TYPE_DROP -> DoPracticeDropViewHolder(getViewHolderDataBinding(parent, viewType))
            TYPE_CHOOSE -> DoPracticeChooseViewHolder(getViewHolderDataBinding(parent, viewType))
            TYPE_WRITE -> DoPracticeWriteViewHolder(getViewHolderDataBinding(parent, viewType))
            else -> {
                throw Exception("Error set holder type")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listItem[position].type) {
            0 -> TYPE_CHOOSE
            1 -> TYPE_DROP
            else -> TYPE_WRITE
        }
    }

}