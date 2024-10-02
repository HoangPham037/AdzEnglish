package com.adzenglish.adzenglish.ui.topic.box

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentBoxSixBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.learn.result.ResultFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.viewmodel.TopicViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BoxSixFragment : BaseFragmentWithBinding<FragmentBoxSixBinding>() {
    private val viewModel: TopicViewModel by viewModels()
    private var adapter: BoxAdapter? = null
    private var dictEntities: List<DictEntity>? = null
    private var totalTime: String? = "00:00"
    private var completionRate: String? = ""
    private var typeFrom: String? = ""
    private var totalRightAnswer: String? = ""

    @Inject
    lateinit var preferences: Preferences
    override fun getViewBinding(inflater: LayoutInflater): FragmentBoxSixBinding {
        return FragmentBoxSixBinding.inflate(layoutInflater)
    }

    override fun init() {
        adapter = BoxAdapter {
            adapter?.listItem?.let { updateUi(it) }
        }
        binding.rcvWordNew.adapter = adapter
    }

    override fun initData() {
        arguments?.let { bundle ->
            val jsonString = bundle.getString(Constants.KET_LIST_DICT)
            totalTime = bundle.getString(Constants.KEY_TOTAL_TIME).toString()
            completionRate = bundle.getString(Constants.KEY_COMPLETION).toString()
            typeFrom = bundle.getString(Constants.KEY_TYPE_FROM).toString()
            totalRightAnswer = bundle.getString(Constants.KEY_TOTAL_RIGHT_ANSWER)
            dictEntities = Gson().fromJson(jsonString, Array<DictEntity>::class.java).toList().sortedBy { it.isAnswer }
            dictEntities?.let { listDict ->
                adapter?.submitList(listDict)
                updateUi(listDict)
                viewModel.updateDictToRoomByLearning(listDict)
            }
        }
    }

    private fun updateUi(dictEntities: List<DictEntity>) {
        val totalWord = viewModel.getTotalWord(dictEntities)
        binding.btnAddFrom.text =
            if (totalWord == Constants.INDEX_0) resources.getString(R.string.txt_not_add_word) else String.format(
                "Thêm $totalWord từ"
            )
        binding.tvAddAll.text =
            resources.getString(if (totalWord == dictEntities.size) R.string.txt_close_select else R.string.txt_select_all)
    }

    override fun initAction() {
        binding.tvAddAll.setOnClickListener {
            selectWord()
        }
        binding.btnAddFrom.setOnClickListener {
            addWordToRoom()
        }
    }

    private fun selectWord() {
        adapter?.listItem?.let { listWord ->
            when (viewModel.getTotalWord(listWord)) {
                listWord.size -> selectWord(true, listWord)
                else -> selectWord(false, listWord)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun selectWord(boolean: Boolean, list: List<DictEntity>) {
        list.forEach { item ->
            item.isAnswer = boolean
        }
        updateUi(list)
        adapter?.notifyDataSetChanged()
    }

    private fun addWordToRoom() {
        adapter?.listItem?.let {
            if (typeFrom == Constants.KEY_FROM_DO_VOCABULARY) {
                navigateToScreen(it)
            } else {
                when (viewModel.getTotalWord(it)) {
                    Constants.INDEX_0 -> onBackPressed()
                    else -> navigateToScreen(it)
                }
            }
        }
    }

    private fun navigateToScreen(list: List<DictEntity>) {
        if (typeFrom == Constants.KEY_FROM_DO_VOCABULARY) {
            Bundle().apply {
                val jsonString = Gson().toJson(list)
                putString(Constants.KET_LIST_DICT, jsonString)
                putString(Constants.KEY_TOTAL_TIME, totalTime)
                putString(Constants.KEY_COMPLETION, completionRate)
                putString(Constants.KEY_TOTAL_RIGHT_ANSWER, totalRightAnswer)
                putString(Constants.KEY_TYPE_FROM, Constants.KEY_FROM_DO_VOCABULARY)
                openFragment(ResultFragment::class.java, this, true)
            }
        } else {
            Bundle().apply {
                val jsonString = Gson().toJson(list)
                putString(Constants.KET_LIST_DICT, jsonString)
                openFragment(BoxEightFragment::class.java, this, true)
            }
        }
    }
}

