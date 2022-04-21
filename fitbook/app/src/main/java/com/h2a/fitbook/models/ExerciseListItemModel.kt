package com.h2a.fitbook.models

data class ExerciseListItemModel(
    var name: String = "",
    var measureCalories: Int = 0,
    var measureDuration: Int = 0,
    var image: String = "",
) {
    var id: String = ""
}