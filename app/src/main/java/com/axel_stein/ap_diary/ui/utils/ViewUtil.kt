package com.axel_stein.ap_diary.ui.utils

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.core.widget.doAfterTextChanged
import com.axel_stein.ap_diary.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun TextInputLayout.showError(error: Boolean, msg: Int) {
    if (error) {
        setError(context.getString(msg))
        editText?.showKeyboard()
    }
    isErrorEnabled = error
}

fun TextInputEditText.setupEditor(onTextChanged: (String) -> Unit) {
    doAfterTextChanged { onTextChanged(text.toString()) }
    setOnEditorActionListener { _, actionId, _ ->
        var consumed = false
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()
            consumed = true
        }
        consumed
    }
    setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) hideKeyboard()
    }
}

fun TextInputEditText.setEditorText(text: String, handleKeyboard: Boolean = true) {
    val current = this.text.toString()
    if (current != text) {
        setText(text)
        setSelection(length())
    }
    if (handleKeyboard) {
        if (text.isBlank()) {
            showKeyboardDelayed(resources.getInteger(R.integer.axis_duration))
        } else if (!isFocused) {
            hideKeyboard()
        }
    }
}

fun setItemSelectedListener(callback: (position: Int) -> Unit): AdapterView.OnItemSelectedListener {
    return object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            callback(position)
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}