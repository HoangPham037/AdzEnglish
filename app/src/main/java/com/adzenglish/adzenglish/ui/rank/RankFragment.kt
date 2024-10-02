package com.adzenglish.adzenglish.ui.rank

import android.content.SharedPreferences
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentRankBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.learn.vocabulary.IntroVocabularyFragment
import com.adzenglish.adzenglish.ui.rank.profile.ProfileNameFragment
import com.adzenglish.adzenglish.utils.extension.goneIf
import com.adzenglish.adzenglish.utils.extension.showNotifyInternet
import com.adzenglish.adzenglish.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RankFragment : BaseFragmentWithBinding<FragmentRankBinding>() {
    @Inject
    lateinit var preferences: Preferences
    private lateinit var adapter: RankAdapter
    private val viewModel: ViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun init() {
        adapter = RankAdapter(viewModel.repository.getDeviceId())
        binding.rvView.adapter = adapter
        viewModel.listAccount.observe(viewLifecycleOwner) { listAccount ->
            adapter.submitList(listAccount)
        }
        viewModel.account.observe(viewLifecycleOwner) { account ->
            updateUi(account == null)
            account?.let {
                viewModel.getListAccounts()
            }
        }
    }

    private fun updateUi(boolean: Boolean) {
        binding.constrainLayout.goneIf(!boolean)
        binding.nestedScrollView.goneIf(boolean)
        binding.btnCollectUniCoins.text = requireContext().getString(if (boolean) R.string.txt_continue else R.string.txt_collect_uni_coins)
        binding.btnCollectUniCoins.setBackgroundResource(if (boolean) R.drawable.bg_btn_rank else R.drawable.bg_text_view)
    }

    override fun initData() {
        val isNetwork = isNetworkAvailable()
        binding.rvView.goneIf(!isNetwork)
        binding.btnRetry.goneIf(isNetwork)
        binding.tvContent.goneIf(isNetwork)
        binding.tvRatings.goneIf(!isNetwork)
        binding.tvInternet.goneIf(isNetwork)
        binding.progressBar.goneIf(isNetwork)
        binding.btnCollectUniCoins.goneIf(!isNetwork)
        if (isNetwork) viewModel.getAccount(viewModel.repository.getDeviceId()) else updateUi(false)
    }

    override fun initAction() {
        binding.btnCollectUniCoins.setOnClickListener {
           if ( isNetworkAvailable()) openFragment(if (viewModel.account.value == null) ProfileNameFragment::class.java else IntroVocabularyFragment::class.java, null, true)
            else showNotification()
        }
        binding.btnRetry.setOnClickListener {
            if (isNetworkAvailable())  initData() else showNotification()
        }
    }

    private fun showNotification() {
       requireContext().showNotifyInternet()
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentRankBinding {
        return FragmentRankBinding.inflate(layoutInflater)
    }
}