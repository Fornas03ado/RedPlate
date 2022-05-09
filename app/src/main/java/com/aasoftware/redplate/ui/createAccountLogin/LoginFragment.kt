package com.aasoftware.redplate.ui.createAccountLogin

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aasoftware.redplate.R
import com.aasoftware.redplate.databinding.LoginFragmentBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


// TODO: Handle animations
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)

        with(binding){

            loginButton.setOnClickListener {
                findNavController().navigate(R.id.home_fragment)
            }

            // com.google.android.gms.common.SignInButton could also be used, but a custom
            // button design was used in this case to fit the app color scheme
            googleLoginButton.setOnClickListener {
                onGoogleClicked()
            }

            goToCreateAccountButton.setOnClickListener {
                findNavController().navigate(R.id.action_login_fragment_to_create_account_fragment)
            }

            forgotPasswordButton.setOnClickListener {
                //TODO: Navigate to forgot password fragment
            }

            passwordInput.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    // if the event is a key down event on the enter button
                    if (event.action == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER
                    ) {
                        // clear focus in order to hide cursor from edit text
                        passwordInput.clearFocus()
                        return true
                    }
                    return false
                }
            })

        }
        return binding.root
    }

    /** Check if the user has previously logged in via Google services.
    * If not, launch the Google sign in intent */
    private fun onGoogleClicked() {
        /* Configure sign-in to request the user's ID, email address, and basic
        profile. ID and basic profile are included in DEFAULT_SIGN_IN. */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        /* Build a GoogleSignInClient with the options specified by gso. */
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        /* Check for existing Google Sign In account, if the user is already signed in
        the GoogleSignInAccount will be non-null. Otherwise, me method will return null */
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if (account == null){
            val signInIntent: Intent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 0)
        } else {
            findNavController().navigate(R.id.home_fragment)
        }

    }

}