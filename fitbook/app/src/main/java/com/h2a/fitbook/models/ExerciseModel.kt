package com.h2a.fitbook.models

open class ExerciseModel(
    var id: String,
    var title: String,
    var duration: Int,
    var calories: Int,
    var image: String,
    var description: String,
    var steps: ArrayList<String>
) {}