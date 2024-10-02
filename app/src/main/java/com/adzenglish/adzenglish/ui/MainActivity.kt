package com.adzenglish.adzenglish.ui

import android.view.LayoutInflater
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseActivity
import com.adzenglish.adzenglish.databinding.ActivityMainBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.learn.grammar.DoGrammarFragment
import com.adzenglish.adzenglish.ui.learn.practice.DoPracticesFragment
import com.adzenglish.adzenglish.ui.learn.quicktask.IntroQuickTaskFragment
import com.adzenglish.adzenglish.ui.learn.quicktask.QuickTaskFragment
import com.adzenglish.adzenglish.ui.learn.result.ResultFragment
import com.adzenglish.adzenglish.ui.learn.vocabulary.DoExerciseVocabularyFragment
import com.adzenglish.adzenglish.ui.mainfragment.MainFragment
import com.adzenglish.adzenglish.ui.practice.PracticeCheckFragment
import com.adzenglish.adzenglish.ui.practice.skill.ResultSkillFragment
import com.adzenglish.adzenglish.ui.practice.skill.SkillFragment
import com.adzenglish.adzenglish.ui.practice.transplant.TransplantFromFrg
import com.adzenglish.adzenglish.ui.rank.profile.ProfilePhotoFragment
import com.adzenglish.adzenglish.ui.splash.SplashFragment
import com.adzenglish.adzenglish.ui.topic.box.BoxEightFragment
import com.adzenglish.adzenglish.ui.topic.box.BoxNineFragment
import com.adzenglish.adzenglish.ui.topic.box.BoxSixFragment
import com.adzenglish.adzenglish.ui.topic.box.BoxThreeFragment
import com.adzenglish.adzenglish.ui.topic.box.BoxTwoFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    @Inject
    lateinit var preferences: Preferences

    override fun getViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun init() {
        openFragment(SplashFragment::class.java, null, false)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(android.R.id.content)) {
            is ProfilePhotoFragment, is PracticeCheckFragment, is ResultFragment, is ResultSkillFragment, is BoxTwoFragment, is BoxNineFragment, is BoxSixFragment, is BoxEightFragment -> {
                backToMainFragment()
            }

            is TransplantFromFrg -> {
                BottomSheetDialogClose(
                    getString(R.string.txt_leave_learn),
                    getString(R.string.txt_desc_leave_learn),
                    getString(R.string.txt_yes_practice),
                    R.drawable.ic_close_app
                ) {
                    super.onBackPressed()
                }.show(supportFragmentManager, MainFragment::class.java.name)
            }

            is DoExerciseVocabularyFragment, is DoGrammarFragment, is DoPracticesFragment, is SkillFragment, is BoxThreeFragment -> {
                BottomSheetDialogClose(
                    getString(R.string.txt_leave_learn),
                    getString(R.string.txt_desc_leave_learn),
                    getString(R.string.txt_yes_practice),
                    R.drawable.ic_close_app
                ) {
                    backToMainFragment()
                }.show(supportFragmentManager, MainFragment::class.java.name)
            }

            is MainFragment -> {
                BottomSheetDialogClose(
                    getString(R.string.txt_Leave),
                    getString(R.string.txt_practice_more),
                    getString(R.string.txt_yes_practice),
                    R.drawable.ic_close_app_one
                ) {
                    finish()
                }.show(supportFragmentManager, MainFragment::class.java.name)
            }
            is IntroQuickTaskFragment, is QuickTaskFragment -> {
                //do nothing
            }

            else -> super.onBackPressed()
        }
    }

    private fun backToMainFragment() {
        supportFragmentManager.popBackStack(
            MainFragment::class.java.simpleName,
            0
        )
    }
}