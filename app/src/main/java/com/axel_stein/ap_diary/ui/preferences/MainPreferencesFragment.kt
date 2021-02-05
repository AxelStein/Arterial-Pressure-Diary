package com.axel_stein.ap_diary.ui.preferences

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.preference.PreferenceFragmentCompat
import com.axel_stein.ap_diary.R

class MainPreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }
}