package com.axel_stein.ap_diary.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val items = MutableLiveData<List<Any>>()
}