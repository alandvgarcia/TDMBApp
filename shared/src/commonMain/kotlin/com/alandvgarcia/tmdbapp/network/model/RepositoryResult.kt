package com.alandvgarcia.tmdbapp.network.model

sealed class RepositoryResult<out T> : ResultManager<T>() {
    data class Success<T>(override val data: T?) : RepositoryResult<T>()
    data class Error(override val error: String) : RepositoryResult<Nothing>()
}

abstract class ResultManager<out V> {
    open val data: V? = null
    open val error: String = ""

    val isSuccess: Boolean get() = !isFailure
    private val isFailure: Boolean get() = error.isNotEmpty()
}