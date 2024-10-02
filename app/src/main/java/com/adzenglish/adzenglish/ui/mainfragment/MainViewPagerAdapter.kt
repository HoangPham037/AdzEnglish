package com.adzenglish.adzenglish.ui.mainfragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.adzenglish.adzenglish.ui.home.HomeFragment
import com.adzenglish.adzenglish.ui.practice.PracticeFragment
import com.adzenglish.adzenglish.ui.rank.RankFragment
import com.adzenglish.adzenglish.ui.setting.SettingFragment


class MainViewPagerAdapter(
    fra: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fra, lifecycle) {
    private val list: ArrayList<Fragment> = arrayListOf(
        HomeFragment(),
        PracticeFragment(),
        RankFragment(),
        SettingFragment()
    )

    override fun getItemCount(): Int = list.size
    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}