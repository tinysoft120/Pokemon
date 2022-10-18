package com.tinysoft.pokemon.network

sealed class ResultWrapper<out T> {
    object Loading: ResultWrapper<Nothing>()
    data class Success<out T>(val value: T): ResultWrapper<T>()
    data class GenericError(val code: Int, val message: String? = null): ResultWrapper<Nothing>()
    object NetworkError: ResultWrapper<Nothing>()
}