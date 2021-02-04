package com.axel_stein.ap_diary.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.data.AppResources
import com.axel_stein.ap_diary.data.room.repository.LogRepository
import com.axel_stein.ap_diary.data.room.repository.LogRepository.LogListResult
import com.axel_stein.ap_diary.ui.App
import com.axel_stein.ap_diary.ui.utils.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

@SuppressLint("CheckResult")
class HomeViewModel : ViewModel() {
    private lateinit var repository: LogRepository
    private lateinit var resources: AppResources
    private val disposables = CompositeDisposable()

    private val _itemsData = MutableLiveData<LogListResult>()
    val itemsLiveData: LiveData<LogListResult> = _itemsData

    private var yearMonthList = listOf<String>()
    var selectedYearMonth = -1
    private var forceUpdate = true

    private val _yearMonthListData = MutableLiveData<List<String>>()
    val yearMonthListLiveData: LiveData<List<String>> = _yearMonthListData

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    @Inject
    fun inject(r: LogRepository, res: AppResources) {
        repository = r
        resources = res
    }

    fun selectYearMonth(pos: Int) {
        if (selectedYearMonth == pos && !forceUpdate) return
        forceUpdate = false

        disposables.clear()
        selectedYearMonth = pos

        yearMonthList[pos].run {
            repository.getLogsByYearMonth(this)
                .subscribeOn(io())
                .subscribe({
                    _itemsData.postValue(it)
                }, {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_loading))
                }).also {
                    disposables.add(it)
                }
        }
    }

    init {
        App.appComponent.inject(this)
        repository.getYearMonthList()
            .subscribeOn(io())
            .subscribe({
                updateYearMonthList(it)
                it.map { ym ->
                    val (year, month) = ym.split("-")
                    val monthFormatted = resources.months[month.toInt() - 1]
                    "$monthFormatted $year"
                }.run {
                    _yearMonthListData.postValue(this)
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun updateYearMonthList(newList: List<String>) {
        var index = selectedYearMonth
        if (yearMonthList.isNotEmpty()) { // update
            if (newList.isNotEmpty()) {
                val current = yearMonthList[index]
                index = newList.indexOf(current)
                if (index == -1) index = 0
            }
        } else if (newList.isNotEmpty()) { // new
            index = 0
        }
        forceUpdate = true
        yearMonthList = newList
        selectedYearMonth = index
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}