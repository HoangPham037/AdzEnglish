package com.adzenglish.adzenglish.ui.setting.rate

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentRateBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RateFragment : BaseFragmentWithBinding<FragmentRateBinding>() {
    private var rate = Constants.INDEX_5

    @Inject
    lateinit var preferences: Preferences
    override fun init() {
        // do nothing
    }

    override fun initData() {
        // do nothing
    }

    override fun initAction() {
        binding.ivStarOne.setOnClickListener {
            updateUi(Constants.INDEX_1)
        }
        binding.ivStarTwo.setOnClickListener {
            updateUi(Constants.INDEX_2)
        }
        binding.ivStarThree.setOnClickListener {
            updateUi(Constants.INDEX_3)
        }
        binding.ivStarFour.setOnClickListener {
            updateUi(Constants.INDEX_4)
        }
        binding.ivStarFive.setOnClickListener {
            updateUi(Constants.INDEX_5)
        }
        binding.btnRate.setOnClickListener {
            rateApp()
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun rateApp() {
        preferences.setString(Constants.KEY_RATE, Constants.KEY_RATE)
        try {
            val packageName = requireContext().packageName
            val storeLink = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            val intent = Intent(Intent.ACTION_VIEW, storeLink)
            startActivity(intent)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        onBackPressed()
    }

    private fun updateUi(selectedStar: Int) {
        rate = selectedStar
        binding.ivStarOne.setImageResource(if (selectedStar >= Constants.INDEX_1) R.drawable.ic_star_true else R.drawable.ic_star_false)
        binding.ivStarTwo.setImageResource(if (selectedStar >= Constants.INDEX_2) R.drawable.ic_star_true else R.drawable.ic_star_false)
        binding.ivStarThree.setImageResource(if (selectedStar >= Constants.INDEX_3) R.drawable.ic_star_true else R.drawable.ic_star_false)
        binding.ivStarFour.setImageResource(if (selectedStar >= Constants.INDEX_4) R.drawable.ic_star_true else R.drawable.ic_star_false)
        binding.ivStarFive.setImageResource(if (selectedStar >= Constants.INDEX_5) R.drawable.ic_star_true else R.drawable.ic_star_false)
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentRateBinding {
        return FragmentRateBinding.inflate(layoutInflater)
    }
}