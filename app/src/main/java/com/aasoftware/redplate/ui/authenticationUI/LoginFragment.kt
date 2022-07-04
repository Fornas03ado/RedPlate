package com.aasoftware.redplate.ui.authenticationUI

import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aasoftware.redplate.R
import com.aasoftware.redplate.databinding.LoginFragmentBinding
import com.aasoftware.redplate.domain.AuthenticationProgress.*
import com.aasoftware.redplate.domain.GoogleSignInFailedException
import com.aasoftware.redplate.ui.LoadingDialogFragment
import com.aasoftware.redplate.util.*
import com.aasoftware.redplate.util.Credentials.isValidEmail
import com.google.android.material.snackbar.Snackbar


// TODO: Handle animations
// TODO: Read email from args
class LoginFragment : Fragment() {

    /* Shared viewModel for CreateAccount, Login and ForgotPassword fragments */
    private val viewModel: AuthViewModel by activityViewModels()
    /* Binding that contains a reference to Login fragment views */
    private lateinit var binding: LoginFragmentBinding
    /* The snackbar that shows the input error */
    private var errorSnackbar: Snackbar? = null
    /* The progress bar dialog */
    private var loadingDialog: LoadingDialogFragment? = null
    /** Whether an error is currently displayed */
    private var error = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)

        addListeners()
        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.uiAuthState.observe(viewLifecycleOwner){ state ->
            when(state.progress){
                SUCCESS -> {
                    viewModel.onAuthenticationFinished()
                }
                ERROR -> {
                    if(state.error != null && state.error is GoogleSignInFailedException){
                        makeLongSnackbar(state.error.message!!)
                        viewModel.onResultReceived()
                    } else {
                        errorSnackbar = makeIndefiniteSnackbar(R.string.invalid_credentials)
                        binding.emailInput.errorDrawables(R.drawable.ic_email_24)
                        binding.passwordInput.errorDrawables(R.drawable.ic_key_24)
                        error = true
                        viewModel.onResultReceived()
                    }
                }
                else -> Unit
            }
        }
    }

    private fun addListeners() {
        with(binding){

            loginButton.setOnClickListener {
                val emailValid = emailInput.text.toString().isValidEmail()
                val passValid = passwordInput.text.toString().isNotEmpty()
                if (emailValid && passValid){
                    viewModel.requestFirebaseLogin(emailInput.text.toString(),
                        passwordInput.text.toString())
                } else {
                    if (!emailValid){binding.emailInput.errorDrawables(R.drawable.ic_email_24)}
                    if (!passValid){binding.passwordInput.errorDrawables(R.drawable.ic_key_24)}
                    errorSnackbar = makeIndefiniteSnackbar(R.string.invalid_credentials)
                    error = true
                }
            }

            // com.google.android.gms.common.SignInButton could also be used, but a custom
            // button design was used in this case to fit the app color scheme
            googleLoginButton.setOnClickListener {
                viewModel.requestGoogleLogin(requireActivity()){ result ->
                    if (result.isSuccessful){
                        try {
                            startIntentSenderForResult(
                                result.result.pendingIntent.intentSender, FirebaseConstants.GOOGLE_LOGIN_RC,
                                null, 0, 0, 0, null)
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e(DEBUG_TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                            makeLongSnackbar(R.string.couldnt_open_google_signin_error)
                        }
                    } else {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        result.exception!!.localizedMessage?.let { msg ->
                            Log.d(DEBUG_TAG, msg)
                            makeLongSnackbar(R.string.no_google_accounts_found_error)
                        }
                    }
                }
            }

            goToCreateAccountButton.setOnClickListener {
                navigateWithActionId(R.id.action_login_fragment_to_create_account_fragment)
            }

            forgotPasswordButton.setOnClickListener {
                navigateWithActionId(R.id.action_login_fragment_to_forgotPasswordFragment)
            }

            emailInput.doOnTextChanged{ _, _, _, _ ->
                if(error){
                    errorSnackbar?.dismiss()
                    emailInput.defaultDrawables(R.drawable.ic_email_24)
                    passwordInput.defaultDrawables(R.drawable.ic_key_24)
                    error = false
                }
            }
            passwordInput.doOnTextChanged{ _, _, _, _ ->
                if(error){
                    errorSnackbar?.dismiss()
                    emailInput.defaultDrawables(R.drawable.ic_email_24)
                    passwordInput.defaultDrawables(R.drawable.ic_key_24)
                    error = false
                }
            }
        }
    }

    /** Clear layout and navigate via a provided action id */
    private fun navigateWithActionId(@IdRes navAction: Int, args: Bundle? = null) {
        clearLayout()
        findNavController().navigate(navAction, args)
    }

    /** Remove any error message, restore input texts default state: default drawables and empty text */
    private fun clearLayout() {
        with(binding){
            // Make sure the input text drawables are default
            if(error){
                errorSnackbar?.dismiss()
                emailInput.defaultDrawables(R.drawable.ic_email_24)
                passwordInput.defaultDrawables(R.drawable.ic_key_24)
                error = false
            }

            // Empty the input text
            emailInput.setText("")
            passwordInput.setText("")
            loadingDialog?.dismiss()
        }
    }

}