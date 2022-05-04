package com.h2a.fitbook.viewmodels.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StatisticsViewModel: ViewModel() {

    private val _isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    fun setRefreshingState(state: Boolean) {
        _isRefreshing.value = state
    }

}
