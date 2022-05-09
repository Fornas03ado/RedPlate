package com.aasoftware.redplate.ui.createAccountLogin

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aasoftware.redplate.R
import com.aasoftware.redplate.databinding.CreateAccountFragmentBinding

// TODO: Handle animations
class CreateAccountFragment : Fragment() {

    /* State holder for CreateAccountFragment. Get instance with the viewModels delegate function */
    private val viewModel: CreateAccountViewModel by viewModels()
    /* Object that contains the reference to CreateAccountFragment layout views */
    private lateinit var binding: CreateAccountFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateAccountFragmentBinding.inflate(inflater, container, false)

        // TODO: Add account to firebase
        binding.createAccountButton.setOnClickListener {
            findNavController().navigate(R.id.home_fragment)
        }

        binding.goToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.login_fragment)
        }

        binding.passwordInput.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // if the event is a key down event on the enter button
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    // clear focus in order to hide cursor from edit text
                    binding.passwordInput.clearFocus()
                    return true
                }
                return false
            }
        })

        return binding.root
    }
}