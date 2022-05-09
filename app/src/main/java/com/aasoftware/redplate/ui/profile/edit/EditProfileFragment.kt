package com.aasoftware.redplate.ui.profile.edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.aasoftware.redplate.databinding.EditProfileFragmentBinding

class EditProfileFragment : Fragment() {
    private lateinit var binding: EditProfileFragmentBinding
    private val viewModel: EditProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}