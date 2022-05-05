package com.h2a.fitbook.models

data class ExerciseDailyListItemModel(
    var duration: Long, var measureCalories: Double, var scheduleDate: String, var totalSet: Long
) {
    var id = ""
    var image = ""
    var name = ""
}