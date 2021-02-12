package com.axel_stein.ap_diary.ui.edit_ap

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.data.room.dao.ApLogDao
import com.axel_stein.ap_diary.data.room.model.ApLog
import com.axel_stein.ap_diary.ui.App
import com.axel_stein.ap_diary.ui.edit_ap.ApCategory.NONE
import com.axel_stein.ap_diary.ui.utils.Event
import com.axel_stein.ap_diary.ui.utils.get
import com.axel_stein.ap_diary.ui.utils.hasValue
import com.axel_stein.ap_diary.ui.utils.set
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject
import kotlin.math.max

class EditApViewModel(private val id: Long = 0L, state: SavedStateHandle, app: App) :
    AndroidViewModel(app) {

    private val apData = state.getLiveData<ApLog>("ap")
    val apLiveData: LiveData<ApLog> = apData

    private val categoryData = MutableLiveData<ApCategory>()
    val categoryLiveData: LiveData<ApCategory> = categoryData

    private val errorSystolicEmpty = MutableLiveData<Boolean>()
    val errorSystolicEmptyLiveData: LiveData<Boolean> = errorSystolicEmpty

    private val errorDiastolicEmpty = MutableLiveData<Boolean>()
    val errorDiastolicEmptyLiveData: LiveData<Boolean> = errorDiastolicEmpty

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private lateinit var dao: ApLogDao
    private val disposables = CompositeDisposable()

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun inject(dao: ApLogDao) {
        this.dao = dao
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        val ap = apData.get()
        val date = ap.dateTime.toMutableDateTime()
        date.year = year
        date.monthOfYear = month
        date.dayOfMonth = dayOfMonth
        ap.dateTime = date.toDateTime()
    }

    fun setTime(hourOfDay: Int, minuteOfHour: Int) {
        val ap = apData.get()
        val date = ap.dateTime.toMutableDateTime()
        date.hourOfDay = hourOfDay
        date.minuteOfHour = minuteOfHour
        ap.dateTime = date.toDateTime()
    }

    fun getCurrentDateTime() = apData.value!!.dateTime

    fun setSystolic(s: String) {
        with(apData.get()) {
            systolic = try {
                s.toInt()
            } catch (e : NumberFormatException) {
                e.printStackTrace()
                0
            }
            if (systolic != 0) {
                errorSystolicEmpty.value = false
            }
        }
        checkAp()
    }

    fun setDiastolic(s: String) {
        with(apData.get()) {
            diastolic = try {
                s.toInt()
            } catch (e : NumberFormatException) {
                e.printStackTrace()
                0
            }
            if (diastolic != 0) {
                errorDiastolicEmpty.value = false
            }
        }
        checkAp()
    }

    private fun checkAp() {
        val (systolic, diastolic) = with(apData.get()) {
            this.systolic to this.diastolic
        }
        if (systolic == 0 || diastolic == 0) {
            categoryData.value = NONE
            return
        }

        val checkSystolic = when {
            systolic < 120 -> 0 // normal
            systolic in 120..129 -> 1 // elevated
            systolic in 130..139 -> 2 // stage 1
            systolic in 140..179 -> 3 // stage 2
            else -> 4 // crisis
        }
        val checkDiastolic = when {
            diastolic < 80 -> 0 // normal
            diastolic in 80..89 -> 2 // stage 1
            diastolic in 90..119 -> 3 // stage 2
            else -> 4 // crisis
        }

        val category = max(checkSystolic, checkDiastolic)
        categoryData.value = ApCategory.fromInt(category)
    }

    fun setComment(s: String) {
        with(apData.get()) {
            comment = s
        }
    }

    private fun loadData() {
        if (apData.hasValue()) return
        if (id == 0L) apData.value = ApLog()
        else dao.getById(id)
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({
                apData.value = it
            }, {
                it.printStackTrace()
                showMessage.value = Event(R.string.error_loading)
            })
    }

    private fun checkArguments(): Boolean {
        return with(apData.get()) {
            var result = true
            if (systolic == 0) {
                errorSystolicEmpty.set(true)
                result = false
            }
            if (diastolic == 0) {
                errorDiastolicEmpty.set(true)
                result = false
            }
            result
        }
    }

    fun save() {
        if (!checkArguments()) return
        Completable.fromAction {
            dao.upsert(apData.get())
        }.subscribeOn(io()).subscribe({
            showMessage.postValue(Event(R.string.msg_log_saved))
            actionFinish.postValue(Event())
        }, {
            it.printStackTrace()
            showMessage.postValue(Event(R.string.error_saving))
        }).also { disposables.add(it) }
    }

    fun delete() {
        if (id != 0L) dao.deleteById(id)
            .subscribeOn(io())
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