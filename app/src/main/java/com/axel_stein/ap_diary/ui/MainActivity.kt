package com.axel_stein.ap_diary.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.axel_stein.ap_diary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
    }
}