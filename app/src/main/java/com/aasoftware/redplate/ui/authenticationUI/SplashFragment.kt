package com.aasoftware.redplate.ui.authenticationUI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.aasoftware.redplate.R
import com.aasoftware.redplate.ui.MainActivity
import com.aasoftware.redplate.util.DEBUG_TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkAuthState()
        viewModel.viewModelScope.launch {
            delay(1000)
            viewModel.authFinished.observe(viewLifecycleOwner){ loggedIn ->
                if (loggedIn == null) return@observe
                if (loggedIn){
                    requireActivity().apply {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_login_fragment)
                    Log.d(DEBUG_TAG, "Navigation to Login launched")
                }
            }
        }
    }
}