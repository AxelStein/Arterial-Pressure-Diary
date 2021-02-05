package com.axel_stein.ap_diary.ui.edit_ap

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.axel_stein.ap_diary.ui.App

@Suppress("UNCHECKED_CAST")
class EditApFactory(
    private val context: Context,
    owner: SavedStateRegistryOwner,
    private val id: Long,
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T = EditApViewModel(id, handle, context.applicationContext as App) as T
}