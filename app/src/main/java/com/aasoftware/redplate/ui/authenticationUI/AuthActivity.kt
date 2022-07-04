package com.aasoftware.redplate.ui.authenticationUI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aasoftware.redplate.data.AuthRepository
import com.aasoftware.redplate.databinding.ActivityAuthBinding
import com.aasoftware.redplate.ui.MainActivity
import com.aasoftware.redplate.util.DEBUG_TAG
import com.aasoftware.redplate.util.FirebaseConstants
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AuthActivity : AppCompatActivity(){

    private lateinit var binding: ActivityAuthBinding

    private lateinit var oneTapClient: SignInClient
    private lateinit var auth: FirebaseAuth

    private val viewModel: AuthViewModel by viewModels{
        AuthViewModel.Factory(AuthRepository(auth, FirebaseFirestore.getInstance(), oneTapClient))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        oneTapClient = Identity.getSignInClient(this)
        auth = FirebaseAuth.getInstance()

        // Check if user is logged in. In that case, navigate to MainActivity
        viewModel.authFinished.observe(this){ navigate ->
            if (navigate == null) return@observe
            if (navigate){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        viewModel.checkLoggedIn()

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
        viewModel.checkLoggedIn()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(DEBUG_TAG, "on ActivityResult: request code $requestCode")

        when (requestCode) {
            FirebaseConstants.GOOGLE_LOGIN_RC -> {
                viewModel.onGoogleSignInResult(this, oneTapClient, auth, data!!)
            }
        }
    }

}