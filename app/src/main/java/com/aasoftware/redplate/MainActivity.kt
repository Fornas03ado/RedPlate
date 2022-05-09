package com.aasoftware.redplate

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.IdRes
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aasoftware.redplate.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private val loggedIn = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment).apply {
            addOnDestinationChangedListener(this@MainActivity)
        }

        // TODO: Check if user is logged in
        /* Check if the user is logged in.
        In that case, navigate directly to home fragment */
        /*val loggedIn = false
        if(loggedIn){
            findNavController(R.id.nav_host_fragment).navigate(R.id.home_fragment)
        }
        binding.navView.visibility = if (loggedIn) View.VISIBLE else View.GONE*/

        loggedIn.observe(this) { logged ->
            if (logged) {
                navController.navigate(R.id.home_fragment)
                binding.navView.visibility = View.VISIBLE
            } else {
                binding.navView.visibility = View.GONE
            }
        }

        val navView: BottomNavigationView = binding.navView
        navView.selectedItemId = R.id.home_fragment
        navView.setOnItemSelectedListener { item ->
            // The menu id is the same as the destination id
           navController.navigate(item.itemId)
            true
        }

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

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        // TODO: Handle navigation changes
        if (destination.id == R.id.home_fragment
            or R.id.profile_overview_fragment
            or R.id.search_fragment
        ) {
            loggedIn.value = true
            controller.clearBackStack(destination.id)
        } else {
            loggedIn.value = false
        }
        Log.d(javaClass.canonicalName, "Destination id: ${destination.id}")
    }

    /*override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment)
        val fragmentId = navController.currentDestination?.id
        if (fragmentId == R.id.home_fragment) {
            navController.clearBackStack(fragmentId)
            finish()
        } else {
            super.onBackPressed()
        }
    }*/

}