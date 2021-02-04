package com.axel_stein.ap_diary.ui.edit_pulse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.data.room.dao.PulseLogDao
import com.axel_stein.ap_diary.data.room.model.PulseLog
import com.axel_stein.ap_diary.ui.App
import com.axel_stein.ap_diary.ui.utils.Event
import com.axel_stein.ap_diary.ui.utils.get
import com.axel_stein.ap_diary.ui.utils.hasValue
import com.axel_stein.ap_diary.ui.utils.set
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class EditPulseViewModel(private val id: Long = 0L, state: SavedStateHandle) : ViewModel() {
    private var pulseData = state.getLiveData<PulseLog>("pulse")
    val pulseLiveData: LiveData<PulseLog> = pulseData

    private val errorValueEmpty = MutableLiveData<Boolean>()
    val errorValueEmptyLiveData: LiveData<Boolean> = errorValueEmpty

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private lateinit var dao: PulseLogDao
    private val disposables = CompositeDisposable()

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun inject(dao: PulseLogDao) {
        this.dao = dao
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        val pulse = pulseData.get()
        val date = pulse.dateTime.toMutableDateTime()
        date.year = year
        date.monthOfYear = month
        date.dayOfMonth = dayOfMonth
        pulse.dateTime = date.toDateTime()
    }

    fun setTime(hourOfDay: Int, minuteOfHour: Int) {
        val ap = pulseData.get()
        val date = ap.dateTime.toMutableDateTime()
        date.hourOfDay = hourOfDay
        date.minuteOfHour = minuteOfHour
        ap.dateTime = date.toDateTime()
    }

    fun getCurrentDateTime() = pulseData.value!!.dateTime

    fun setPulse(s: String) {
        with(pulseData.get()) {
            value = try {
                s.toInt()
            } catch (e : NumberFormatException) {
                e.printStackTrace()
                0
            }
            if (value != 0) {
                errorValueEmpty.value = false
            }
        }
    }

    fun setComment(s: String) {
        with(pulseData.get()) {
            comment = s
        }
    }

    private fun loadData() {
        if (pulseData.hasValue()) return
        if (id == 0L) pulseData.value = PulseLog()
        else dao.getById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                pulseData.set(it)
            }, {
                it.printStackTrace()
                showMessage.value = Event(R.string.error_loading)
            })
    }

    private fun checkArguments(): Boolean {
        return with(pulseData.get()) {
            var result = true
            if (value == 0) {
                errorValueEmpty.set(true)
                result = false
            }
            result
        }
    }

    fun save() {
        if (!checkArguments()) return
        Completable.fromAction {
            dao.upsert(pulseData.get())
        }.subscribeOn(Schedulers.io()).subscribe({
            showMessage.postValue(Event(R.string.msg_log_saved))
            actionFinish.postValue(Event())
        }, {
            it.printStackTrace()
            showMessage.postValue(Event(R.string.error_saving))
        }).also { disposables.add(it) }
    }

    fun delete() {
        if (id != 0L) dao.deleteById(id)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    showMessage.postValue(Event(R.string.msg_log_deleted))
                    actionFinish.postValue(Event())
                },
                {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_deleting))
                }
            )
    }
}