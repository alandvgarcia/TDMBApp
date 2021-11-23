package com.alandvgarcia.tmdbapp.network

sealed class ClientServiceResult<out T> {
    data class Success<T>(val result: T) : ClientServiceResult<T>()
    data class Error(val error: String) : ClientServiceResult<Nothing>()
}