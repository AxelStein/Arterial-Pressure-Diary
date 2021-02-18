package com.axel_stein.ap_diary.ui.edit_ap

enum class ApCategory {
    NORMAL,
    HIGH_NORMAL,
    GRADE_1,
    GRADE_2,
    NONE;

    companion object {
        fun fromInt(value: Int) = values().first { it.ordinal == value }
    }
}