package com.adzenglish.adzenglish.ui.topic.box

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentBoxOneBinding
import com.adzenglish.adzenglish.models.local.room.entity.WordThemeEntity
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.inapppurchase.BottomSheetDialogInApp
import com.adzenglish.adzenglish.ui.inapppurchase.PurchaseActivity
import com.adzenglish.adzenglish.ui.topic.ChangeTopicFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.showNotifyInternet
import com.adzenglish.adzenglish.utils.extension.visible
import com.adzenglish.adzenglish.viewmodel.TopicViewModel
import com.adzenglish.adzenglish.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class BoxOneFragment : BaseFragmentWithBinding<FragmentBoxOneBinding>() {
    @Inject
    lateinit var preferences: Preferences
    private val viewModelMain: ViewModel by viewModels()
    private val viewModel: TopicViewModel by viewModels()
    private var isDict = false
    private var isWordTheme = false
    override fun init() {
        viewModel.listAllDict.observe(viewLifecycleOwner) {
            isDict = true
            updateUi()
        }
        viewModel.listTheme.observe(viewLifecycleOwner) { dataStatusResult ->
            when (dataStatusResult.status) {
                DataStatus.Status.SUCCESS -> {
                    isWordTheme = true
                    updateUi()
                }
                DataStatus.Status.ERROR -> toast(dataStatusResult.message.toString())
            }
        }
        viewModelMain.account.observe(viewLifecycleOwner) { account ->
            binding.tvCoin.text = (account?.gold?: Constants.INDEX_0).toString()
        }
    }

    private fun updateUi() {
        if (isDict && isWordTheme) {
            viewModel.listAllDict.value?.let { listItem ->
                viewModel.listTheme.value?.data?.let { listTheme ->
                    val list: ArrayList<WordThemeEntity> = arrayListOf()
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        listTheme.forEach { theme ->
                            val listWords = viewModel.repository.getWordThemeBySetId(theme.id)
                            list.addAll(listWords)
                        }
                        viewModel.listItem = list
                        val listTmp = list.map { it.id }
                        val totalDictLeaning = listItem.count { dict -> dict.isLeaning }
                        val totalDict = listItem.count { listTmp.indexOf(it.wordThemeId) != -1 }
                        withContext(Dispatchers.Main) {
                            binding.menu.tvFrom.visible()
                            binding.menu.progressBar.max = totalDict
                            binding.menu.progressBar.progress = totalDictLeaning
                            binding.menu.tvLevel.text = String.format("Thay đổi")
                            binding.menu.tvFrom.text = String.format("$totalDict từ")
                            binding.menu.tvTopic.text = String.format("${listTheme.size}  chủ đề")
                            binding.menu.tvProgress.text =
                                String.format("$totalDictLeaning / $totalDict từ")
                        }
                    }
                }
            }
        }
    }

    override fun initData() {
        viewModel.getAllDict()
        viewModel.getListThemeSelect()
        if (!isNetworkAvailable()) binding.tvCoin.text =
            String.format("?") else viewModelMain.getAccount(viewModel.repository.getDeviceId())
    }

    override fun initAction() {
        binding.btnOpenChest.click {
            clickOpenChest()
        }
        binding.tvNo.setOnClickListener {
            onBackPressed()
        }
        binding.menu.tvLevel.setOnClickListener {
            openFragment(ChangeTopicFragment::class.java, null, true)
        }
        binding.tvCoin.setOnClickListener {
            startPurchaseActivity()
        }
    }

    private fun startPurchaseActivity() {
        if (isNetworkAvailable()) startActivity(
            Intent(
                activity,
                PurchaseActivity::class.java
            )
        ) else requireContext().showNotifyInternet()
    }

    private fun clickOpenChest() {
        if (viewModel.listItem.isNotEmpty()) {
            val listWordTheme: ArrayList<WordThemeEntity> = arrayListOf()
            viewModel.listItem.forEach { item ->
                if (item.isHidden == Constants.INDEX_0) listWordTheme.add(item)
            }
            if (listWordTheme.size == Constants.INDEX_0) {
                toast("Bạn đã mở hết dương hãy đăng kí thêm chủ đề để tiếp tục !")
            } else {
                openChest(listWordTheme[Constants.INDEX_0].id)
            }
        }
    }

    private fun openChest(wordThemeId: Int) {
        val currentDate = viewModel.getDate()
        val dayAttendance = preferences.getString(Constants.KEY_TOPIC)
        if (dayAttendance == null || dayAttendance != currentDate) {
            showBoxTwoFragment(wordThemeId)
            preferences.setString(Constants.KEY_TOPIC, currentDate)
        } else {
            if (isNetworkAvailable()) {
                if (viewModelMain.account.value != null) {
                    val coin = viewModelMain.account.value?.gold ?: Constants.INDEX_0
                    if (coin >= Constants.INDEX_5) {
                        BottomSheetDialogInApp(
                            getString(R.string.txt_deduction),
                            getString(R.string.txt_deduction_content),
                            getString(R.string.txt_agree),
                            R.drawable.iv_deduction
                        ) {
                            showLoadingDialog()
                            viewModelMain.updateCoin(
                                coin - Constants.INDEX_5,
                                viewModelMain.account.value?.androidId
                            ) { update ->
                                hideLoadingDialog()
                                if (update) showBoxTwoFragment(wordThemeId) else toast("Có lỗi sảy ra vui lòng thử lại !")
                            }
                        }.show(childFragmentManager, BoxThreeFragment::class.java.name)

                    } else {
                        openPurchaseActivity()
                    }
                } else {
                    openPurchaseActivity()
                }
            } else {
                hideLoadingDialog()
                requireContext().showNotifyInternet()
            }
        }
    }

    private fun openPurchaseActivity() {
        BottomSheetDialogInApp(
            getString(R.string.txt_recharge),
            getString(R.string.txt_recharge_content),
            getString(R.string.txt_to_store),
            R.drawable.iv_recharge
        ) {
            startPurchaseActivity()
        }.show(childFragmentManager, BoxThreeFragment::class.java.name)

    }


    private fun showBoxTwoFragment(wordThemeId: Int) {
        hideLoadingDialog()
        Bundle().apply {
            putInt(Constants.KEY_ID_WORD_THEME, wordThemeId)
            openFragment(BoxTwoFragment::class.java, this, true)
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentBoxOneBinding {
        return FragmentBoxOneBinding.inflate(layoutInflater)
    }
}

