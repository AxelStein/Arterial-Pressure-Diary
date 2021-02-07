package com.axel_stein.ap_diary.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.axel_stein.ap_diary.ui.App

@Suppress("UNCHECKED_CAST")
class MainPreferencesFactory(private val app: App) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainPreferencesViewModel::class.java)) {
            return MainPreferencesViewModel(app) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}