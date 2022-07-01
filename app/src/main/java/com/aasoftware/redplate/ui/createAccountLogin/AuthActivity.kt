package com.aasoftware.redplate.ui.createAccountLogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.aasoftware.redplate.R
import com.aasoftware.redplate.data.AuthRepository
import com.aasoftware.redplate.data.remote.AuthService
import com.aasoftware.redplate.databinding.ActivityAuthBinding
import com.aasoftware.redplate.ui.MainActivity
import com.aasoftware.redplate.util.DEBUG_TAG

class AuthActivity : AppCompatActivity(){

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels{
        AuthViewModel.Factory(AuthRepository(AuthService()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is logged in. In that case, navigate to MainActivity
        viewModel.authFinished.observe(this){ navigate ->
            if (navigate == null) return@observe
            if (navigate){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

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

}