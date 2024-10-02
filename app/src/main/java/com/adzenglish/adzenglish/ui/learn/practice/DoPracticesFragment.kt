package com.adzenglish.adzenglish.ui.learn.practice

import adapter.DoPracticeAdapter
import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentDoPracticesBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.learn.grammar.DoGrammarFragment
import com.adzenglish.adzenglish.ui.learn.quicktask.IntroQuickTaskFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DoPracticesFragment : BaseFragmentWithBinding<FragmentDoPracticesBinding>() {
    private lateinit var doPracticeAdapter: DoPracticeAdapter
    private val viewModel: ViewModel by viewModels()

    @Inject
    lateinit var preferences: Preferences
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragmentManager = activity?.supportFragmentManager
        val fragment = fragmentManager?.findFragmentByTag(DoGrammarFragment::class.java.simpleName)

        if (fragment != null) {
            fragmentManager.beginTransaction().also {
                it.remove(fragment)
                it.commit()
            }
        }
    }
    override fun getViewBinding(inflater: LayoutInflater): FragmentDoPracticesBinding {
        return FragmentDoPracticesBinding.inflate(layoutInflater)
    }

    override fun init() {
        val lessonId = preferences.getInt(Constants.KEY_LESSON_ID)
        doPracticeAdapter = DoPracticeAdapter(viewModel) {
            if (it < doPracticeAdapter.listItem.size - 1) {
                binding.rcvPractice.smoothScrollToPosition(it + 1)
                binding.layoutProgress.progressBar.max = doPracticeAdapter.listItem.size
                binding.layoutProgress.progressBar.progress = it + 1
            } else {
                viewModel.updateIsFinishPartPractice(lessonId,true)
                openFragment(IntroQuickTaskFragment::class.java, null, true)
            }
        }
        binding.rcvPractice.adapter = doPracticeAdapter
        viewModel.lessonQuestionByRuleId.observe(viewLifecycleOwner) { lessonQuestions ->
            val lessonQuestionsSort = lessonQuestions.sortedBy { it.trainerPosition }
            doPracticeAdapter.submitList(lessonQuestionsSort)
        }
        viewModel.getLessonQuestionByLessonId(preferences.getInt(Constants.KEY_LESSON_ID))
    }

    override fun initData() {
    }

    override fun initAction() {
        binding.layoutToolBar.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}