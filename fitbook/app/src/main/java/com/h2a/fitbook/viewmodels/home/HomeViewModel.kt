package com.h2a.fitbook.viewmodels.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.models.ExerciseListItemModel

class HomeViewModel : ViewModel() {
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getExerciseList(callback: (Boolean, ArrayList<ExerciseListItemModel>) -> Unit) {
        firestore.collection("exercises").get().addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                val list: ArrayList<ExerciseListItemModel> = arrayListOf()
                for (document in task.result) {
                    val item = document.toObject(ExerciseListItemModel::class.java)
                    item.id = document.id
                    list.add(item)
                }
                callback(task.isSuccessful, list)
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