package com.gscapin.blogger

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.gscapin.blogger.core.hide
import com.gscapin.blogger.core.show
import com.gscapin.blogger.databinding.ActivityMainBinding
import com.gscapin.blogger.presentation.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        observeDestinationChange(navController)

    }

    private fun observeDestinationChange(navController: NavController) {
        navController.addOnDestinationChangedListener{controller, destination, arguments ->
            when(destination.id){
                R.id.loginFragment -> {
                    binding.bottomNavigation.hide()
                }
                R.id.createAccountFragment -> {
                    binding.bottomNavigation.hide()
                }
                R.id.welcomeFragment -> {
                    binding.bottomNavigation.hide()
                }
                R.id.setupProfileFragment -> {
                    binding.bottomNavigation.hide()
                }
                R.id.userProfileFragment -> {
                    binding.bottomNavigation.hide()
                }
                R.id.messageUserFragment -> {
                    binding.bottomNavigation.hide()
                }
                else -> {
                    binding.bottomNavigation.show()
                }

            }
        }
    }
}