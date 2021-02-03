package com.axel_stein.ap_diary.data.dagger

import com.axel_stein.ap_diary.ui.home.HomeViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(vm: HomeViewModel)
}