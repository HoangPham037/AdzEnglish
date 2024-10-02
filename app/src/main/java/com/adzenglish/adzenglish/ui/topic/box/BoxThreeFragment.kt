package com.adzenglish.adzenglish.ui.topic.box

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.base.media.MediaManager
import com.adzenglish.adzenglish.databinding.FragmentBoxThreeBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.notification.ReusableDialog
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.gone
import com.adzenglish.adzenglish.utils.extension.goneIf
import com.adzenglish.adzenglish.utils.extension.showNotifyInternet
import com.adzenglish.adzenglish.viewmodel.TopicViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BoxThreeFragment : BaseFragmentWithBinding<FragmentBoxThreeBinding>() {
    @Inject
    lateinit var preferences: Preferences
    private var isAnswer = false
    private var item: DictEntity? = null
    private var index = Constants.INDEX_0
    private var dialog: BottomSheetDialogFrg? = null
    private val viewModel: TopicViewModel by viewModels()
    private var listItem: List<DictEntity> = arrayListOf()
    private var listTmp: ArrayList<DictEntity> = arrayListOf()


    override fun init() {
        viewModel.listDict.observe(viewLifecycleOwner) {
            listItem = it.sortedBy { item -> item.id }
            listTmp = it as ArrayList
            index = Constants.INDEX_0
            learning(index)
            binding.progressBar.progress = Constants.INDEX_0
        }
        if (!isNetworkAvailable()) showDialogInternet()
    }

    private fun showDialogInternet() {
       requireContext().showNotifyInternet()
    }

    private fun learning(index: Int) {
        updateUi(true)
        listItem.sortedBy { it.id }
        item = listItem[index]
        listTmp.remove(item)
        listTmp.shuffle()
        val listAnswer = arrayListOf(item, listTmp[Constants.INDEX_0])
        listAnswer.shuffle()
        item?.let {
            listTmp.add(index, it)
            val isPlay = preferences.getBoolean(Constants.KEY_OPEN_SOUND)
            if (isPlay) MediaManager.getInstance()?.playWithPath(it.sound)
        }
        binding.tvQuestion.text = item?.wordEn
        binding.tvAnswerOne.text = listAnswer[Constants.INDEX_0]?.wordRu
        binding.tvAnswerTwo.text = listAnswer[Constants.INDEX_1]?.wordRu
    }

    override fun initData() {
        arguments?.let {
            viewModel.getListDictById(it.getInt(Constants.KEY_WORD_THEME))
        }
        dialog = BottomSheetDialogFrg { boolean ->
            feedBack(boolean)
        }
    }

    private fun feedBack(boolean: Boolean) {
        if (boolean) {
            ReusableDialog(
                requireContext(),
                requireContext().getString(R.string.txt_feed_back_error),
                R.drawable.ic_send_feed_back
            ).showDialog()
        }
    }

    override fun initAction() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnNext.setOnClickListener {
            if (index == listTmp.size) showFragment() else {
                binding.constraintLayout.animate()
                    .translationX(if (isAnswer) -1000f else 1000f)
                    .setDuration(300)
                    .start()
                updateUi(true)
                binding.tvAnswerOne.gone()
                binding.tvAnswerTwo.gone()
                binding.constraintResult.gone()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.constraintLayout.animate()
                        .translationX(0f)
                        .setDuration(0)
                        .start()
                    learning(index)
                }, 300)
            }
        }
        binding.tvAnswerOne.setOnClickListener {
            checkAnswer(binding.tvAnswerOne.text.toString())
        }
        binding.tvAnswerTwo.setOnClickListener {
            checkAnswer(binding.tvAnswerTwo.text.toString())
        }
        binding.ivSound.setOnClickListener {
            item?.let {
                if (!isNetworkAvailable()) showDialogInternet() else
                    MediaManager.getInstance()?.playWithPath(it.sound)
            }
        }
        binding.ivFeedBack.setOnClickListener {
            dialog?.show(childFragmentManager, BoxThreeFragment::class.java.name)
        }
    }

    private fun showFragment() {
        Bundle().apply {
            val gson = Gson()
            val jsonString = gson.toJson(listItem)
            putString(Constants.KET_LIST_DICT, jsonString)
            putString(Constants.KEY_TYPE_FROM, Constants.KEY_FROM_BOX_THREE)
            openFragment(BoxSixFragment::class.java, this, true)
        }
    }

    private fun checkAnswer(answer: String) {
        updateUi(false)
        index++
        binding.progressBar.progress = index
        item?.let {
            it.isAnswer = viewModel.checkAnswer(answer, it)
            isAnswer = it.isAnswer
            it.isLeaning = true
            binding.tvTitle.text = if (it.isAnswer) "Chính xác" else "Câu trả lời đúng"
            binding.tvTitle.setTextColor(requireContext().getColor(if (it.isAnswer) R.color.blue_02 else R.color.red))
            binding.tvResult.setTextColor(requireContext().getColor(if (it.isAnswer) R.color.blue_02 else R.color.red))
            binding.ivSound.setImageResource(if (it.isAnswer) R.drawable.ic_sound else R.drawable.ic_sound_false)
            binding.tvResult.text = String.format("${item?.wordEn} - ${item?.wordRu}")
            binding.ivFeedBack.setImageResource(if (it.isAnswer) R.drawable.ic_feedback else R.drawable.ic_feedback_false)
            binding.constraintResult.setBackgroundResource(if (it.isAnswer) R.drawable.bg_result_true else R.drawable.bg_result_false)
        }
    }

    private fun updateUi(boolean: Boolean) {
        binding.btnNext.goneIf(boolean)
        binding.tvAnswerOne.goneIf(!boolean)
        binding.tvAnswerTwo.goneIf(!boolean)
        binding.constraintResult.goneIf(boolean)
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentBoxThreeBinding {
        return FragmentBoxThreeBinding.inflate(layoutInflater)
    }

    override fun onDestroyView() {
        dialog = null
        super.onDestroyView()
    }
}