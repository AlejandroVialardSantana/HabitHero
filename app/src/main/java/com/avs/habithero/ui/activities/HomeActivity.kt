package com.avs.habithero.ui.activities

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.avs.habithero.R
import com.avs.habithero.databinding.ActivityHomeBinding
import com.avs.habithero.models.Habit
import com.avs.habithero.ui.fragments.AddHabitFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class HomeActivity: BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        val navController = navHostFragment.navController

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)

        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> bottomNavigationView.menu.findItem(R.id.homeFragment).isChecked = true
                R.id.statsFragment -> bottomNavigationView.menu.findItem(R.id.statsFragment).isChecked = true
                R.id.settingsFragment -> bottomNavigationView.menu.findItem(R.id.settingsFragment).isChecked = true
            }
        }
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        if (!navController.popBackStack()) {
            super.onBackPressed()
        }
    }
}