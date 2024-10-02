package com.adzenglish.adzenglish.ui.practice.skill

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentSkillBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.notification.ReusableDialog
import com.adzenglish.adzenglish.ui.topic.box.BottomSheetDialogFrg
import com.adzenglish.adzenglish.ui.topic.box.BoxThreeFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.showNotifyInternet
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SkillFragment : BaseFragmentWithBinding<FragmentSkillBinding>() {
    private var type = 1
    private var isEn = true
    private var startTime: Long = 0L
    private var typeClickAnswer: Int = Constants.INDEX_0
    private var resultTime: String? = ""
    private var adapter: SkillAdapter? = null
    private var dialog: BottomSheetDialogFrg? = null
    private var handlerTimer = Handler(Looper.getMainLooper())

    @Inject
    lateinit var preferences: Preferences
    private val updateTimeTask = object : Runnable {
        override fun run() {
            val elapsedTime = System.currentTimeMillis() - startTime
            val seconds = (elapsedTime / 1000).toInt() % 60
            val minutes = (elapsedTime / 1000).toInt() / 60
            resultTime = String.format("$minutes:$seconds")
            handlerTimer.postDelayed(this, 1000)
        }
    }

    override fun init() {
        startTime = System.currentTimeMillis()
        handlerTimer.post(updateTimeTask)
        initAdapter()
        dialog = BottomSheetDialogFrg { boolean ->
            feedBack(boolean)
        }
        if (!isNetworkAvailable()) openDialogInternet()
    }

    private fun initAdapter() {
        val isPlay = preferences.getBoolean(Constants.KEY_OPEN_SOUND)
        adapter = SkillAdapter(isPlay) { pos, typeClick ->
            when (typeClick) {
                TypeClickSkill.CLICK_ANSWER -> {
                    typeClickAnswer = Constants.INDEX_1
                    binding.progressBar.progress = pos + Constants.INDEX_1
                }

                TypeClickSkill.CLICK_NEXT -> adapter?.listItem?.let { list ->
                    if (pos < list.size - Constants.INDEX_1) {
                        typeClickAnswer = Constants.INDEX_0
                        binding.rcvDoExercise.scrollToPosition(pos + Constants.INDEX_1)
                    } else {
                        navigateResultFragment(list)
                    }
                }

                TypeClickSkill.FEED_BACK -> dialog?.show(
                    childFragmentManager,
                    BoxThreeFragment::class.java.name
                )
            }
        }
        binding.rcvDoExercise.adapter = adapter
    }

    private fun openDialogInternet() {
      requireContext().showNotifyInternet()
    }

    private fun navigateResultFragment(listItem: List<DictEntity>) {
        handlerTimer.removeCallbacks(updateTimeTask)
        Bundle().apply {
            val gson = Gson()
            val jsonString = gson.toJson(listItem)
            putString(Constants.KET_LIST_DICT, jsonString)
            putString(Constants.KEY_TOTAL_TIME, resultTime)
            openFragment(ResultSkillFragment::class.java, this, true)
        }
    }

    private fun feedBack(boolean: Boolean) {
        if (boolean) ReusableDialog(
            requireContext(),
            requireContext().getString(R.string.txt_feed_back_error),
            R.drawable.ic_send_feed_back
        ).showDialog()
    }

    override fun initData() {
        arguments?.let { bundle ->
            val jsonString = bundle.getString(Constants.KEY_LIST_DICT_SKILL)
            jsonString?.let {
                val gson = Gson()
                val listDict = gson.fromJson(jsonString, Array<DictEntity>::class.java).toList()
                binding.progressBar.max = listDict.size
                adapter?.submitList(listDict.sortedBy { it.id })
            }
        }
    }

    override fun initAction() {
        binding.tbViEn.setOnClickListener {
            setDataQuestion()
        }
        binding.ivChoose.setOnClickListener {
            updateUi(Constants.INDEX_1, binding.ivChoose)
        }
        binding.ivWrite.setOnClickListener {
            updateUi(Constants.INDEX_2, binding.ivWrite)
        }
        binding.ivListen.setOnClickListener {
            if (!isNetworkAvailable()) openDialogInternet()
            updateUi(Constants.INDEX_3, binding.ivListen)
        }
    }

    private fun updateUi(index: Int, imageView: ImageView) {
        if (index == type || typeClickAnswer == Constants.INDEX_1) return
        type = index
        adapter?.updateViewType(type)
        binding.ivChoose.setImageResource(R.drawable.ic_choose_false)
        binding.ivWrite.setBackgroundResource(R.drawable.bg_vi_false)
        binding.ivChoose.setBackgroundResource(R.drawable.bg_vi_false)
        binding.ivListen.setBackgroundResource(R.drawable.bg_vi_false)
        binding.ivWrite.setImageResource(R.drawable.ic_keyboard_false)
        binding.ivListen.setImageResource(R.drawable.ic_sound_false_skill)
        imageView.setBackgroundResource(R.drawable.bg_vi_true)
        when (type) {
            Constants.INDEX_1 -> {
                typeClickAnswer = Constants.INDEX_0
                binding.ivChoose.setImageResource(R.drawable.ic_choose_true)
            }

            Constants.INDEX_2 -> {
                typeClickAnswer = Constants.INDEX_2
                binding.ivWrite.setImageResource(R.drawable.ic_keyboard_true)
            }

            Constants.INDEX_3 -> {
                typeClickAnswer = Constants.INDEX_2
                binding.ivListen.setImageResource(R.drawable.ic_sound_true_skill)
            }
        }
    }

    private fun setDataQuestion() {
        if (typeClickAnswer == Constants.INDEX_1 || typeClickAnswer == Constants.INDEX_2) return
        isEn = !isEn
        adapter?.setTypeEnVi(isEn)
        binding.tvEn.setBackgroundResource(if (isEn) R.drawable.bg_vi_true else R.drawable.bg_vi_false)
        binding.tvVi.setBackgroundResource(if (isEn) R.drawable.bg_vi_false else R.drawable.bg_vi_true)
        binding.tvEn.setTextColor(requireContext().getColor(if (isEn) R.color.blue_02 else R.color.gray_02))
        binding.tvVi.setTextColor(requireContext().getColor(if (isEn) R.color.gray_02 else R.color.blue_02))
    }

    override fun onDestroy() {
        handlerTimer.removeCallbacks(updateTimeTask)
        dialog = null
        super.onDestroy()
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentSkillBinding {
        return FragmentSkillBinding.inflate(layoutInflater)
    }
}