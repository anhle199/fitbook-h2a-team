package com.h2a.fitbook.viewmodels.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExerciseDetailViewModel : ViewModel() {
    private val _thumbnail: MutableLiveData<String> = MutableLiveData()
    private val _title: MutableLiveData<String> = MutableLiveData()
    private val _detail: MutableLiveData<String> = MutableLiveData()
    private val _description: MutableLiveData<String> = MutableLiveData()
    private val _steps: MutableLiveData<ArrayList<String>> = MutableLiveData(arrayListOf())

    val thumbnail: LiveData<String> = _thumbnail
    val title: LiveData<String> = _title
    val detail: LiveData<String> = _detail
    val description: LiveData<String> = _description
    val steps: LiveData<ArrayList<String>> = _steps

    fun setThumbnail(thumbnail: String) {
        _thumbnail.value = thumbnail
    }

    fun setTitle(title: String) {
        _title.value = title
    }
    fun setDetail(detail: String) {
        _detail.value = detail
    }
    fun setDescription(desc: String) {
        _description.value = desc
    }
    fun setSteps(steps: ArrayList<String>) {
        _steps.value?.clear()
        _steps.value?.addAll(steps)
    }
}