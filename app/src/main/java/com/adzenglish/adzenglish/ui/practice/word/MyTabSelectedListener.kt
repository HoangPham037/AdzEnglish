package com.adzenglish.adzenglish.ui.practice.word

import com.google.android.material.tabs.TabLayout

interface MyTabSelectedListener : TabLayout.OnTabSelectedListener {
    override fun onTabSelected(tab: TabLayout.Tab?)
    override fun onTabUnselected(tab: TabLayout.Tab?) {  }
    override fun onTabReselected(tab: TabLayout.Tab){}
}