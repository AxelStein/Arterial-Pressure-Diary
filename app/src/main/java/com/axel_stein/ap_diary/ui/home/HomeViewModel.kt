package com.axel_stein.ap_diary.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.ap_diary.data.room.repository.LogRepository
import com.axel_stein.ap_diary.data.room.repository.LogRepository.LogListResult
import com.axel_stein.ap_diary.ui.App
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class HomeViewModel : ViewModel() {
    private lateinit var repository: LogRepository
    private val disposables = CompositeDisposable()
    private val _items = MutableLiveData<LogListResult>()
    val items: LiveData<LogListResult> = _items

    @Inject
    fun inject(r: LogRepository) {
        repository = r
    }

    init {
        App.appComponent.inject(this)
        repository.getLogsByYearMonth("2021-02")
            .subscribeOn(io())
            .subscribe({
                _items.postValue(it)
            }, {
                it.printStackTrace()
            }).also {
                disposables.add(it)
            }
    }
}