package com.garrettforsyth.tankwatch.util


sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}

fun<T> Resource<T>.dataOrNull(): T? = when (this) {
    is Resource.Success -> this.data
    else -> null
}

fun<T> Resource<T>.errorOrNull(): String? = when (this) {
    is Resource.Error -> this.message
    else -> null
}