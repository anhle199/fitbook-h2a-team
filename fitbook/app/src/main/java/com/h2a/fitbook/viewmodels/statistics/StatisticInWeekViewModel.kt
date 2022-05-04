package com.h2a.fitbook.viewmodels.statistics

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
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

class StatisticInWeekViewModel: ViewModel() {

    // Fetching state observable variable
    private val _isFetching: MutableLiveData<Boolean> = MutableLiveData(false)
    val isFetching: LiveData<Boolean> = _isFetching

    // Value from UI
    var exerciseIntensity = 0f

    // Indicating statistic specifications
    val standardCalories get() = calculateStandardCalories()
    val consumedCalories get() = calculateConsumedCalories()
    val burnedCalories get() = calculateBurnedCalories()
    val exerciseCount get() = countNumberOfExercises()
    val totalWorkoutTime get() = calculateTotalWorkoutTimes() / 60f
    val caloriesBurnedData get() = calculateCaloriesBurnedData()
    val workoutTimesData get() = calculateWorkoutTimesData()

    // Values from Firebase
    private var gender = "male"
    private var dob = Timestamp.now()
    private var basicStatisticInfoList = ArrayList<BasicStatisticInfo>()
    private var foodList = List(7) { ArrayList<FoodStatisticModel>() } as ArrayList
    private var exerciseList = List(7) { ArrayList<ScheduleStatisticModel>() }  as ArrayList

    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private val firestore = FirebaseFirestore.getInstance()

    fun setFetchingState(state: Boolean) {
        _isFetching.value = state
    }

    fun fetchData() {
        basicStatisticInfoList.clear()
        foodList.forEach { it.clear() }
        exerciseList.forEach { it.clear() }

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

                    Log.i("StatisticByWeek", "getGenderFromFirebase:success - get gender")
                } else {
                    Log.i("StatisticByWeek", "getGenderFromFirebase:failed - get gender")
                }
            }
    }

    private fun fetchRawStatisticData() {
        val currentWeekInString = UtilFunctions.getWeekRangeInString(LocalDate.now())
        val collectionRef = firestore.collection(Constants.USER_HEALTH_COLLECTION_NAME)
            .document(userId)
            .collection(currentWeekInString)

        val numberOfDays = LocalDate.now().dayOfWeek.value
        for (i in 0 until numberOfDays) {
            val dateInLowercaseString = DayOfWeek.of(i + 1)
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                .lowercase()

            fetchRawStatisticDataByDocumentPath(collectionRef, dateInLowercaseString, i)
        }
    }

    private fun fetchRawStatisticDataByDocumentPath(collectionRef: CollectionReference, documentPath: String, index: Int) {
        val documentRef = collectionRef.document(documentPath)

        documentRef.get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val snapshot = it.result
                    val loadedObject = snapshot.toObject(BasicStatisticInfo::class.java)
                    if (loadedObject != null) {
                        this.basicStatisticInfoList.add(loadedObject)
                    }

                    Log.i("StatisticByWeek", "fetchData:success - $documentPath - get basic statistic info")
                } else {
                    Log.i("StatisticByWeek", "fetchData:failed - $documentPath - get basic statistic info")
                }
            }

        documentRef.collection(Constants.FOOD_LIST_IN_USER_HEALTH_COLLECTION_NAME)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val snapshot = it.result
                    foodList[index] = ArrayList(snapshot.documents.map { item ->
                        item.toObject(FoodStatisticModel::class.java)!!
                    })

                    Log.i("StatisticByWeek", "fetchData:success - $documentPath - get foodList")
                } else {
                    Log.i("StatisticByWeek", "fetchData:failed - $documentPath - get foodList")
                }
            }

        documentRef.collection(Constants.SCHEDULE_LIST_IN_USER_HEALTH_COLLECTION_NAME)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val snapshot = it.result
                    exerciseList[index] = ArrayList(snapshot.documents.map { item ->
                        item.toObject(ScheduleStatisticModel::class.java)!!
                    })

                    Log.i("StatisticByWeek", "fetchData:success - $documentPath - get schedule")
                } else {
                    Log.i("StatisticByWeek", "fetchData:failed - $documentPath - get schedule")
                }
            }
    }

    private fun calculateStandardCalories(): Float {
        var total = 0f
        
        basicStatisticInfoList.forEach {
            if (it.height == 0f || it.weight == 0f)
                return 0f

            // calculate age
            val dobInLocalDate = dob.toDate()
                .toInstant()
                .atZone(ZoneOffset.ofHours(7))
                .toLocalDate()
            val age = Period.between(dobInLocalDate, LocalDate.now()).years

            val kIndex = if (gender == "male") 5 else -161
            val bmr = (10f * it.weight) + (6.25f * it.height) - (5f * age) + kIndex

            total += bmr * exerciseIntensity
        }
        
        return total
    }

    private fun calculateConsumedCalories(): Float {
        var totalCalories = 0f
        foodList.forEach { foodListInDate ->
            foodListInDate.forEach {
                totalCalories += it.calories
            }
        }

        return totalCalories
    }

    private fun calculateBurnedCalories(): Float {
        var totalCalories = 0f
        exerciseList.forEach { exerciseListInDate ->
            exerciseListInDate.forEach {
                totalCalories += it.calories
            }
        }
        return totalCalories
    }

    private fun countNumberOfExercises(): Int {
        var count = 0
        basicStatisticInfoList.forEach { count += it.exerciseCount }
        return count
    }
    
    private fun calculateTotalWorkoutTimes(): Float {
        var total = 0f

        exerciseList.forEach { exerciseListInDate ->
            exerciseListInDate.forEach {
                total += it.actualWorkoutTime
            }
        }

        return total
    }

    private fun calculateCaloriesBurnedData(): ArrayList<Float> {
        val data = ArrayList<Float>()

        exerciseList.forEach { exerciseListInDate ->
            var totalCalories = 0f
            exerciseListInDate.forEach {
                totalCalories += it.calories
            }

            data.add(totalCalories)
        }

        return data
    }

    private fun calculateWorkoutTimesData(): ArrayList<Float> {
        val data = ArrayList<Float>()

        exerciseList.forEach { exerciseListInDate ->
            var totalWorkoutTimes = 0f
            exerciseListInDate.forEach {
                totalWorkoutTimes += it.actualWorkoutTime / 60f
            }

            data.add(totalWorkoutTimes)
        }

        return data
    }

}
