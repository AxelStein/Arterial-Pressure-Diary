package com.axel_stein.ap_diary.ui.edit_ap

enum class ApCategory {
    NORMAL,
    ELEVATED,
    STAGE_1,
    STAGE_2,
    CRISIS,
    NONE;

    companion object {
        fun fromInt(value: Int) = values().first { it.ordinal == value }
    }
}