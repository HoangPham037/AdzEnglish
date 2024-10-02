package com.adzenglish.adzenglish.ui.chooselevel

import android.util.DisplayMetrics
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.adzenglish.adzenglish.adapter.LevelAdapter
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.customview.LineIndicator
import com.adzenglish.adzenglish.databinding.FragmentChooseLevelBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import com.adzenglish.adzenglish.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseLevelFragment : BaseFragmentWithBinding<FragmentChooseLevelBinding>() {
    private val viewModel: ViewModel by viewModels()
    private lateinit var levelAdapter: LevelAdapter
    private lateinit var layoutManager: LinearLayoutManager
    @Inject
    lateinit var preferences: Preferences

    override fun getViewBinding(inflater: LayoutInflater): FragmentChooseLevelBinding {
        return FragmentChooseLevelBinding.inflate(layoutInflater)
    }

    override fun init() {
        levelAdapter = LevelAdapter(viewModel,getScreenWidth()) { id ->
            if (id == -1) onBackPressed()
            else {
                preferences.setInt(Constants.KEY_ID_LEVEL, id)
                viewModel.updateLevelIsSelectedById(id, Constants.INDEX_1)
                onBackPressed()
            }
        }
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rcvLevel)
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rcvLevel.layoutManager = layoutManager
        binding.rcvLevel.addItemDecoration(LineIndicator())
        binding.rcvLevel.adapter = levelAdapter

    }

    override fun initData() {
        viewModel.levelList.observe(viewLifecycleOwner) { dataStatusResult ->
            when (dataStatusResult.status) {
                DataStatus.Status.SUCCESS -> {
                    levelAdapter.submitList(dataStatusResult.data)
                }

                DataStatus.Status.ERROR -> {
                    toast(dataStatusResult.message.toString())
                }
            }
        }
    }

    override fun initAction() {}

    private fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

}