package com.adzenglish.adzenglish.ui.practice.grammar

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentPracticeGrammarBinding
import com.adzenglish.adzenglish.models.local.room.entity.RuleEntity
import com.adzenglish.adzenglish.ui.practice.word.MyTabSelectedListener
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import com.adzenglish.adzenglish.utils.extension.goneIf
import com.adzenglish.adzenglish.viewmodel.PracticeVM
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PracticeGrammarFragment : BaseFragmentWithBinding<FragmentPracticeGrammarBinding>(),
    MyTabSelectedListener {
    private var index = Constants.INDEX_0
    private var adapter: GrammarAdapter? = null
    private val viewModel: PracticeVM by viewModels()

    override fun init() {
        adapter = GrammarAdapter(viewModel) { rule ->
            updateUi(rule)
        }
        binding.rcvGrammar.adapter = adapter
        viewModel.listGrammarStudying.observe(viewLifecycleOwner) { dataStatusResult ->
            when (dataStatusResult.status) {
                DataStatus.Status.SUCCESS -> {
                    setupTabLayout()
                    dataStatusResult.data?.let { if (index == Constants.INDEX_0) updateUiRule(it) }
                }

                DataStatus.Status.ERROR -> toast(dataStatusResult.message.toString())
            }
        }
        viewModel.listGrammarLearned.observe(viewLifecycleOwner) { dataStatusResult ->
            when (dataStatusResult.status) {
                DataStatus.Status.SUCCESS -> dataStatusResult.data?.let {
                    setupTabLayout()
                    if (index == Constants.INDEX_1) updateUiRule(it)
                }

                DataStatus.Status.ERROR -> toast(dataStatusResult.message.toString())
            }
        }
    }

    private fun updateUiRule(listRule: List<RuleEntity>) {
        setupNoData(listRule)
        adapter?.submitList(listRule)
    }

    private fun updateUi(ruleEntity: RuleEntity) {
        ruleEntity.isStudying = !ruleEntity.isStudying
        viewModel.updateRuleToRoom(ruleEntity)
    }

    override fun initData() {
        arguments?.let { bundle ->
            val totalGrammar = bundle.getString(Constants.KEY_LIST_RULE)
            binding.tvTotalGrammar.text = totalGrammar
        }
        viewModel.getListGrammarStudying(true)
        viewModel.getListGrammarLearned(false)
    }

    private fun setupNoData(listData: List<RuleEntity>?) {
        val boolean = listData.isNullOrEmpty()
        binding.ivNoData.goneIf(!boolean)
        binding.tvNoData.goneIf(!boolean)
    }

    override fun initAction() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(this)
        val tabLearned = binding.tabLayout.getTabAt(Constants.INDEX_1)
        val tabStudying = binding.tabLayout.getTabAt(Constants.INDEX_0)
        binding.tabLayout.setSelectedTabIndicatorColor(requireContext().getColor(R.color.orange))
        tabLearned?.text = String.format("Đã học ${viewModel.listGrammarLearned.value?.data?.size}")
        tabStudying?.text =
            String.format("Đang học ${viewModel.listGrammarStudying.value?.data?.size}")
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.position?.let { position ->
            index = position
            setupNoData(if (index == Constants.INDEX_0) viewModel.listGrammarStudying.value?.data else viewModel.listGrammarLearned.value?.data)
            adapter?.submitList(if (index == Constants.INDEX_0) viewModel.listGrammarStudying.value?.data else viewModel.listGrammarLearned.value?.data)
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentPracticeGrammarBinding {
        return FragmentPracticeGrammarBinding.inflate(layoutInflater)
    }
}