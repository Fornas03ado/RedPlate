package com.aasoftware.redplate.ui.mainUI.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aasoftware.redplate.databinding.FragmentProfileBinding

class ProfileOverviewFragment : Fragment() {

    /** Object that contains the reference to [ProfileOverviewFragment] layout views */
    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView() and onDestroyView().
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        // CUSTOMIZE FRAGMENT HERE

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}