package com.garrettforsyth.tankwatch.data.remote

import com.garrettforsyth.tankwatch.data.model.Device
import kotlinx.serialization.Serializable

@Serializable
data class DeviceDto(
    val id: Int,
    val tankId: Int,
    val name: String,
    val macAddress: String
)

fun DeviceDto.toModel() = Device(
    id = id,
    tankId = tankId,
    name = name,
    macAddress = macAddress
)