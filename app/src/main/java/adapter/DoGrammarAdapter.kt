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
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.media.MediaManager
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.customview.CustomDottedUnderlineSpan
import com.adzenglish.adzenglish.databinding.ItemDropAnswerBinding
import com.adzenglish.adzenglish.models.local.room.entity.QuestionsEntity
import com.adzenglish.adzenglish.ui.learn.grammar.Listener
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.Utils
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.invisible
import com.adzenglish.adzenglish.utils.extension.visible
import com.adzenglish.adzenglish.viewmodel.ViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DoGrammarAdapter(
    val viewModel: ViewModel,
    val event: (Int) -> Unit,
    val eventFeedback: () -> Unit
) : BaseRecyclerAdapter<QuestionsEntity, DoGrammarAdapter.DoGrammarViewHolder>() {
    inner class DoGrammarViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<QuestionsEntity>(binding), Listener {
        private var listWorkEn: ArrayList<String>? = null
        private val items2: MutableList<String> = mutableListOf()
        private var work: List<String>? = null
        private var answer: String? = null
        override fun bind(itemData: QuestionsEntity?) {
            super.bind(itemData)
            if (binding is ItemDropAnswerBinding) {
                with(binding) {
                    setupInitialView()
                    if (itemData != null) {
                        handlePhrase(itemData)
                    }
                    btnCheckAnswer.setupClickCheckAnswer(binding)
                    binding.layoutBtnNext.btnNext.setupClickNext(adapterPosition)
                }
            }
        }

        private fun ItemDropAnswerBinding.setupInitialView() {
            layoutStateAnswer.constraintResult.invisible()
            btnCheckAnswer.apply {
                text = itemView.context.resources.getText(R.string.txt_check_answer)
                setTextColor(itemView.context.getColor(R.color.gray_02))
                setBackgroundResource(R.drawable.bg_text_continue)
                isEnabled = false
            }
        }

        private fun ItemDropAnswerBinding.handlePhrase(questionsEntity: QuestionsEntity) {
            val listWorkVi: List<String>
            if (questionsEntity.task.contains("#") && questionsEntity.task.contains("*") && questionsEntity.task.contains(
                    Constants.KEY_DELETE_VALUE_GRAMMAR
                )
            ) {
                listWorkVi =
                    questionsEntity.task.replace("*", "").replace("#", " ").replace(Constants.KEY_DELETE_VALUE_GRAMMAR, "")
                        .split("; ").map { it.split("/")[0] }
                listWorkEn =
                    questionsEntity.task.replace(Constants.KEY_DELETE_VALUE_GRAMMAR, "").replace("*", "").replace("#", " ")
                        .split("; ").map { it.split("/")[1] }
                        .map { it.split("-")[0] } as ArrayList<String>
            } else if (questionsEntity.task.contains("#") && questionsEntity.task.contains("*")) {
                listWorkVi = questionsEntity.task.replace("*", "").replace("#", " ").split("; ")
                    .map { it.split("/")[0] }
                listWorkEn = questionsEntity.task.replace("*", "").replace("#", " ").split("; ")
                    .map { it.split("/")[1] }
                    .map { it.split("-")[0] } as ArrayList<String>
            } else {
                listWorkVi = questionsEntity.task.split("; ").map { it.split("/")[0] }
                listWorkEn =
                    questionsEntity.task.replace(Constants.KEY_DELETE_VALUE_GRAMMAR, "").split("; ").map { it.split("/")[1] }
                            as ArrayList<String>
            }

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
            answer = questionsEntity.answer.replace(";", "+").split("+")
                .joinToString(" ") { it.split("/")[0] }
            val inputs: List<String> =
                StringBuilder(
                    answer.toString()
                ).append(
                    " ${itemData?.wrong?.replace(";", " ")}"
                )
                    .split(" ")
            layoutDropAnswer.recyclerView2.init(
                inputs.shuffled(),
                JustifyContent.CENTER
            )
        }

        private fun RecyclerView.init(list: List<String>, justifyContents: Int) {
            val layoutManager = FlexboxLayoutManager(itemView.context).apply {
                justifyContent = justifyContents
                alignItems = AlignItems.CENTER
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            this.layoutManager = layoutManager
            val adapter = TextAdapter(this@DoGrammarViewHolder)
            this.adapter = adapter
            this.setOnDragListener(adapter.dragInstance)
            adapter.submitList(list)
        }

        private fun Button.setupClickCheckAnswer(
            binding: ItemDropAnswerBinding
        ) {
            click {
                val isCorrect = (work?.joinToString(" ") == answer)
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

        private fun Button.setupClickNext(position: Int) {
            click {
                event.invoke(position)
            }
        }

        private fun updateBtnControl(binding: ItemDropAnswerBinding) {
            binding.btnCheckAnswer.invisible()
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
                ivSound.setOnClickListener {
                    val sound =
                        itemData?.id?.let { it1 -> viewModel.getQuestSoundByQuesId(it1) }?.url
                    if (sound != null) {
                        MediaManager.getInstance()?.playWithPath(sound)
                    }
                }
                tvResult.text = answer
                ivFeedBack.setImageResource(if (isCorrect) R.drawable.ic_feedback else R.drawable.ic_feedback_false)
                ivFeedBack.setOnClickListener {
                    eventFeedback.invoke()
                }
                constraintResult.setBackgroundResource(if (isCorrect) R.drawable.bg_result_true else R.drawable.bg_result_false)
            }
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

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_drop_answer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoGrammarViewHolder {
        return DoGrammarViewHolder(getViewHolderDataBinding(parent, viewType))
    }

}