package com.h2a.fitbook.viewmodels.schedule

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.models.ExerciseListItemModel
import com.h2a.fitbook.models.ScheduleModel
import com.h2a.fitbook.utils.CommonFunctions
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ScheduleViewModel : ViewModel() {
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    @SuppressLint("WeekBasedYear")
    fun getExerciseList(callback: (Boolean, ArrayList<ScheduleModel>) -> Unit) {
        val uid = auth.currentUser!!.uid
        val (weekStart, weekEnd) = CommonFunctions.getCurrentWeekRange()
        val weekCollection = "$weekStart-$weekEnd".replace("/", ".")

        firestore.collection("user_health").document(uid).collection(weekCollection).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    val scheduleList = arrayListOf<ScheduleModel>()

                    result.forEach {
                        val date = CommonFunctions.mapDateKeyToCalendarValue(it.id)
                        val curDate = CommonFunctions.getDateStringOfDateInWeek(date)

                        val totalExercise = if (it.data["exerciseCount"] != null) {
                            it.data["exerciseCount"] as Long
                        } else 0
                        val totalMinutes = if (it.data["totalWorkoutTime"] != null) {
                            (it.data["totalWorkoutTime"] as Long) / 60
                        } else 0

                        val totalCalories = if (it.data["consumedCalories"] != null) {
                            it.data["consumedCalories"].toString().toDouble()
                        } else 0
                        val schedule = ScheduleModel(curDate, totalExercise, totalMinutes)
                        if (schedule.totalExercises > 0) {
                            scheduleList.add(schedule)
                        }
                    }
                    val sorted = scheduleList.sortedBy {
                        LocalDate.parse(
                            it.date, DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        )
                    }
                    callback(true, ArrayList(sorted))
                } else {
                    callback(false, arrayListOf())
                    Log.i("Firebase", task.exception.toString())
                }
            }.addOnFailureListener {
                callback(false, arrayListOf())
                Log.i("Firebase", it.toString())
            }
    }
}