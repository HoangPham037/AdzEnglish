package com.adzenglish.adzenglish.ui.mainfragment

import android.view.LayoutInflater
import androidx.viewpager2.widget.ViewPager2
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragmentWithBinding<FragmentMainBinding>() {

    override fun init() {
        val viewPagerAdapter = MainViewPagerAdapter(childFragmentManager, lifecycle)
        binding.viewpagerManager.apply {
            offscreenPageLimit = 4
            adapter = viewPagerAdapter
            isUserInputEnabled = false
        }
        binding.bottomNav.setOnClickItemClickListener { position ->
            binding.viewpagerManager.currentItem = position
        }
        binding.viewpagerManager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNav.changeSelectMenu(position)
            }
        })
    }

    override fun initData() {
        //do nothing
    }

    override fun initAction() {
        //do nothing
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentMainBinding {
        return FragmentMainBinding.inflate(layoutInflater)
    }
}