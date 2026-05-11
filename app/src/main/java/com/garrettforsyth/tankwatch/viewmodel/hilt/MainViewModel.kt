package com.garrettforsyth.tankwatch.viewmodel.hilt

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel()  {
    val myValue = 5;
}