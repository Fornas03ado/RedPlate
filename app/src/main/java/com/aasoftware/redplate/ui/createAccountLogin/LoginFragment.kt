package com.aasoftware.redplate.ui.createAccountLogin

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aasoftware.redplate.R
import com.aasoftware.redplate.data.AuthRepository
import com.aasoftware.redplate.data.remote.FirebaseAuthService
import com.aasoftware.redplate.data.remote.GoogleAuthService
import com.aasoftware.redplate.databinding.LoginFragmentBinding
import com.aasoftware.redplate.util.hideSoftKeyboard


// TODO: Handle animations
class LoginFragment : Fragment() {

    /* Shared viewModel for CreateAccount, Login and ForgotPassword fragments */
    private val viewModel: AuthViewModel by activityViewModels{
        AuthViewModel.Factory(AuthRepository(FirebaseAuthService(), GoogleAuthService()))
    }
    /* Binding that contains a reference to Login fragment views */
    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)

        addClickListeners()
        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        // TODO: Observe LiveData
    }

    private fun addClickListeners() {
        with(binding){

            loginButton.setOnClickListener {
                viewModel.requestFirebaseLogin(emailInput.text.toString(),
                passwordInput.text.toString())
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
                //TODO: Navigate to forgot password fragment
            }

        }
    }

}