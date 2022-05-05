package com.h2a.fitbook.viewmodels.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.models.ExerciseDailyListItemModel
import com.h2a.fitbook.models.ExerciseListItemModel
import com.h2a.fitbook.utils.CommonFunctions
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleDailyViewModel : ViewModel() {
    var currentDate: String = ""

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    fun getScheduleDailyList(callback: (Boolean, data: ArrayList<ExerciseDailyListItemModel>) -> Unit) {
        val uid = auth.currentUser!!.uid
        val (weekStart, weekEnd) = CommonFunctions.getCurrentWeekRange()
        val weekCollection = "$weekStart-$weekEnd".replace("/", ".")
        val dateOfWeek = CommonFunctions.mapCalendarValueToDateKey(
            LocalDate.parse(
                currentDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")
            ).dayOfWeek.value + 1
        )

        firestore.collection("user_health").document(uid).collection(weekCollection)
            .document(dateOfWeek).collection("schedule").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result
                    val list = arrayListOf<ExerciseDailyListItemModel>()
                    val idList = arrayListOf<String>()

                    for (dataRow in data) {
                        val item = ExerciseDailyListItemModel(
                            dataRow["duration"] as Long,
                            dataRow["measureCalories"] as Double,
                            dataRow["scheduleDate"] as String,
                            dataRow["totalSet"] as Long
                        )
                        item.id = dataRow.id
                        idList.add(item.id)
                        list.add(item)
                    }
                    firestore.collection("exercises").get().addOnCompleteListener { exerciseTask ->
                            if (exerciseTask.isSuccessful) {
                                val exerciseList = arrayListOf<ExerciseListItemModel>()
                                for (document in exerciseTask.result) {
                                    val item = document.toObject(ExerciseListItemModel::class.java)
                                    item.id = document.id
                                    exerciseList.add(item)
                                }

                                for (item in list) {
                                    // find in new list
                                    val filteredItem = exerciseList.find { itt -> itt.id == item.id }
                                    item.image = filteredItem?.image ?: ""
                                    item.name = filteredItem?.name ?: ""
                                }
                                callback(true, list)
                            } else {
                                Log.i("Firebase", exerciseTask.exception.toString())
                                callback(false, arrayListOf())
                            }
                        }
                } else {
                    Log.i("Firebase", it.exception.toString())
                    callback(false, arrayListOf())
                }
            }
    }
}