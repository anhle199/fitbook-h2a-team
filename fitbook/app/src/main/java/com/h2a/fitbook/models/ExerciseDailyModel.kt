package com.h2a.fitbook.models

class ExerciseDailyModel(
    id: String,
    title: String,
    duration: Int,
    calories: Int,
    image: String,
    description: String,
    steps: ArrayList<String>,
    var totalDuration: Int,
    var totalCalories: Int,
    var scheduleDate: String
) : ExerciseModel(id, title, duration, calories, image, description, steps) {}