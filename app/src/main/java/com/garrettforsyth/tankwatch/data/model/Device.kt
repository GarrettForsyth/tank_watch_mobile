package com.garrettforsyth.tankwatch.data.model

data class Device(
    val id: Int,
    val tankId: Int,
    val name: String,
    val macAddress: String
)