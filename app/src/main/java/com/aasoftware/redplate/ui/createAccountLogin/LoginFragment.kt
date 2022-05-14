package com.aasoftware.redplate.ui.createAccountLogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aasoftware.redplate.R
import com.aasoftware.redplate.data.AuthRepository
import com.aasoftware.redplate.data.remote.AuthService
import com.aasoftware.redplate.databinding.LoginFragmentBinding
import com.aasoftware.redplate.domain.AuthenticationProgress.*
import com.aasoftware.redplate.ui.LoadingDialogFragment
import com.aasoftware.redplate.util.Credentials.isVaildEmail
import com.aasoftware.redplate.util.defaultDrawables
import com.aasoftware.redplate.util.errorDrawables
import com.aasoftware.redplate.util.makeIndefiniteSnackbar
import com.google.android.material.snackbar.Snackbar


// TODO: Handle animations
class LoginFragment : Fragment() {

    /* Shared viewModel for CreateAccount, Login and ForgotPassword fragments */
    private val viewModel: AuthViewModel by activityViewModels{
        AuthViewModel.Factory(AuthRepository(AuthService()))
    }
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
                    errorSnackbar = makeIndefiniteSnackbar(R.string.invalid_credentials)
                    binding.emailInput.errorDrawables(R.drawable.ic_email_24)
                    binding.passwordInput.errorDrawables(R.drawable.ic_key_24)
                    error = true
                    viewModel.onResultReceived()
                }
                else -> Unit
            }
        }
    }

    private fun addListeners() {
        with(binding){

            loginButton.setOnClickListener {
                val emailValid = emailInput.text.toString().isVaildEmail()
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
                viewModel.requestGoogleLogin(this@LoginFragment)
            }

            goToCreateAccountButton.setOnClickListener {
                findNavController().navigate(R.id.action_login_fragment_to_create_account_fragment)
            }

            forgotPasswordButton.setOnClickListener {
                findNavController().navigate(R.id.action_login_fragment_to_forgotPasswordFragment)
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

}