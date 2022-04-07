package com.h2a.fitbook.utils

import com.h2a.fitbook.R
import java.time.DayOfWeek

object CommonFunctions {
    fun mapDateToShortDateText(date: Int): Int {
        return when (date) {
            2 -> R.string.short_date_text_monday
            3 -> R.string.short_date_text_tuesday
            4 -> R.string.short_date_text_wednesday
            5 -> R.string.short_date_text_thursday
            6 -> R.string.short_date_text_friday
            7 -> R.string.short_date_text_saturday
            8 -> R.string.short_date_text_sunday
            else -> R.string.short_date_text_invalid
        }
    }

    fun mapDateToFullDateText(date: Int): Int {
        return when (date) {
            2 -> R.string.full_date_text_monday
            3 -> R.string.full_date_text_tuesday
            4 -> R.string.full_date_text_wednesday
            5 -> R.string.full_date_text_thursday
            6 -> R.string.full_date_text_friday
            7 -> R.string.full_date_text_saturday
            8 -> R.string.full_date_text_sunday
            else -> R.string.full_date_text_invalid
        }
    }
}