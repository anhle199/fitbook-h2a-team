package com.h2a.fitbook.models


data class FoodStatisticModel(
    val caloriesPerUnit: Float = 0f,
    val quantity: Float = 0f,
) {
    val calories get() = calculateCalories()
    private fun calculateCalories(): Float = quantity * caloriesPerUnit
}
