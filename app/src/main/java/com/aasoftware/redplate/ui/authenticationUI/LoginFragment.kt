package com.aasoftware.redplate.ui.authenticationUI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aasoftware.redplate.R
import com.aasoftware.redplate.databinding.FragmentForgotPasswordBinding
import com.aasoftware.redplate.databinding.FragmentLoginBinding
import com.aasoftware.redplate.domain.AuthenticationProgress.*
import com.aasoftware.redplate.domain.GoogleSignInFailedException
import com.aasoftware.redplate.domain.InvalidCredentialsException
import com.aasoftware.redplate.domain.UserNotVerifiedException
import com.aasoftware.redplate.ui.LoadingDialog
import com.aasoftware.redplate.ui.VerifyEmailDialog
import com.aasoftware.redplate.util.*
import com.aasoftware.redplate.util.Credentials.isValidEmail
import com.google.android.material.snackbar.Snackbar

// TODO: Read args
// TODO: Handle animations
class LoginFragment : Fragment() {

    /** Shared viewModel for CreateAccount, Login and ForgotPassword fragments, owned by [AuthActivity] */
    private val viewModel: AuthViewModel by activityViewModels()
    /** Object that contains the reference to [LoginFragment] layout views */
    private var _binding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView() and onDestroyView().
    private val binding get() = _binding!!
    /** The snackbar that shows the input error */
    private var errorSnackbar: Snackbar? = null
    /** The progress bar dialog */
    private var loadingDialog: LoadingDialog? = null
    /** Whether an error is currently displayed */
    private var error = false
    /** The verify email dialog */
    private var verifyDialog: VerifyEmailDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        addListeners()
        observeViewModel()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Write email and password fields if required
        val email = viewModel.navEmail
        val password = viewModel.navPassword
        // Log.d(DEBUG_TAG, "onStart(): {$email, $password}")
        if (email != null){
            binding.emailInput.setText(email, TextView.BufferType.EDITABLE)
            viewModel.navEmail = null
        }
        if (password != null){
            binding.passwordInput.setText(password, TextView.BufferType.EDITABLE)
            viewModel.navPassword = null
        }
        // Log.d(DEBUG_TAG, "EditText values: {${binding.emailInput.text}, ${binding.passwordInput.text}}")
    }

    /** Add the needed listeners to the UI such as click and text changed listeners */
    private fun addListeners() {
        with(binding){
            loginButton.setOnClickListener {
                // Email and password fields
                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()
                // Whether they are valid or not
                val emailValid = email.isValidEmail()
                val passValid = password.isNotEmpty()

                // Check if both fields are correct
                if (emailValid && passValid){
                    // Go on with the log in attempt
                    viewModel.requestFirebaseLogin(email, password)
                } else {
                    // Update UI with the errors
                    if (!emailValid){binding.emailInput.errorDrawables(R.drawable.ic_email_24)}
                    if (!passValid){binding.passwordInput.errorDrawables(R.drawable.ic_key_24)}
                    // Notify the error to the user
                    errorSnackbar = makeIndefiniteSnackbar(R.string.credentials_introduced_not_valid)
                    error = true
                }
            }

            // com.google.android.gms.common.SignInButton could also be used, but a custom
            // button design was used in this case to fit the app color scheme
            googleLoginButton.setOnClickListener {
                viewModel.requestGoogleLogin(this@LoginFragment)
            }

            goToCreateAccountButton.setOnClickListener {
                navigateWithActionId(R.id.action_login_fragment_to_create_account_fragment)
            }

            forgotPasswordButton.setOnClickListener {
                viewModel.navEmail = emailInput.text.toString()
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

    /** Observe [viewModel]'s LiveData */
    private fun observeViewModel() {
        viewModel.uiAuthState.observe(viewLifecycleOwner){ state ->
            when(state.progress){
                SUCCESS -> {
                    viewModel.onAuthenticationFinished()
                }
                ERROR -> {
                    when(state.error){
                        null -> makeLongSnackbar(R.string.unknown_error)
                        is InvalidCredentialsException -> {
                            errorSnackbar = makeIndefiniteSnackbar(R.string.invalid_credentials)
                            binding.emailInput.errorDrawables(R.drawable.ic_email_24)
                            binding.passwordInput.errorDrawables(R.drawable.ic_key_24)
                            error = true
                        }
                        is GoogleSignInFailedException -> {
                            makeLongSnackbar(state.error.message!!)
                            viewModel.onResultReceived()
                        }
                        is UserNotVerifiedException -> {
                            // Show verification dialog
                            verifyDialog = VerifyEmailDialog{
                                verifyDialog?.dismiss()
                                verifyDialog = null
                                loadingDialog = LoadingDialog(getString(R.string.sending_email))
                                loadingDialog?.show(requireActivity().supportFragmentManager, null)
                                viewModel.lastUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                                    if (task.isSuccessful){
                                        loadingDialog?.dismiss()
                                        loadingDialog = null
                                        makeLongSnackbar(R.string.verification_email_sent)
                                    } else {
                                        loadingDialog?.dismiss()
                                        loadingDialog = null
                                        makeLongSnackbar(R.string.error_sending_verification_email)
                                    }
                                }
                            }
                            verifyDialog?.show(requireActivity().supportFragmentManager, null)
                        }
                    }
                    viewModel.onResultReceived()
                }
                // Currently, there are no other relevant UI states
                else -> Unit
            }
        }
        viewModel.loading.observe(viewLifecycleOwner){ loading ->
            if (loading){
                loadingDialog = LoadingDialog(getString(R.string.waiting_for_server))
                loadingDialog?.show(childFragmentManager, null)
            } else {
                loadingDialog?.dismiss()
                loadingDialog = null
            }
        }
    }

    /** Clear layout and navigate via a provided action id */
    private fun navigateWithActionId(@IdRes navAction: Int) {
        clearLayout()
        findNavController().navigate(navAction)
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
            emailInput.setText("", TextView.BufferType.EDITABLE)
            passwordInput.setText("", TextView.BufferType.EDITABLE)
            loadingDialog?.dismiss()
            verifyDialog?.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}