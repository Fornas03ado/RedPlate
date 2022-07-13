package com.aasoftware.redplate.ui.authenticationUI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aasoftware.redplate.Injection
import com.aasoftware.redplate.databinding.ActivityAuthBinding
import com.aasoftware.redplate.ui.mainUI.MainActivity
import com.aasoftware.redplate.util.DEBUG_TAG
import com.aasoftware.redplate.util.FirestoreConstants


class AuthActivity : AppCompatActivity(){

    /** Object that contains the reference to [AuthActivity] layout views */
    private var _binding: ActivityAuthBinding? = null
    // This property is only valid between onCreateView() and onDestroyView().
    private val binding get() = _binding!!

    private val auth = Injection.remoteRepository!!

    /** Authentication UI shared ViewModel */
    private val viewModel: AuthViewModel by viewModels{
        AuthViewModel.Factory(auth)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            FirestoreConstants.GOOGLE_LOGIN_RC -> {
                viewModel.onGoogleSignInResult(this, auth, data!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}