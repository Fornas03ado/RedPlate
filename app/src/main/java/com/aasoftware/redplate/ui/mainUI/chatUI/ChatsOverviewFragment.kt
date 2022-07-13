package com.aasoftware.redplate.ui.mainUI.chatUI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aasoftware.redplate.R
import com.aasoftware.redplate.databinding.FragmentSearchBinding

class ChatsOverviewFragment : Fragment() {

    /** Object that contains the reference to [ChatsOverviewFragment] layout views */
    private var _binding: FragmentSearchBinding? = null
    // This property is only valid between onCreateView() and onDestroyView().
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val dashboardViewModel =
        // ViewModelProvider(this)[SearchViewModel::class.java]
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}