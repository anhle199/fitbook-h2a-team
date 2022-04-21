package com.h2a.fitbook.models

data class ExerciseDetailModel(
    var description: String = "",
    var refId: String = "",
    var steps: ArrayList<String> = arrayListOf(),
) {
    var id: String? = null
    var name: String = ""
    var measureDuration: Int = 0
    var measureCalories: Int = 0
    var image: String = ""
}