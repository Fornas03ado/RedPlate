package com.aasoftware.redplate.ui.mainUI.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aasoftware.redplate.ui.mainUI.onboarding.screens.HomeFragment
import com.aasoftware.redplate.ui.mainUI.onboarding.screens.ProfileOverviewFragment
import com.aasoftware.redplate.ui.mainUI.onboarding.screens.SearchFragment

class MainViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager, lifecycle) {
    companion object {
        const val NUM_PAGES = 3
    }

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment = when(position){
        0 -> SearchFragment()
        1 -> HomeFragment()
        else -> ProfileOverviewFragment()
    }
}