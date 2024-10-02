package com.adzenglish.adzenglish.ui.practice.transplant

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentTransplantFromBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.practice.PracticeCheckFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.visible
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TransplantFromFrg : BaseFragmentWithBinding<FragmentTransplantFromBinding>() {
    private var type: String? = null
    private var sumDict = Constants.INDEX_0
    private var totalDict = Constants.INDEX_0
    private var adapterEn: WordEnAdapter? = null
    private var adapterRu: WordRuAdapter? = null
    private var listDictTmp: List<DictEntity> = arrayListOf()
    private var listDictRemaining: List<DictEntity> = arrayListOf()
    private var listDictSelect: ArrayList<DictEntity> = arrayListOf()

    @Inject
    lateinit var preferences: Preferences

    override fun init() {
        adapterRu = WordRuAdapter {
            type = Constants.RU
            checkAnswer()
        }
        val isPlay = preferences.getBoolean(Constants.KEY_OPEN_SOUND)
        adapterEn = WordEnAdapter(isPlay) {
            type = Constants.EN
            checkAnswer()
        }
        binding.rcvEn.adapter = adapterEn
        binding.rcvRu.adapter = adapterRu
    }

    private fun checkAnswer() {
        if (adapterRu?.dictRu == null || adapterEn?.dictEn == null) return
        val isAnswer = adapterRu?.dictRu?.id == adapterEn?.dictEn?.id
        adapterEn?.isAnswerEn = isAnswer
        adapterRu?.isAnswerRu = isAnswer
        if (adapterEn?.dictEn?.form2?.isEmpty() == true) {
            adapterEn?.dictEn?.form2 = isAnswer.toString()
            adapterEn?.dictEn?.let { listDictSelect.add(it) }
        }
        when (isAnswer) {
            true -> rightAnswer()
            false -> wrongAnswer()
        }
        notifyData()
    }

    private fun wrongAnswer() {
        setOnClick(false)
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            if (type == Constants.EN) {
                adapterEn?.dictEn = null
                adapterEn?.id = null
            } else {
                adapterRu?.dictRu = null
                adapterRu?.id = null
            }
            adapterRu?.isAnswerRu = null
            adapterEn?.isAnswerEn = null
            notifyData()
            setOnClick(true)
        }, 300)
    }

    private fun rightAnswer() {
        totalDict += Constants.INDEX_1
        setOnClick(false)
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            adapterEn?.dictEn?.id?.let {
                adapterEn?.listSelected?.add(it)
            }
            adapterRu?.dictRu?.id?.let {
                adapterRu?.listSelected?.add(it)
            }
            adapterEn?.dictEn = null
            adapterRu?.dictRu = null
            adapterEn?.id = null
            adapterRu?.id = null
            adapterEn?.isAnswerEn = null
            adapterRu?.isAnswerRu = null
            binding.progressBar.progress += Constants.INDEX_1
            notifyData()
            if (totalDict == sumDict) {
                binding.tvSoGood.visible()
                binding.btnContinue.setTextColor(Color.WHITE)
                binding.btnContinue.setBackgroundResource(R.drawable.bg_text_view)
            }
            if (totalDict % Constants.INDEX_5 == Constants.INDEX_0) {
                nextQuestion()
            }
            setOnClick(true)
        }, 300)
    }

    private fun setOnClick(isClick: Boolean) {
        adapterRu?.isClick = isClick
        adapterEn?.isClick = isClick
    }

    private fun nextQuestion() {
        val list =
            if (listDictTmp.size < Constants.INDEX_5) listDictTmp else listDictTmp.take(Constants.INDEX_5)
        if (listDictTmp.size > Constants.INDEX_5) listDictTmp =
            listDictTmp.subList(Constants.INDEX_5, listDictTmp.size)
        setupAdapter(list)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyData() {
        adapterEn?.notifyDataSetChanged()
        adapterRu?.notifyDataSetChanged()
    }

    override fun initData() {
        arguments?.let { bundle ->
            val jsonString = bundle.getString(Constants.KEY_TRANSPLANT_FROM)
            jsonString?.let {
                val gson = Gson()
                val listDict = gson.fromJson(jsonString, Array<DictEntity>::class.java).toList()
                    .sortedBy { it.id }
                if (listDict.size > Constants.INDEX_25) {
                    listDictTmp = listDict.take(Constants.INDEX_25)
                    listDictRemaining = listDict.subList(Constants.INDEX_25, listDict.size)
                } else {
                    listDictTmp = listDict
                }
                sumDict = listDictTmp.size
                binding.progressBar.max = sumDict
                if (listDictTmp.size <= Constants.INDEX_5) {
                    setupAdapter(listDictTmp)
                } else {
                    setupAdapter(listDictTmp.take(Constants.INDEX_5))
                    listDictTmp = listDictTmp.subList(Constants.INDEX_5, listDictTmp.size)
                }
            }
        }
    }

    private fun setupAdapter(listDict: List<DictEntity>) {
        adapterRu?.submitList(listDict)
        adapterEn?.submitList(listDict.shuffled())
    }

    override fun initAction() {
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.btnContinue.setOnClickListener {
            nextFragment()
        }
    }

    private fun nextFragment() {
        if (listDictSelect.size == sumDict) {
            Bundle().apply {
                val gson = Gson()
                val jsonString = gson.toJson(listDictRemaining)
                putString(Constants.KEY_LIST_WIN, jsonString)
                openFragment(PracticeCheckFragment::class.java, this, true)
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentTransplantFromBinding {
        return FragmentTransplantFromBinding.inflate(layoutInflater)
    }
}