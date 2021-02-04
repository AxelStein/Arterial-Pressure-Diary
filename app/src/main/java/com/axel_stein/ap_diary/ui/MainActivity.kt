package com.axel_stein.ap_diary.ui

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.databinding.ActivityMainBinding
import com.axel_stein.ap_diary.ui.home.HomeFragment
import com.axel_stein.ap_diary.ui.utils.setVisible

class MainActivity : AppCompatActivity(), HomeFragment.YearMonthListCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val destinationListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            binding.spinnerDate.setVisible(destination.id == R.id.home_fragment)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navController = findNavController(R.id.nav_host_fragment)
        binding.toolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(destinationListener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(destinationListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun getSpinnerView() = binding.spinnerDate
}