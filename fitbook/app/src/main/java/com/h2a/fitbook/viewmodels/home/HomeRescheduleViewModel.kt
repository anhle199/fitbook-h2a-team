package com.h2a.fitbook.viewmodels.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.h2a.fitbook.utils.CommonFunctions
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HomeRescheduleViewModel : ViewModel() {
    var duration: Int = 0
    var set: Int = 0
    var date: String = ""
    var time: String = ""
    var id: String = ""
    var measureDuration: Int = 0
    var measureCalories: Double = 0.0
    var name: String = ""

    var mode: String = "CREATE"
    var oldDuration: Int = 0
    var oldSet: Int = 0

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    fun scheduleAnExercise(callback: (Boolean) -> Unit) {
        val uid = auth.currentUser!!.uid
        val (start, end) = CommonFunctions.getCurrentWeekRange()
        val weekCollection = "$start-$end".replace("/", ".")
        val curDate =
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).dayOfWeek.value

        firestore.collection("user_health").document(uid).collection(weekCollection)
            .document(CommonFunctions.mapCalendarValueToDateKey(curDate + 1)).collection("schedule")
            .document(id).set(
                hashMapOf(
                    "name" to name,
                    "actualSet" to 0,
                    "actualWorkoutTime" to 0,
                    "duration" to (duration * 60),
                    "measureCalories" to (measureCalories / measureDuration),
                    "totalSet" to set,
                    "scheduleDate" to LocalDateTime.parse(
                        "$date $time", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                ) as Map<String, Any>
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    firestore.collection("user_health").document(uid).collection(weekCollection)
                        .document(CommonFunctions.mapCalendarValueToDateKey(curDate + 1)).get()
                        .addOnCompleteListener { subtask ->
                            if (subtask.isSuccessful) {
                                var totalWorkoutTime: Long = 0
                                var exerciseCount: Long = 0
                                var consumedCalories = 0.0

                                if (subtask.result.data != null) {
                                    totalWorkoutTime =
                                        subtask.result.data!!["totalWorkoutTime"] as Long
                                    exerciseCount = subtask.result.data!!["exerciseCount"] as Long
                                    consumedCalories =
                                        subtask.result.data!!["consumedCalories"] as Double
                                }
                                ++exerciseCount
                                totalWorkoutTime += (duration * 60 * set)
                                consumedCalories += measureCalories / measureDuration * duration * 60 * set

                                firestore.collection("user_health").document(uid)
                                    .collection(weekCollection)
                                    .document(CommonFunctions.mapCalendarValueToDateKey(curDate + 1))
                                    .set(
                                        hashMapOf(
                                            "exerciseCount" to exerciseCount,
                                            "totalWorkoutTime" to totalWorkoutTime,
                                            "consumedCalories" to consumedCalories,
                                        ), SetOptions.merge()
                                    ).addOnCompleteListener { subtaskInner ->
                                        if (subtaskInner.isSuccessful) callback(true) else {
                                            Log.i("Firebase", subtaskInner.exception.toString())
                                            callback(false)
                                        }
                                    }
                            } else {
                                Log.i("Firebase", subtask.exception.toString())
                                callback(false)
                            }
                        }
                } else {
                    Log.i("Firebase", it.exception.toString())
                    callback(false)
                }
            }
    }

    fun editAnExercise(callback: (Boolean) -> Unit) {
        val uid = auth.currentUser!!.uid
        val (start, end) = CommonFunctions.getCurrentWeekRange()
        val weekCollection = "$start-$end".replace("/", ".")
        val curDate =
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).dayOfWeek.value
        val curDateKey = CommonFunctions.mapCalendarValueToDateKey(curDate + 1)

        firestore.collection("user_health").document(uid).collection(weekCollection)
            .document(curDateKey).collection("schedule").document(id).set(
                hashMapOf(
                    "duration" to (duration * 60),
                    "totalSet" to set,
                    "scheduleDate" to LocalDateTime.parse(
                        "$date $time", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                ) as Map<String, Any>, SetOptions.merge()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    firestore.collection("user_health").document(uid).collection(weekCollection)
                        .document(curDateKey).get().addOnCompleteListener { subtask ->
                            if (subtask.isSuccessful) {
                                var totalWorkoutTime: Long = 0
                                var consumedCalories = 0.0

                                if (subtask.result.data != null) {
                                    totalWorkoutTime =
                                        subtask.result.data!!["totalWorkoutTime"] as Long
                                    consumedCalories =
                                        subtask.result.data!!["consumedCalories"] as Double
                                }
                                totalWorkoutTime += (duration * 60 * set) - (oldDuration * 60 * oldSet)
                                consumedCalories += (measureCalories / measureDuration * duration * 60 * set) - (measureCalories / measureDuration * oldDuration * 60 * oldSet)

                                firestore.collection("user_health").document(uid)
                                    .collection(weekCollection).document(curDateKey).set(
                                        hashMapOf(
                                            "totalWorkoutTime" to totalWorkoutTime,
                                            "consumedCalories" to consumedCalories,
                                        ), SetOptions.merge()
                                    ).addOnCompleteListener { subtaskInner ->
                                        if (subtaskInner.isSuccessful) {
                                            callback(true)
                                        } else {
                                            Log.i("Firebase", subtaskInner.exception.toString())
                                            callback(false)
                                        }
                                    }
                            } else {
                                Log.i("Firebase", subtask.exception.toString())
                                callback(false)
                            }
                        }
                } else {
                    Log.i("Firebase", it.exception.toString())
                    callback(false)
                }
            }
    }
}