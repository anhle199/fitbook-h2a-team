package com.h2a.fitbook.viewmodels.statistics

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.models.BasicStatisticInfo
import com.h2a.fitbook.models.FoodStatisticModel
import com.h2a.fitbook.models.ScheduleStatisticModel
import com.h2a.fitbook.utils.Constants
import com.h2a.fitbook.utils.UtilFunctions
import java.time.*
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class StatisticInDateViewModel: ViewModel() {

    // Fetching state observable variable
    private val _isFetching: MutableLiveData<Boolean> = MutableLiveData(false)
    val isFetching: LiveData<Boolean> = _isFetching

    // Values from UI
    var selectedDateIndex = LocalDate.now().dayOfWeek.value - 1
    var exerciseIntensity = 0f

    // Indicating statistic specifications
    val standardCalories get() = calculateStandardCalories()
    val consumedCalories get() = calculateConsumedCalories()
    val burnedCalories get() = calculateBurnedCalories()
    val exerciseCount get() = basicStatisticInfo.exerciseCount
    val totalWorkoutTime get() = calculateTotalWorkoutTimes() / 60f
    val caloriesBurnedData get() = mapCaloriesBurnedData()
    val workoutTimesData get() = mapWorkoutTimesData()

    // Values from Firebase
    private var gender = "male"
    private var dob = Timestamp.now()
    private var basicStatisticInfo = BasicStatisticInfo()
    private var foodList = ArrayList<FoodStatisticModel>()
    private var exerciseList = ArrayList<ScheduleStatisticModel>()

    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private val firestore = FirebaseFirestore.getInstance()

    fun setFetchingState(state: Boolean) {
        _isFetching.value = state
    }

    fun fetchData() {
        basicStatisticInfo = BasicStatisticInfo()
        foodList.clear()
        exerciseList.clear()

        fetchGenderAndDobFromFirebase()
        fetchRawStatisticData()
    }

    private fun fetchGenderAndDobFromFirebase() {
        firestore.collection(Constants.USERS_COLLECTION_NAME)
            .document(userId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val snapshot = it.result

                    // gender
                    val loadedGender = snapshot.get("gender", String::class.java)
                    if (!loadedGender.isNullOrEmpty()) {
                        this.gender = loadedGender
                    }

                    // date of birth
                    val loadedDob = snapshot.get("dateOfBirth", Timestamp::class.java)
                    if (loadedDob != null) {
                        this.dob = loadedDob
                    }

                    Log.i("StatisticByDate", "getGenderFromFirebase:success - get gender")
                } else {
                    Log.i("StatisticByDate", "getGenderFromFirebase:failed - get gender")
                }
            }
    }

    private fun fetchRawStatisticData() {
        val currentWeekInString = UtilFunctions.getWeekRangeInString(LocalDate.now())
        val currentDateInLowercaseString = DayOfWeek.of(selectedDateIndex + 1)
            .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            .lowercase()

        val documentRef = firestore.collection(Constants.USER_HEALTH_COLLECTION_NAME)
            .document(userId)
            .collection(currentWeekInString)
            .document(currentDateInLowercaseString)

        documentRef.get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val snapshot = it.result
                    val loadedObject = snapshot.toObject(BasicStatisticInfo::class.java)
                    if (loadedObject != null) {
                        this.basicStatisticInfo = loadedObject
                    }

                    Log.i("StatisticByDate", "fetchData:success - get basic statistic info")
                } else {
                    Log.i("StatisticByDate", "fetchData:failed - get basic statistic info")
                }
            }

        documentRef.collection(Constants.FOOD_LIST_IN_USER_HEALTH_COLLECTION_NAME)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val snapshot = it.result
                    foodList.addAll(
                        snapshot.documents.map { item -> item.toObject(FoodStatisticModel::class.java)!! }
                    )

                    Log.i("StatisticByDate", "fetchData:success - get foodList")
                } else {
                    Log.i("StatisticByDate", "fetchData:failed - get foodList")
                }
            }

        documentRef.collection(Constants.SCHEDULE_LIST_IN_USER_HEALTH_COLLECTION_NAME)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val snapshot = it.result
                    exerciseList.addAll(
                        snapshot.documents.map { item -> item.toObject(ScheduleStatisticModel::class.java)!! }
                    )

                    Log.i("StatisticByDate", "fetchData:success - get schedule")
                } else {
                    Log.i("StatisticByDate", "fetchData:failed - get schedule")
                }
            }
    }

    private fun calculateStandardCalories(): Float {
        if (basicStatisticInfo.height == 0f || basicStatisticInfo.weight == 0f)
            return 0f

        // calculate age
        val dobInLocalDate = dob.toDate()
            .toInstant()
            .atZone(ZoneOffset.ofHours(7))
            .toLocalDate()
        val age = Period.between(dobInLocalDate, LocalDate.now()).years

        val kIndex = if (gender == "male") 5 else -161
        val bmr = (10f * basicStatisticInfo.weight) + (6.25f * basicStatisticInfo.height) - (5f * age) + kIndex

        return bmr * exerciseIntensity
    }

    private fun calculateConsumedCalories(): Float {
        var totalCalories = 0f
        foodList.forEach { totalCalories += it.calories }

        return totalCalories
    }

    private fun calculateBurnedCalories(): Float {
        var totalCalories = 0f
        exerciseList.forEach { totalCalories += it.calories }

        return totalCalories
    }

    private fun calculateTotalWorkoutTimes(): Float {
        var total = 0f
        exerciseList.forEach { total += it.actualWorkoutTime }
        return total
    }

    private fun mapCaloriesBurnedData(): LinkedHashMap<String, Float> {
        val data = LinkedHashMap<String, Float>()
        exerciseList.forEach { data[it.name] = it.calories }
        return data
    }

    private fun mapWorkoutTimesData(): LinkedHashMap<String, Float> {
        val data = LinkedHashMap<String, Float>()
        exerciseList.forEach { data[it.name] = it.actualWorkoutTime / 60f }
        return data
    }

}
