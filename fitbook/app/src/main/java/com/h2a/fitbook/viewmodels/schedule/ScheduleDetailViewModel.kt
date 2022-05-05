package com.h2a.fitbook.viewmodels.schedule

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.h2a.fitbook.models.ExerciseDetailModel
import com.h2a.fitbook.models.ExerciseListItemModel
import com.h2a.fitbook.utils.CommonFunctions
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleDetailViewModel : ViewModel() {
    private val _thumbnail: MutableLiveData<String> = MutableLiveData()
    private val _title: MutableLiveData<String> = MutableLiveData()
    private val _detail: MutableLiveData<String> = MutableLiveData()
    private val _description: MutableLiveData<String> = MutableLiveData()
    private val _steps: MutableLiveData<ArrayList<String>> = MutableLiveData(arrayListOf())
    private val _schedule: MutableLiveData<String> = MutableLiveData()

    val thumbnail: LiveData<String> = _thumbnail
    val title: LiveData<String> = _title
    val detail: LiveData<String> = _detail
    val description: LiveData<String> = _description
    val steps: LiveData<ArrayList<String>> = _steps
    val schedule: LiveData<String> = _schedule

    var id = ""
    var measureDuration: Long = 0
    var measureCalories: Double = 0.0
    var scheduleDate: String = ""
    var totalSet: Long = 0

    fun setSchedule(str: String) {
        _schedule.value = str
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    fun getScheduleDetailById(callback: (Boolean) -> Unit) {
        firestore.collection("exercises").document(id).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val exerciseGeneral = it.result.toObject(ExerciseListItemModel::class.java)
                if (exerciseGeneral == null) {
                    callback(false)
                } else {
                    exerciseGeneral.id = it.result.id
                    _thumbnail.value = exerciseGeneral.image
                    _title.value = exerciseGeneral.name
                    _detail.value =
                        "${measureDuration / 60} Phút | ${measureCalories * measureDuration} Calo"
                    _schedule.value = scheduleDate

                    firestore.collection("exercise_details")
                        .whereEqualTo("refId", exerciseGeneral.id).get()
                        .addOnCompleteListener { subIt ->
                            if (subIt.isSuccessful) {
                                val details =
                                    subIt.result.toObjects(ExerciseDetailModel::class.java)
                                _description.value = details[0].description
                                _steps.value = details[0].steps
                                callback(true)
                            } else {
                                Log.i("Firebase", it.exception.toString())
                                callback(false)
                            }
                        }
                }
            } else {
                Log.i("Firebase", it.exception.toString())
                callback(false)
            }
        }
    }

    fun deleteSchedule(callback: (Boolean) -> Unit) {
        val uid = auth.currentUser!!.uid
        val (start, end) = CommonFunctions.getCurrentWeekRange()
        val weekCollection = "$start-$end".replace("/", ".")
        val curDate =
            LocalDate.parse(scheduleDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME).dayOfWeek.value
        val curDateKey = CommonFunctions.mapCalendarValueToDateKey(curDate + 1)

        // delete document
        val deleteRef = firestore.collection("user_health").document(uid).collection(weekCollection)
            .document(curDateKey).collection("schedule").document(id)
        deleteRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val data = it.result.data
                if (data == null) {
                    callback(false)
                } else {
                    val actualSet = if (data["actualSet"] != null) {
                        data["actualSet"] as Long
                    } else 0L // allow to delete only if actualSet = 0
                    if (actualSet != 0L) {
                        callback(false)
                    } else {
                        deleteRef.delete().addOnCompleteListener { itInner ->
                            if (itInner.isSuccessful) { // update parent document
                                firestore.collection("user_health").document(uid)
                                    .collection(weekCollection).document(curDateKey).get()
                                    .addOnCompleteListener { subtask ->
                                        if (subtask.isSuccessful) {
                                            if (subtask.result.data != null) {
                                                var totalWorkoutTime: Long =
                                                    subtask.result.data!!["totalWorkoutTime"] as Long
                                                var exerciseCount =
                                                    subtask.result.data!!["exerciseCount"] as Long
                                                var consumedCalories =
                                                    subtask.result.data!!["consumedCalories"] as Double

                                                --exerciseCount
                                                totalWorkoutTime -= measureDuration * totalSet
                                                consumedCalories -= measureCalories * measureDuration * totalSet

                                                firestore.collection("user_health").document(uid)
                                                    .collection(weekCollection).document(
                                                        curDateKey
                                                    ).set(
                                                        hashMapOf(
                                                            "exerciseCount" to exerciseCount,
                                                            "totalWorkoutTime" to totalWorkoutTime,
                                                            "consumedCalories" to consumedCalories,
                                                        ), SetOptions.merge()
                                                    ).addOnCompleteListener { subtaskInner ->
                                                        if (subtaskInner.isSuccessful) {
                                                            callback(true)
                                                        } else {
                                                            Log.i(
                                                                "Firebase",
                                                                subtaskInner.exception.toString()
                                                            )
                                                            callback(false)
                                                        }
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
            } else {
                Log.i("Firebase", it.exception.toString())
                callback(false)
            }
        }
    }

    fun getExerciseDetail(exerciseId: String, callback: (Boolean, ExerciseListItemModel) -> Unit) {
        firestore.collection("exercises").document(exerciseId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val exerciseGeneral = task.result.toObject(ExerciseListItemModel::class.java)
                if (exerciseGeneral == null) {
                    callback(false, ExerciseListItemModel())
                } else {
                    exerciseGeneral.id = task.result.id
                    callback(true, exerciseGeneral)
                }
            } else {
                Log.i("Firebase", task.exception.toString())
                callback(false, ExerciseListItemModel())
            }
        }
    }
}