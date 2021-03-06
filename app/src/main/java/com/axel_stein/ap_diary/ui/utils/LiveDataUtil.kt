package com.axel_stein.ap_diary.ui.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.joda.time.DateTime
import org.joda.time.MutableDateTime

fun <T> MutableLiveData<T>.get() = value!!

fun <T> MutableLiveData<T>.set(value: T) {
    this.value = value
}

fun <T> MutableLiveData<T>.getOrDefault(default: T) = value ?: default

fun MutableLiveData<String>.notBlankOrDefault(default: String) =
    if (value.isNullOrBlank()) default
    else value ?: ""

fun MutableLiveData<MutableDateTime>.getOrDefault() = value ?: MutableDateTime()

fun MutableLiveData<MutableDateTime>.getOrDateTime(): DateTime = value?.toDateTime() ?: DateTime()

fun <T> MutableLiveData<T>.hasValue() = value != null

fun <T> LiveData<T>.get() = value!!

fun <T> LiveData<T>.getOrDefault(default: T) = value ?: default

fun LiveData<MutableDateTime>.getOrDefault() = value ?: MutableDateTime()

fun LiveData<MutableDateTime>.getOrDateTime(): DateTime = value?.toDateTime() ?: DateTime()

fun <T> LiveData<T>.hasValue() = value != null