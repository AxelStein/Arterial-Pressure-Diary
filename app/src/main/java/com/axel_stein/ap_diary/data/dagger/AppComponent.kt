package com.axel_stein.ap_diary.data.dagger

import com.axel_stein.ap_diary.ui.MainActivity
import com.axel_stein.ap_diary.ui.edit_ap.EditApViewModel
import com.axel_stein.ap_diary.ui.edit_pulse.EditPulseViewModel
import com.axel_stein.ap_diary.ui.home.HomeViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(vm: HomeViewModel)
    fun inject(vm: EditApViewModel)
    fun inject(vm: EditPulseViewModel)
    fun inject(activity: MainActivity)
}