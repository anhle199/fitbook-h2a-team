package com.h2a.fitbook.utils

import android.annotation.SuppressLint
import com.h2a.fitbook.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*

object CommonFunctions {
    fun mapDateToShortDateText(date: DayOfWeek): Int {
        return when (date) {
            DayOfWeek.MONDAY -> R.string.short_date_text_monday
            DayOfWeek.TUESDAY -> R.string.short_date_text_tuesday
            DayOfWeek.WEDNESDAY -> R.string.short_date_text_wednesday
            DayOfWeek.THURSDAY -> R.string.short_date_text_thursday
            DayOfWeek.FRIDAY -> R.string.short_date_text_friday
            DayOfWeek.SATURDAY -> R.string.short_date_text_saturday
            DayOfWeek.SUNDAY -> R.string.short_date_text_sunday
            else -> R.string.short_date_text_invalid
        }
    }

    fun mapDateToFullDateText(date: DayOfWeek): Int {
        return when (date) {
            DayOfWeek.MONDAY -> R.string.full_date_text_monday
            DayOfWeek.TUESDAY -> R.string.full_date_text_tuesday
            DayOfWeek.WEDNESDAY -> R.string.full_date_text_wednesday
            DayOfWeek.THURSDAY -> R.string.full_date_text_thursday
            DayOfWeek.FRIDAY -> R.string.full_date_text_friday
            DayOfWeek.SATURDAY -> R.string.full_date_text_saturday
            DayOfWeek.SUNDAY -> R.string.full_date_text_sunday
            else -> R.string.full_date_text_invalid
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentWeekRange(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_WEEK] = Calendar.MONDAY

        val format: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        val start = format.format(calendar.time)
        for (i in 0..5) {
            calendar.add(Calendar.DATE, 1)
        }
        val end = format.format(calendar.time)
        return Pair(start, end)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateStringOfDateInWeek(date: Int): String {
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_WEEK] = date
        val format: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        return format.format(calendar.time)
    }

    fun mapDateKeyToCalendarValue(date: String): Int {
        return when (date) {
            "monday" -> Calendar.MONDAY
            "tuesday" -> Calendar.TUESDAY
            "wednesday" -> Calendar.WEDNESDAY
            "thursday" -> Calendar.THURSDAY
            "friday" -> Calendar.FRIDAY
            "saturday" -> Calendar.SATURDAY
            "sunday" -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }
    }

    fun mapCalendarValueToDateKey(date: Int): String {
        return when (date) {
            Calendar.MONDAY -> "monday"
            Calendar.TUESDAY -> "tuesday"
            Calendar.WEDNESDAY -> "wednesday"
            Calendar.THURSDAY -> "thursday"
            Calendar.FRIDAY -> "friday"
            Calendar.SATURDAY -> "saturday"
            Calendar.SUNDAY -> "sunday"
            else -> ""
        }
    }
}