package com.garrettforsyth.tankwatch.data.remote

import kotlinx.serialization.Serializable

@Serializable
class FcmTokenDto(
    val token: String
) {
}