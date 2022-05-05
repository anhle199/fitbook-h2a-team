package com.h2a.fitbook.viewmodels.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.models.ExerciseDetailModel
import com.h2a.fitbook.models.ExerciseListItemModel

class ExerciseDetailViewModel : ViewModel() {
    private val _thumbnail: MutableLiveData<String> = MutableLiveData()
    private val _title: MutableLiveData<String> = MutableLiveData()
    private val _detail: MutableLiveData<String> = MutableLiveData()
    private val _description: MutableLiveData<String> = MutableLiveData()
    private val _steps: MutableLiveData<ArrayList<String>> = MutableLiveData(arrayListOf())
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    val thumbnail: LiveData<String> = _thumbnail
    val title: LiveData<String> = _title
    val detail: LiveData<String> = _detail
    val description: LiveData<String> = _description
    val steps: LiveData<ArrayList<String>> = _steps
    val isLoading: LiveData<Boolean> = _isLoading

    fun setLoading(state: Boolean) {
        _isLoading.value = state
    }

    fun getExerciseDetail(exerciseId: String, callback: (Boolean, ExerciseListItemModel) -> Unit) {
        firestore.collection("exercises").document(exerciseId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val exerciseGeneral = task.result.toObject(ExerciseListItemModel::class.java)
                if (exerciseGeneral == null) {
                    callback(false, ExerciseListItemModel())
                } else {
                    exerciseGeneral.id = task.result.id
                    _thumbnail.value = exerciseGeneral.image
                    _title.value = exerciseGeneral.name
                    _detail.value = "${exerciseGeneral.measureDuration / 60} Phút | ${exerciseGeneral.measureCalories} Calo"
                    firestore.collection("exercise_details").whereEqualTo("refId", exerciseGeneral.id).get()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val details = it.result.toObjects(ExerciseDetailModel::class.java)
                                _description.value = details[0].description
                                _steps.value = details[0].steps
                                callback(true, exerciseGeneral)
                            } else {
                                Log.i("Firebase", it.exception.toString())
                                callback(false, ExerciseListItemModel())
                            }
                        }
                }
            } else {
                Log.i("Firebase", task.exception.toString())
                callback(false, ExerciseListItemModel())
            }
        }
    }
}

