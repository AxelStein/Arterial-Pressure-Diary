package com.axel_stein.ap_diary.ui.preferences

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.data.AppSettings
import com.axel_stein.ap_diary.databinding.FragmentMainPrefsBinding
import com.axel_stein.ap_diary.ui.App
import com.axel_stein.ap_diary.ui.dialogs.ConfirmDialog
import com.axel_stein.ap_diary.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.ap_diary.ui.preferences.MainPreferencesViewModel.Companion.CODE_PICK_FILE
import com.axel_stein.ap_diary.ui.utils.formatTime
import com.axel_stein.ap_diary.ui.utils.showTimePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.MaterialSharedAxis.Z
import org.joda.time.DateTime
import javax.inject.Inject

class MainPreferencesFragment : PreferenceFragmentCompat(), OnConfirmListener {
    private val viewModel: MainPreferencesViewModel by viewModels {
        MainPreferencesFactory(requireContext().applicationContext as App)
    }
    private lateinit var settings: AppSettings

    init {
        App.appComponent.inject(this)
    }

    @Inject
    fun inject(s: AppSettings) {
        settings = s
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(Z, true).apply {
            duration = resources.getInteger(R.integer.axis_duration).toLong()
        }
        returnTransition = MaterialSharedAxis(Z, false).apply {
            duration = resources.getInteger(R.integer.axis_duration).toLong()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMainPrefsBinding.bind(view)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val theme = preferenceManager.findPreference<ListPreference>("theme")
        theme?.setOnPreferenceChangeListener { _, value ->
            settings.applyTheme(value as String)
            true
        }

        val reminderMorning = findPreference<SwitchPreference>("reminder_morning")
        reminderMorning?.apply {
            setOnPreferenceClickListener {
                if (isChecked) {
                    showTimePicker(requireContext(), DateTime()) { hourOfDay, minuteOfHour ->
                        reminderMorning.summaryOn = formatTime(requireContext(), hourOfDay, minuteOfHour)
                    }
                }
                true
            }
        }

        val reminderEvening = findPreference<SwitchPreference>("reminder_evening")
        reminderEvening?.apply {
            setOnPreferenceClickListener {
                if (isChecked) {
                    showTimePicker(requireContext(), DateTime()) { hourOfDay, minuteOfHour ->
                        reminderEvening.summaryOn = formatTime(requireContext(), hourOfDay, minuteOfHour)
                    }
                }
                true
            }
        }

        val exportBackup = findPreference<Preference>("export_backup")
        exportBackup?.setOnPreferenceClickListener {
            viewModel.startExportToFile()
                .subscribe({
                    startActivity(Intent.createChooser(it, null))
                }, {
                    it.printStackTrace()
                    showMessage(R.string.error_create_backup)
                })
            true
        }

        val importBackup = preferenceManager.findPreference<Preference>("import_backup")
        importBackup?.setOnPreferenceClickListener {
            ConfirmDialog.Builder()
                .from(this, "import_backup")
                .title(R.string.dialog_title_confirm)
                .message(R.string.message_import_backup)
                .positiveBtnText(R.string.action_import)
                .negativeBtnText(R.string.action_cancel)
                .show()
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }

    private fun showMessage(message: Int) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onConfirm(tag: String?) {
        when (tag) {
            "import_backup" -> startActivityForResult(viewModel.startImportFromFile(), CODE_PICK_FILE)
        }
    }
}