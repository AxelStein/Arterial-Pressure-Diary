package com.axel_stein.ap_diary.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.axel_stein.ap_diary.data.AppResources
import com.axel_stein.ap_diary.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var appResources: AppResources

    @Inject
    fun inject(resources: AppResources) {
        this.appResources = resources
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        appResources.initColorResources(this)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
    }
}