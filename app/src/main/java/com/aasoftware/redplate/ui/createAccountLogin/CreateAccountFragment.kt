package com.aasoftware.redplate.ui.createAccountLogin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aasoftware.redplate.R
import com.aasoftware.redplate.data.AuthRepository
import com.aasoftware.redplate.data.remote.FirebaseAuthService
import com.aasoftware.redplate.data.remote.GoogleAuthService
import com.aasoftware.redplate.databinding.CreateAccountFragmentBinding
import com.aasoftware.redplate.domain.AuthError
import com.aasoftware.redplate.domain.AuthErrorType
import com.aasoftware.redplate.domain.AuthErrorType.*
import com.aasoftware.redplate.domain.AuthenticationProgress.*
import com.aasoftware.redplate.util.defaultDrawables
import com.aasoftware.redplate.util.errorDrawables
import com.google.android.material.snackbar.Snackbar

// TODO: Handle animations
class CreateAccountFragment : Fragment() {

    /* Shared viewModel for CreateAccount, Login and ForgotPassword fragments */
    private val viewModel: AuthViewModel by activityViewModels{
        AuthViewModel.Factory(AuthRepository(FirebaseAuthService(),
            GoogleAuthService()))
    }
    /* Object that contains the reference to CreateAccountFragment layout views */
    private lateinit var binding: CreateAccountFragmentBinding

    /* The error that is currently displayed or null if there´s no error */
    private var currentErrorType: AuthErrorType? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateAccountFragmentBinding.inflate(inflater, container, false)

        addListeners()
        observeViewModel()

        return binding.root
    }

    /** Add listeners to the fragment views (OnClick, OnTextChanged...) */
    private fun addListeners() {
        with(binding){
            /* CLICK LISTENERS */
            createAccountButton.setOnClickListener {
                onCreateAccountClicked()
            }
            goToLoginButton.setOnClickListener {
                findNavController().navigate(R.id.login_fragment)
            }

            /* ON TEXT CHANGED LISTENERS:
            * Restore the default drawables when modifying the text on the edit texts, in case
            * they were error themed. */
            usernameInput.doOnTextChanged { _, _, _, _ ->
                if (currentErrorType == USERNAME_ERROR){
                    usernameInput.defaultDrawables(R.drawable.ic_person_24)
                    currentErrorType = null
                }
            }
            emailInput.doOnTextChanged { _, _, _, _ ->
                if (currentErrorType == EMAIL_ERROR){
                    emailInput.defaultDrawables(R.drawable.ic_email_24)
                    currentErrorType = null
                }
            }
            passwordInput.doOnTextChanged { _, _, _, _ ->
                if (currentErrorType == PASSWORD_ERROR){
                    passwordInput.defaultDrawables(R.drawable.ic_key_24)
                    confirmPasswordInput.defaultDrawables(R.drawable.ic_key_24)
                    currentErrorType = null
                }
            }
            confirmPasswordInput.doOnTextChanged { _, _, _, _ ->
                if (currentErrorType == PASSWORD_ERROR){
                    passwordInput.defaultDrawables(R.drawable.ic_key_24)
                    confirmPasswordInput.defaultDrawables(R.drawable.ic_key_24)
                    currentErrorType = null
                }
            }
        }
    }

    /** Add observers to the ViewModel's LiveData variables */
    private fun observeViewModel() {
        viewModel.uiAuthState.observe(viewLifecycleOwner){ state ->
            when(state.progress){
                IN_PROGRESS -> {onCreateAccountStart()}
                ERROR -> {onCreateAccountError()}
                /* The SUCCESS state is already handled by AuthActivity and the NONE
                state is ignored */
                else -> Unit
            }
        }
    }

    /** Called when the create account tasks starts.
     * Displays a progress bar indicating the account is being created. */
    private fun onCreateAccountStart() {
        // TODO: Display progress bar
        viewModel.onResultReceived()
    }

    /** Triggered when there was an error creating or uploading the Firebase account.
     * Shows an error message */
    private fun onCreateAccountError() {
        Toast.makeText(requireContext(),
            getString(R.string.create_account_error_msg), Toast.LENGTH_LONG).show()
        viewModel.onResultReceived()
    }

    /** Shows the progress indicator if [show] is true. Hides it otherwise. */
    private fun showProgressBar(show: Boolean){
        // TODO: Alternate progress bar visibility
    }

    /** Called when create account button is clicked.
     * Check if the field inputs are correct via [viewModel] validateInput() function. In case there
     * is an error, handle it calling [handleError] with the error provided by validateInput().
     * Otherwise, attempt to create the account with the inputs provided calling
     * [viewModel].createAccount() */
    private fun onCreateAccountClicked() {
        with(binding){
            val error = viewModel.validateInput(
                usernameInput.text.toString(),
                emailInput.text.toString(),
                passwordInput.text.toString(),
                confirmPasswordInput.text.toString(),
                resources
            )

            if (error == null) {
                viewModel.createAccount(
                    usernameInput.text.toString(),
                    emailInput.text.toString(),
                    passwordInput.text.toString()
                )
            } else {
                handleError(error)
            }
        }
    }

    /** Show an indefinite snackbar with the error message and an 'ok' action to dismiss it.
     * Theme the edit text that contains the error. */
    private fun handleError(error: AuthError){
        Snackbar.make(binding.root, error.errorMessage, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.input_stroke_color))
            .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.input_text_color))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.input_text_color))
            .apply{setAction("OK"){dismiss()}}.show()

        /* Update the edit text drawables */
        when(error.type){
            USERNAME_ERROR -> { binding.usernameInput.errorDrawables(R.drawable.ic_person_24) }
            EMAIL_ERROR -> { binding.emailInput.errorDrawables(R.drawable.ic_email_24) }
            else -> {
                binding.passwordInput.errorDrawables(R.drawable.ic_key_24)
                binding.confirmPasswordInput.errorDrawables(R.drawable.ic_key_24)
            }
        }

        currentErrorType = error.type
    }

}