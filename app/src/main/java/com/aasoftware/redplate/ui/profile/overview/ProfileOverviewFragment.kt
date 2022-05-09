package com.aasoftware.redplate.ui.profile.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aasoftware.redplate.databinding.FragmentProfileBinding

class ProfileOverviewFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileOverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
}