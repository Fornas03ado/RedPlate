package com.aasoftware.redplate.ui.authenticationUI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aasoftware.redplate.R
import com.aasoftware.redplate.data.AuthRepository
import com.aasoftware.redplate.databinding.ActivityAuthBinding
import com.aasoftware.redplate.ui.MainActivity
import com.aasoftware.redplate.util.DEBUG_TAG
import com.aasoftware.redplate.util.FirebaseConstants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AuthActivity : AppCompatActivity(){

    private lateinit var binding: ActivityAuthBinding

    private lateinit var googleClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private val viewModel: AuthViewModel by viewModels{
        AuthViewModel.Factory(AuthRepository(auth, FirebaseFirestore.getInstance(), googleClient))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        // Check if user is logged in. In that case, navigate to MainActivity
        viewModel.authFinished.observe(this){ navigate ->
            if (navigate == null) return@observe
            if (navigate){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        viewModel.checkAuthState()

        /* val navController = findNavController(R.id.nav_host_fragment_activity_main)
        /* Passing each menu ID as a set of Ids because each
        menu should be considered as top level destinations.
        Each menu item is automatically bind with each destination */
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_search, R.id.navigation_home, R.id.navigation_profile
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)*/
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkAuthState()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(DEBUG_TAG, "onActivityResult(): request code $requestCode")
        //Snackbar.make(binding.root, "onActivityResult(): RC = $requestCode", Snackbar.LENGTH_LONG).show()
        when (requestCode) {
            FirebaseConstants.GOOGLE_LOGIN_RC -> {
                viewModel.onGoogleSignInResult(this, auth, data!!)
            }
        }
    }

}