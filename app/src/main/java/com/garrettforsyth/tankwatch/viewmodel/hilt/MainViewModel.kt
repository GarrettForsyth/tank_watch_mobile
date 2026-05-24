package com.garrettforsyth.tankwatch.viewmodel.hilt

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel()  {
    private val _pendingSensorId = MutableStateFlow<Int?>(null)
    val pendingSensorId: StateFlow<Int?> = _pendingSensorId.asStateFlow()

    fun onNotificationTapped(sensorId: Int) {
        _pendingSensorId.value = sensorId
    }

    fun onNavigatedToSensor() {
        _pendingSensorId.value = null
    }
}