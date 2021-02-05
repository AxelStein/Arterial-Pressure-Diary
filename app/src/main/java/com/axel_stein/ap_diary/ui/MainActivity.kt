package com.axel_stein.ap_diary.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.data.AppSettings
import com.axel_stein.ap_diary.data.google_drive.DriveWorker
import com.axel_stein.ap_diary.data.google_drive.GoogleDriveService
import com.axel_stein.ap_diary.databinding.ActivityMainBinding
import com.axel_stein.ap_diary.ui.home.HomeFragment
import com.axel_stein.ap_diary.ui.utils.setVisible
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HomeFragment.YearMonthListCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val destinationListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            binding.spinnerDate.setVisible(destination.id == R.id.home_fragment)
        }
    private lateinit var driveService: GoogleDriveService
    private lateinit var settings: AppSettings

    init {
        App.appComponent.inject(this)
    }

    @Inject
    fun inject(d: GoogleDriveService, s: AppSettings) {
        driveService = d
        settings = s
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

        binding.toolbar.setVisible(!settings.showSplashScreen())
        binding.splash.setVisible(settings.showSplashScreen())
        binding.signInGoogle.setOnClickListener {
            signInGoogle()
        }
        binding.skip.setOnClickListener {
            skip()
        }
    }

    private fun signInGoogle() {
        if (!driveService.hasPermissions()) {
            driveService.requestPermissions(this, REQUEST_PERMISSIONS_CODE)
        } else {
            skip()
        }
    }

    private fun skip() {
        binding.toolbar.setVisible(true)
        binding.splash.setVisible(false)
        settings.setShowSplashScreen(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                enableAutoSync(true)
                skip()
                with(binding.splash) {
                    Snackbar.make(this, R.string.msg_sign_google_success, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    // fixme
    fun enableAutoSync(enable: Boolean) {
        val tag = "$packageName.drive_worker"
        val wm = WorkManager.getInstance(this)
        if (enable) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<DriveWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .addTag(tag)
                .build()
            wm.enqueue(request)
        } else {
            wm.cancelAllWorkByTag(tag)
        }
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

    companion object {
        const val REQUEST_PERMISSIONS_CODE = 1
    }
}