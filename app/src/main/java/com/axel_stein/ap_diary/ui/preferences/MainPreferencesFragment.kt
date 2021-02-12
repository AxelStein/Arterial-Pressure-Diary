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
import com.axel_stein.ap_diary.ui.utils.formatDateTime
import com.axel_stein.ap_diary.ui.utils.formatTime
import com.axel_stein.ap_diary.ui.utils.setVisible
import com.axel_stein.ap_diary.ui.utils.showTimePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.MaterialSharedAxis.Z
import org.joda.time.DateTime
import org.joda.time.LocalTime
import javax.inject.Inject

class MainPreferencesFragment : PreferenceFragmentCompat(), OnConfirmListener {
    private val viewModel: MainPreferencesViewModel by viewModels {
        MainPreferencesFactory(requireContext().applicationContext as App)
    }
    private lateinit var settings: AppSettings
    private var lastSynced: Preference? = null

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
        setupReminder(reminderMorning, settings::setReminderMorningTime, settings::getReminderMorningTime)

        val reminderEvening = findPreference<SwitchPreference>("reminder_evening")
        setupReminder(reminderEvening, settings::setReminderEveningTime, settings::getReminderEveningTime)

        findPreference<Preference>("export_backup")?.apply {
            setOnPreferenceClickListener {
                viewModel.startExportToFile()
                    .subscribe({
                        startActivity(Intent.createChooser(it, null))
                    }, {
                        it.printStackTrace()
                        showMessage(R.string.error_create_backup)
                    })
                true
            }
        }

        preferenceManager.findPreference<Preference>("import_backup")?.apply {
            setOnPreferenceClickListener {
                ConfirmDialog.Builder()
                    .from(this@MainPreferencesFragment, "import_backup")
                    .title(R.string.dialog_title_confirm)
                    .message(R.string.message_import_backup)
                    .positiveBtnText(R.string.action_import)
                    .negativeBtnText(R.string.action_cancel)
                    .show()
                true
            }
        }

        preferenceManager.findPreference<Preference>("google_create_backup")?.apply {
            setOnPreferenceClickListener {
                ConfirmDialog.Builder()
                    .from(this@MainPreferencesFragment, "google_create_backup")
                    .title(R.string.dialog_title_confirm)
                    .message(R.string.message_drive_export)
                    .positiveBtnText(R.string.action_create)
                    .negativeBtnText(R.string.action_cancel)
                    .show()
                true
            }
        }

        preferenceManager.findPreference<Preference>("google_import_backup")?.apply {
            setOnPreferenceClickListener {
                ConfirmDialog.Builder()
                    .from(this@MainPreferencesFragment, "google_import_backup")
                    .title(R.string.dialog_title_confirm)
                    .message(R.string.message_import_backup)
                    .positiveBtnText(R.string.action_import)
                    .negativeBtnText(R.string.action_cancel)
                    .show()
                true
            }
        }

        lastSynced = preferenceManager.findPreference("google_last_sync")

        viewModel.lastSyncTimeLiveData.observe(viewLifecycleOwner, { time ->
            if (time > 0) {
                lastSynced?.summary = formatDateTime(requireContext(), DateTime(time), false)
                lastSynced?.isVisible = true
            }
        })

        viewModel.messageLiveData.observe(viewLifecycleOwner, {
            val msg = it.getContent()
            if (msg != null) {
                showMessage(msg)
            }
        })

        viewModel.showProgressBarLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.setVisible(it)
        }
    }

    private fun setupReminder(
        pref: SwitchPreference?,
        setTime: (hours: Int, minutes: Int) -> Unit,
        getTime: () -> LocalTime
    ) {
        pref?.apply {
            val updateSummaryOn = {
                if (isChecked) {
                    val time = getTime()
                    summaryOn = formatTime(requireContext(), time.hourOfDay, time.minuteOfHour)
                }
            }
            updateSummaryOn()

            setOnPreferenceClickListener {
                updateSummaryOn()
                if (isChecked) {
                    showTimePicker(requireContext(), getTime()) { hourOfDay, minuteOfHour ->
                        setTime(hourOfDay, minuteOfHour)
                        updateSummaryOn()
                    }
                }
                true
            }
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
            "google_create_backup" -> viewModel.driveCreateBackup(this)
            "google_import_backup" -> viewModel.driveImportBackup(this)
        }
    }
}