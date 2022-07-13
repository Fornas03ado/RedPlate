package com.aasoftware.redplate.ui.authenticationUI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aasoftware.redplate.R
import com.aasoftware.redplate.databinding.FragmentCreateAccountBinding
import com.aasoftware.redplate.databinding.FragmentForgotPasswordBinding
import com.aasoftware.redplate.domain.AuthenticationProgress.*
import com.aasoftware.redplate.ui.LoadingDialog
import com.aasoftware.redplate.util.Credentials.isValidEmail
import com.aasoftware.redplate.util.defaultDrawables
import com.aasoftware.redplate.util.errorDrawables
import com.aasoftware.redplate.util.makeIndefiniteSnackbar
import com.google.android.material.snackbar.Snackbar

class ForgotPasswordFragment : Fragment() {

    /** Shared viewModel for CreateAccount, Login and ForgotPassword fragments */
    private val viewModel: AuthViewModel by activityViewModels()
    /** Object that contains the reference to [ForgotPasswordFragment] layout views */
    private var _binding: FragmentForgotPasswordBinding? = null
    // This property is only valid between onCreateView() and onDestroyView().
    private val binding get() = _binding!!
    /** The progress bar dialog */
    private var loadingDialog: LoadingDialog? = null
    /** The snackbar that contains the input error */
    private var snackbar: Snackbar? = null
    /** Whether an error is being displayed */
    private var error = false

    /** Add listeners and livedata observers */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)

        /* Attempt to send a reset email */
        binding.continueButton.setOnClickListener {
            if(binding.emailInput.text.toString().isValidEmail()){
                viewModel.recoverPassword(binding.emailInput.text.toString())
            } else {
                binding.emailInput.errorDrawables(R.drawable.ic_email_24)
                snackbar = makeIndefiniteSnackbar(R.string.email_error_text)
                error = true
            }
        }

        /* Navigate back to Login Fragment */
        binding.goBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_login_fragment)
        }

        /* Set the default drawables back if there was an error */
        binding.emailInput.doOnTextChanged { _, _, _, _ ->
            if(error){
                snackbar?.dismiss()
                snackbar = null
                binding.emailInput.defaultDrawables(R.drawable.ic_email_24)
                error = false
            }
        }

        /* Observe for the task result */
        viewModel.uiAuthState.observe(viewLifecycleOwner){
            when(it.progress){
                SUCCESS -> {
                    viewModel.onResultReceived()
                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_login_fragment)
                }
                ERROR -> {
                    snackbar = makeIndefiniteSnackbar(R.string.email_not_found)
                    binding.emailInput.errorDrawables(R.drawable.ic_email_24)
                    error = true
                    viewModel.onResultReceived()
                }
                else -> Unit
            }
        }

        /* Observe the view model loading state */
        viewModel.loading.observe(viewLifecycleOwner){ loading ->
            if (loading){
                loadingDialog = LoadingDialog(getString(R.string.sending_email))
                loadingDialog?.show(requireActivity().supportFragmentManager, null)
            } else {
                loadingDialog?.dismiss()
                loadingDialog = null
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Login Fragment will save the email in the AuthViewModel navEmail variable
        // The first time it is read, it will be non-null and will be written to improve
        // user experience. Any other onStart cases the email will remain unchanged.
        val email = viewModel.navEmail
        if (email != null){
            binding.emailInput.setText(email, TextView.BufferType.EDITABLE)
            viewModel.navEmail = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}