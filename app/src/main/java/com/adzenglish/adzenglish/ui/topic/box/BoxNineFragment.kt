package com.adzenglish.adzenglish.ui.topic.box

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentBoxNineBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.learn.grammar.IntroGrammarFragment
import com.adzenglish.adzenglish.ui.learn.practice.IntroPracticeLessonFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.viewmodel.MainActivityVM
import com.adzenglish.adzenglish.viewmodel.TopicViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class BoxNineFragment : BaseFragmentWithBinding<FragmentBoxNineBinding>() {
    @Inject
    lateinit var preferences: Preferences
    private var adapter: DayInfoAdapter? = null
    private val viewModel: TopicViewModel by viewModels()
    private val mainActivityVM: MainActivityVM by activityViewModels()
    override fun init() {
        saveDataDate()
        adapter = DayInfoAdapter(viewModel)
        binding.rcvCalendar.adapter = adapter
        viewModel.listDayInfo.observe(viewLifecycleOwner) { list ->
            adapter?.submitList(list.sortedBy {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.parse(it.date)?.time
            })
        }
    }

    private fun saveDataDate() {
        val currentDate = viewModel.getDate()
        val day = preferences.getString(Constants.KEY_DAY)
        if (day == null) {
            saveData(Constants.INDEX_1, currentDate)
        } else {
            val numberDay = viewModel.compareLightValues(day)
            if (numberDay == Constants.INDEX_1) {
                val totalDay = preferences.getInt(Constants.KEY_NUMBER_DAY) + Constants.INDEX_1
                saveData(totalDay, currentDate)
            } else if (numberDay > Constants.INDEX_1) {
                saveData(Constants.INDEX_1, currentDate)
            }
        }
        setTextNumberDay()
    }
    private fun saveData(numberDay: Int, currentDate: String) {
        viewModel.insertDayToRom(currentDate)
        preferences.setString(Constants.KEY_DAY, currentDate)
        preferences.setInt(Constants.KEY_NUMBER_DAY, numberDay)
    }
    private fun setTextNumberDay() {
        val numberDay = preferences.getInt(Constants.KEY_NUMBER_DAY)
        val text = "Chuỗi $numberDay liên tiếp"
        val tvNumberDay = SpannableString(text)
        tvNumberDay.setSpan(
            RelativeSizeSpan(1.7f),
            Constants.INDEX_6,
            numberDay.toString().length + Constants.INDEX_6,
            Constants.INDEX_0
        )
        tvNumberDay.setSpan(
            ForegroundColorSpan(requireContext().getColor(R.color.yellow)),
            Constants.INDEX_6,
            numberDay.toString().length + Constants.INDEX_6,
            Constants.INDEX_0
        )
        binding.tvNumberDay.text = tvNumberDay
    }

    override fun initData() {
        viewModel.getDataDay()
    }

    override fun initAction() {
        binding.btnContinue.setOnClickListener {
            if (arguments != null) {
                if (arguments?.getString(Constants.KEY_OPEN_SOUND) == Constants.KEY_OPEN_SOUND) onBackPressed()
            } else
                when (mainActivityVM.navigateFragment.value) {
                    Constants.INDEX_1 -> openFragment(IntroGrammarFragment::class.java, null, true)
                    Constants.INDEX_2 -> openFragment(
                        IntroPracticeLessonFragment::class.java,
                        null,
                        true
                    )

                    Constants.INDEX_3 -> onBackPressed()
                }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentBoxNineBinding {
        return FragmentBoxNineBinding.inflate(layoutInflater)
    }
}

