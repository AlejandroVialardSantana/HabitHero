package com.avs.habithero.ui.activities

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import com.avs.habithero.R
import com.avs.habithero.databinding.ActivityHomeBinding
import com.avs.habithero.models.Habit
import com.avs.habithero.ui.fragments.AddHabitFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class HomeActivity: BaseActivity(), AddHabitFragment.CalendarActionListener {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        val navController = navHostFragment.navController

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.statsFragment -> {
                    navController.navigate(R.id.statsFragment)
                    true
                }
                R.id.settingsFragment -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }
                else -> {
                    it.onNavDestinationSelected(navController)
                }
            }
        }
    }

    override fun onAddEventToCalendar(habit: Habit) {
        val startTime = Calendar.getInstance()
        val endTime = Calendar.getInstance().apply {
            add(Calendar.HOUR, habit.duration)
        }

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, habit.title)
            putExtra(CalendarContract.Events.EVENT_LOCATION, "Home")
            putExtra(CalendarContract.Events.DESCRIPTION, "Scheduled via HabitHero app.")
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

}