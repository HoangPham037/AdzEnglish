package com.adzenglish.adzenglish.ui.learn.grammar

import adapter.DoGrammarRuleAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentDoGrammarBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.learn.result.ResultFragment
import com.adzenglish.adzenglish.ui.topic.box.BottomSheetDialogFrg
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import com.adzenglish.adzenglish.viewmodel.DoGrammarVM
import com.adzenglish.adzenglish.viewmodel.MainActivityVM
import com.adzenglish.adzenglish.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class DoGrammarFragment : BaseFragmentWithBinding<FragmentDoGrammarBinding>() {
    private val viewModel: ViewModel by viewModels()
    private val doGrammarVM: DoGrammarVM by viewModels()
    private val mainActivityVM: MainActivityVM by activityViewModels()
    private var doGrammarRuleAdapter: DoGrammarRuleAdapter? = null
    private var dialog: BottomSheetDialogFrg? = null
    private val handlerTimer = Handler(Looper.getMainLooper())
    private var startTime: Long = 0L
    private var resultTime: String? = "00:00"
    var test = 0
    @Inject
    lateinit var preferences: Preferences

    private val updateTimeTask = object : Runnable {
        override fun run() {
            val elapsedTime = System.currentTimeMillis() - startTime
            val seconds = (elapsedTime / 1000).toInt() % 60
            val minutes = (elapsedTime / 1000).toInt() / 60
            resultTime = String.format("$minutes:$seconds", minutes, seconds)
            handlerTimer.postDelayed(this, 1000)
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentDoGrammarBinding {
        return FragmentDoGrammarBinding.inflate(layoutInflater)
    }

    override fun init() {
        dialog =  BottomSheetDialogFrg { boolean ->
            feedBack(boolean)
        }
        val lessonId = preferences.getInt(Constants.KEY_LESSON_ID)
        startTime = System.currentTimeMillis()
        handlerTimer.post(updateTimeTask)
        doGrammarVM.progress.observe(viewLifecycleOwner) {
            binding.layoutProgress.progressBar.progress = it
        }
        var index = 0
        doGrammarRuleAdapter =
            DoGrammarRuleAdapter(viewModel, { pos, posRule, sumItem ->
                index ++
                doGrammarVM.setProgress(index)
                if (index < test) {
                    if (sumItem - 1 == pos) {
                        binding.recyclerViewRule.smoothScrollToPosition(posRule + 1)
                    }
                } else {
                    mainActivityVM.setNavigateFragment(Constants.INDEX_2)
                    viewModel.ruleByLessonId.value?.data?.let { listRule ->
                        listRule.forEach { rule ->
                            rule.isLearning = true
                            rule.isStudying = true
                            rule.createdAt = viewModel.getDate()
                            viewModel.updateRule(rule)
                        }
                    }
                    Bundle().apply {
                        putString(Constants.KEY_TOTAL_TIME, resultTime)
                        putString(Constants.KEY_COMPLETION, "100%")
                        putString(Constants.KEY_TOTAL_RIGHT_ANSWER, "Tuyệt")
                        putString(Constants.KEY_TYPE_FROM, Constants.KEY_FROM_DO_GRAMMAR)
                        openFragment(ResultFragment::class.java, this, true)
                    }
                }
            }, {
                dialog?.show(childFragmentManager, DoGrammarFragment::class.java.name)
            })
        binding.recyclerViewRule.setHasFixedSize(true)
        binding.recyclerViewRule.adapter = doGrammarRuleAdapter

        viewModel.ruleByLessonId.observe(viewLifecycleOwner) { dataStatus ->
            when (dataStatus.status) {
                DataStatus.Status.SUCCESS -> {
                    doGrammarRuleAdapter?.submitList(dataStatus.data)
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        var totalQuestionSize = 0
                        dataStatus.data?.forEach { rule ->
                            val listQuestionSize = viewModel.repository.getQuestionByRuleId(rule.id)
                            totalQuestionSize+= listQuestionSize.size
                            test += listQuestionSize.size
                        }
                        withContext(Dispatchers.Main) {
                            binding.layoutProgress.progressBar.max = totalQuestionSize
                        }
                    }
                }
                DataStatus.Status.ERROR -> toast("Error: ${dataStatus.message.toString()}")
            }
        }
        viewModel.getRuleByLessonId(lessonId)

    }

    override fun initData() {
    }

    override fun initAction() {
        binding.layoutToolBar.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun feedBack(boolean: Boolean) {
        if (boolean) toast(requireContext().getString(R.string.txt_feed_back_error))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        doGrammarRuleAdapter = null
        handlerTimer.removeCallbacks(updateTimeTask)
        dialog = null
    }
}
