package com.axel_stein.ap_diary.data.dagger

import com.axel_stein.ap_diary.data.backup.BackupHelper
import com.axel_stein.ap_diary.ui.MainActivity
import com.axel_stein.ap_diary.ui.charts.ChartsViewModel
import com.axel_stein.ap_diary.ui.charts.helpers.ChartData
import com.axel_stein.ap_diary.ui.charts.helpers.LineDataInflater
import com.axel_stein.ap_diary.ui.edit_ap.EditApViewModel
import com.axel_stein.ap_diary.ui.edit_pulse.EditPulseViewModel
import com.axel_stein.ap_diary.ui.home.HomeViewModel
import com.axel_stein.ap_diary.ui.preferences.MainPreferencesFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(vm: HomeViewModel)
    fun inject(vm: EditApViewModel)
    fun inject(vm: EditPulseViewModel)
    fun inject(backupHelper: BackupHelper)
    fun inject(fragment: MainPreferencesFragment)
    fun inject(chartData: ChartData)
    fun inject(lineDataInflater: LineDataInflater)
    fun inject(vm: ChartsViewModel)
    fun inject(activity: MainActivity)
}