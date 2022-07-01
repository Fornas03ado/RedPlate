package com.aasoftware.redplate.ui.createAccountLogin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.annotation.IdRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aasoftware.redplate.R
import com.aasoftware.redplate.data.AuthRepository
import com.aasoftware.redplate.data.remote.AuthService
import com.aasoftware.redplate.databinding.CreateAccountFragmentBinding
import com.aasoftware.redplate.domain.AuthError
import com.aasoftware.redplate.domain.AuthErrorType
import com.aasoftware.redplate.domain.AuthErrorType.*
import com.aasoftware.redplate.domain.AuthenticationProgress.*
import com.aasoftware.redplate.ui.LoadingDialogFragment
import com.aasoftware.redplate.util.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthUserCollisionException

// TODO: Handle animations
class CreateAccountFragment : Fragment() {

    /* Shared viewModel for CreateAccount, Login and ForgotPassword fragments */
    private val viewModel: AuthViewModel by activityViewModels{
        AuthViewModel.Factory(AuthRepository(AuthService()))
    }
    /* Object that contains the reference to CreateAccountFragment layout views */
    private lateinit var binding: CreateAccountFragmentBinding
    /* The error that is currently displayed or null if there´s no error */
    private var currentErrorType: AuthErrorType? = null
    /* The progress bar dialog */
    private var loadingDialog: LoadingDialogFragment? = null
    /* The snackbar that shows the input error */
    private var errorSnackbar: Snackbar? = null

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
                navigateWithActionId(R.id.action_create_account_fragment_to_login_fragment)
            }
            bannerDismissAction.setOnClickListener {
                showBanner(false)
                currentErrorType = null
            }
            bannerLoginAction.setOnClickListener {
                val args = Bundle().apply {
                    putString(BundleConstants.EMAIL_KEY, emailInput.text.toString())
                }
                navigateWithActionId(R.id.action_create_account_fragment_to_login_fragment, args)
            }

            /* ON TEXT CHANGED LISTENERS:
            * Restore the default drawables when modifying the text on the edit texts, in case
            * they were error themed. */
            usernameInput.doOnTextChanged { _, _, _, _ ->
                if (currentErrorType == USERNAME_ERROR){
                    usernameInput.defaultDrawables(R.drawable.ic_person_24)
                    currentErrorType = null
                    errorSnackbar?.dismiss()
                }
            }
            emailInput.doOnTextChanged { _, _, _, _ ->
                if (currentErrorType == EMAIL_ERROR){
                    emailInput.defaultDrawables(R.drawable.ic_email_24)
                    currentErrorType = null
                    errorSnackbar?.dismiss()
                    showBanner(false)
                }
            }
            passwordInput.doOnTextChanged { _, _, _, _ ->
                if (currentErrorType == PASSWORD_ERROR){
                    passwordInput.defaultDrawables(R.drawable.ic_key_24)
                    confirmPasswordInput.defaultDrawables(R.drawable.ic_key_24)
                    currentErrorType = null
                    errorSnackbar?.dismiss()
                }
            }
            confirmPasswordInput.doOnTextChanged { _, _, _, _ ->
                if (currentErrorType == PASSWORD_ERROR){
                    passwordInput.defaultDrawables(R.drawable.ic_key_24)
                    confirmPasswordInput.defaultDrawables(R.drawable.ic_key_24)
                    currentErrorType = null
                    errorSnackbar?.dismiss()
                }
            }
        }
    }

    /** Add observers to the ViewModel's LiveData variables */
    private fun observeViewModel() {
        viewModel.uiAuthState.observe(viewLifecycleOwner){ state ->
            when(state.progress){
                ERROR -> {
                    if(state.error is FirebaseAuthUserCollisionException){
                        /* User is already registered in Firebase database.
                          Show a banner that asks the user if he wants to log in */
                        currentErrorType = EMAIL_ERROR
                        binding.emailInput.errorDrawables(R.drawable.ic_email_24)
                        showBanner(true)
                    } else {
                        // Unknown error. Show a snackbar reporting the error
                        makeLongSnackbar(R.string.create_account_error_msg)
                    }
                    viewModel.onResultReceived()
                }
                SUCCESS -> {
                    // Account has been created (FirebaseAuth) and uploaded (Firestore) successfully
                    val args = Bundle().apply {
                        putString(BundleConstants.EMAIL_KEY, binding.usernameInput.text.toString())
                        putString(BundleConstants.PASSWORD_KEY, binding.passwordInput.text.toString())
                    }
                    navigateWithActionId(R.id.action_create_account_fragment_to_login_fragment)
                }
                else -> Unit
            }
        }
        viewModel.loading.observe(viewLifecycleOwner){ loading ->
            if (loading){
                loadingDialog = LoadingDialogFragment(getString(R.string.creating_account))
                loadingDialog?.show(requireActivity().supportFragmentManager, null)
            } else {
                loadingDialog?.dismiss()
                loadingDialog = null
            }
        }
    }

    /** Shows the "email already registered" banner when [show] is true. Hides it otherwise */
    private fun showBanner(show: Boolean) {
        // TODO: Handle in and out animations
        if(show){
            binding.banner.visibility = View.VISIBLE
            binding.redplateLabel.visibility = View.INVISIBLE
        } else {
            binding.banner.visibility = View.GONE
            binding.redplateLabel.visibility = View.VISIBLE
        }
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
        errorSnackbar = makeIndefiniteSnackbar(error.errorMessage)

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

    /** Clear layout and navigate via a provided action id */
    private fun navigateWithActionId(@IdRes navAction: Int, args: Bundle? = null) {
        clearLayout()
        findNavController().navigate(navAction, args)
    }

    /** Remove any error message, restore input texts default state: default drawables and empty text */
    private fun clearLayout() {
        with(binding){
            when(currentErrorType){
                EMAIL_ERROR -> {
                    emailInput.defaultDrawables(R.drawable.ic_email_24)
                    showBanner(false)
                }
                USERNAME_ERROR -> {
                    usernameInput.defaultDrawables(R.drawable.ic_person_24)
                }
                PASSWORD_ERROR -> {
                    passwordInput.defaultDrawables(R.drawable.ic_key_24)
                    confirmPasswordInput.defaultDrawables(R.drawable.ic_key_24)
                }
                else -> Unit
            }
            errorSnackbar?.dismiss()
            currentErrorType = null
            usernameInput.setText("")
            emailInput.setText("")
            passwordInput.setText("")
            confirmPasswordInput.setText("")
        }
    }

}