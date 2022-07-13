package com.aasoftware.redplate.ui.mainUI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.aasoftware.redplate.Injection
import com.aasoftware.redplate.R
import com.aasoftware.redplate.databinding.ActivityMainBinding
import com.aasoftware.redplate.ui.authenticationUI.AuthActivity

class MainActivity : AppCompatActivity() {
    /** Object that contains the reference to [MainActivity] layout views */
    private var _binding: ActivityMainBinding? = null
    // This property is only valid between onCreateView() and onDestroyView().
    private val binding get() = _binding!!

    /** Authentication and Firestore (remote sources) repository */
    private val remoteRepository = Injection.remoteRepository!!

    /** Main UI shared ViewModel */
    private val viewModel: MainViewModel by viewModels{
        MainViewModel.Factory(remoteRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel.loggedIn.observe(this){ loggedIn ->
            if (!loggedIn){
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }
        viewModel.checkLoggedIn()

        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is still logged in
        viewModel.checkLoggedIn()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}