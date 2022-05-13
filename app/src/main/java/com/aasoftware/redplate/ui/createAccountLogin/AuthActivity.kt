package com.aasoftware.redplate.ui.createAccountLogin

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aasoftware.redplate.R
import com.aasoftware.redplate.data.AuthRepository
import com.aasoftware.redplate.data.remote.FirebaseAuthService
import com.aasoftware.redplate.data.remote.GoogleAuthService
import com.aasoftware.redplate.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity(){

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels{
        AuthViewModel.Factory(AuthRepository(FirebaseAuthService(), GoogleAuthService()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Check if user is logged in. In that case, navigate to PresentationActivity
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*val navController = findNavController(R.id.nav_host_fragment_activity_main)
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

}