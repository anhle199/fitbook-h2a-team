package com.h2a.fitbook.models

data class ScheduleStatisticModel(
    val actualWorkoutTime: Float = 0f,
    val measureCalories: Float = 0f,
    var name: String = "",
) {
    val calories get() = calculateCalories()
    private fun calculateCalories(): Float = measureCalories * actualWorkoutTime
}
