package com.h2a.fitbook.viewmodels.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.h2a.fitbook.utils.CommonFunctions
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleTimerViewModel : ViewModel() {
    var totalSet = 0L
    var id: String = ""
    var duration = 0L

    var actualSeconds = 0L
    var actualSets = 0L
    var timer = 0L

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    fun saveResult(callback: (Boolean) -> Unit) {
        val uid = auth.currentUser!!.uid
        val (weekStart, weekEnd) = CommonFunctions.getCurrentWeekRange()
        val weekCollection = "$weekStart-$weekEnd".replace("/", ".")
        val dateOfWeek =
            CommonFunctions.mapCalendarValueToDateKey(LocalDate.now().dayOfWeek.value + 1)

        val ref = firestore.collection("user_health").document(uid).collection(weekCollection)
            .document(dateOfWeek).collection("schedule").document(id)
        ref.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val data = it.result.data
                val scheduleDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                var actualSet = 0L
                var actualWorkoutTime = 0L
                if (data != null) {
                    actualSet += data["actualSet"].toString().toLong()
                    actualWorkoutTime += data["actualWorkoutTime"].toString().toLong()
                }

                actualSet += actualSets
                actualWorkoutTime += actualSeconds

                ref.set(
                    hashMapOf(
                        "scheduleDate" to scheduleDate,
                        "actualSet" to actualSet,
                        "actualWorkoutTime" to actualWorkoutTime
                    ), SetOptions.merge()
                ).addOnCompleteListener { itInner ->
                    if (itInner.isSuccessful) {
                        callback(true)
                    } else {
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