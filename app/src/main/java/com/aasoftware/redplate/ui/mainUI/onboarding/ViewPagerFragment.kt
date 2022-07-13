package com.aasoftware.redplate.ui.mainUI.onboarding

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aasoftware.redplate.R
import com.aasoftware.redplate.databinding.FragmentProfileBinding
import com.aasoftware.redplate.databinding.FragmentViewPagerBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerFragment : Fragment() {
    /** Object that contains the reference to [ViewPagerFragment] layout views */
    private var _binding: FragmentViewPagerBinding? = null
    // This property is only valid between onCreateView() and onDestroyView().
    private val binding get() = _binding!!
    /** Default fragment icons for TabLayout */
    private lateinit var defaultTabIcons: Array<Drawable?>
    /** ViewPager2 fragment state adapter */
    private lateinit var pagerAdapter: MainViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        // Set up onboarding via ViewPager2 and TabLayout
        setUpOnboardingComponents()

        return binding.root
    }

    private fun setUpOnboardingComponents() {
        with(binding){
            // ViewPager must have an adapter before TabLayoutMediator is attached
            pagerAdapter = MainViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
            viewPager.adapter = pagerAdapter

            val selectedTabIconColor = ContextCompat.getColor(
                requireContext(), R.color.input_text_color)
            val defaultTabIconColor = ContextCompat.getColor(
                requireContext(), R.color.input_drawable_tint)
            defaultTabIcons = arrayOf(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_search_24)?.mutate(),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_home_24)?.mutate(),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_person_24)?.mutate(),
            )

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.icon = defaultTabIcons[position]
                tab.icon?.setTint(defaultTabIconColor)
            }.attach()

            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            tabLayout.tabGravity = TabLayout.GRAVITY_CENTER

            tabLayout.setSelectedTabIndicatorColor(selectedTabIconColor)
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        if(tab != null) {
                            tab.icon?.setTint(selectedTabIconColor)
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                        if(tab != null) {
                            tab.icon?.setTint(defaultTabIconColor)
                        }
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        // Nothing to do here
                    }
                })

            // First item in Navigation is Home Fragment, at pos 1.
            viewPager.setCurrentItem(1, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}