package com.alandvgarcia.tmdbapp.repository

import com.alandvgarcia.tmdbapp.database.DriverFactory
import com.alandvgarcia.tmdbapp.database.extensions.parseToDbEntity
import com.alandvgarcia.tmdbapp.db.Movie
import com.alandvgarcia.tmdbapp.network.ClientServiceResult
import com.alandvgarcia.tmdbapp.network.MovieApi
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList

class MovieRepository() {

    private val movieApi = MovieApi()
    private val database = DriverFactory().createDatabase()

    val moviesPopular = database.movieQueries.selectPopularMovies().asFlow().mapToList()
    val moviesTopRated = database.movieQueries.selectTopRated().asFlow().mapToList()

    suspend fun getMovies(
        page: Int,
        movieApiTypeEnum: MovieApiTypeEnum
    ): RepositoryResult<Boolean> {
        val result = when (movieApiTypeEnum) {
            MovieApiTypeEnum.POPULAR -> {
                movieApi.getPopularMovies(page)
            }
            MovieApiTypeEnum.TOP_RATED -> {
                //TODO TOP_RATED
                movieApi.getPopularMovies(page)
            }
            MovieApiTypeEnum.RELEASE -> {
                //TODO RELEASE
                movieApi.getPopularMovies(page)
            }
        }

        return when (result) {
            is ClientServiceResult.Success -> {
                syncMoviesDatabase(result.result.results.map { it.parseToDbEntity() })
                RepositoryResult.Success(true)
            }
            is ClientServiceResult.Error -> {
                RepositoryResult.Error(result.error)
            }
        }
    }


    private fun syncMoviesDatabase(movies: List<Movie>, refreshData: Boolean = false) {

        if (refreshData) database.movieQueries.deleteAll()

        database.transaction {
            movies.forEach {
                database.movieQueries.insert(it)
            }
        }
    }


}

enum class MovieApiTypeEnum {
    POPULAR, TOP_RATED, RELEASE
}


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