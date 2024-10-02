package com.adzenglish.adzenglish.ui.practice

import android.os.Bundle
import android.view.LayoutInflater
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentPracticeCheckBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.ui.practice.transplant.TransplantFromFrg
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.goneIf
import com.adzenglish.adzenglish.utils.extension.setInVisibility
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PracticeCheckFragment : BaseFragmentWithBinding<FragmentPracticeCheckBinding>() {
    private var listDict: List<DictEntity>? = null
    override fun init() {
        // do nothing
    }

    override fun initData() {
        arguments?.let { bundle ->
            val jsonString = bundle.getString(Constants.KEY_LIST_WIN)
            jsonString?.let {
                listDict = Gson().fromJson(jsonString, Array<DictEntity>::class.java).toList()
                updateUi(listDict)
            }
        }
    }

    private fun updateUi(listDict: List<DictEntity>?) {
        val boolean = listDict.isNullOrEmpty()
        binding.tvExit.setInVisibility(!boolean)
        binding.btnCheck.text =
            String.format(if (!boolean) "Kiểm tra thêm ${listDict?.size} từ vựng nữa" else "Thoát")
    }

    override fun initAction() {
        binding.tvExit.setOnClickListener { onBackPressed() }
        binding.btnCheck.setOnClickListener {
            if (listDict?.isEmpty() == false) {
                Bundle().apply {
                    val jsonString = Gson().toJson(listDict)
                    putString(Constants.KEY_TRANSPLANT_FROM, jsonString)
                    openFragment(TransplantFromFrg::class.java, this, true)
                }
            } else {
                onBackPressed()
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentPracticeCheckBinding {
        return FragmentPracticeCheckBinding.inflate(layoutInflater)
    }
}