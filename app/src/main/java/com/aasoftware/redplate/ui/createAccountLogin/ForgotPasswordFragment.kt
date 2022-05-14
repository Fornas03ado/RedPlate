package com.aasoftware.redplate.ui.createAccountLogin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aasoftware.redplate.R
import com.aasoftware.redplate.data.AuthRepository
import com.aasoftware.redplate.data.remote.AuthService
import com.aasoftware.redplate.databinding.FragmentForgotPasswordBinding
import com.aasoftware.redplate.domain.AuthenticationProgress.*
import com.aasoftware.redplate.ui.LoadingDialogFragment
import com.aasoftware.redplate.util.Credentials.isVaildEmail
import com.aasoftware.redplate.util.defaultDrawables
import com.aasoftware.redplate.util.errorDrawables
import com.aasoftware.redplate.util.makeIndefiniteSnackbar
import com.google.android.material.snackbar.Snackbar

class ForgotPasswordFragment : Fragment() {

    /* Shared viewModel for CreateAccount, Login and ForgotPassword fragments */
    private val viewModel: AuthViewModel by activityViewModels{
        AuthViewModel.Factory(AuthRepository(AuthService()))
    }
    private lateinit var binding: FragmentForgotPasswordBinding
    /* The progress bar dialog */
    private var loadingDialog: LoadingDialogFragment? = null
    /* The snackbar that contains the input error */
    private var snackbar: Snackbar? = null
    private var error = false

    /** Add listeners and livedata observers */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)

        /* Attempt to send a reset email */
        binding.continueButton.setOnClickListener {
            if(binding.emailInput.text.toString().isVaildEmail()){
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
                loadingDialog = LoadingDialogFragment(getString(R.string.creating_account))
                loadingDialog?.show(requireActivity().supportFragmentManager, null)
            } else {
                loadingDialog?.dismiss()
                loadingDialog = null
            }
        }

        return binding.root
    }
}