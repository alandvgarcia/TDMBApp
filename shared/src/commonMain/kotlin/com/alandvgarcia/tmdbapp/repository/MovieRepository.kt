package com.alandvgarcia.tmdbapp.repository

import com.alandvgarcia.tmdbapp.database.DriverFactory
import com.alandvgarcia.tmdbapp.database.extensions.parseToDbEntity
import com.alandvgarcia.tmdbapp.db.Movie
import com.alandvgarcia.tmdbapp.db.MoviePopularPagingKey
import com.alandvgarcia.tmdbapp.network.ClientServiceResult
import com.alandvgarcia.tmdbapp.network.MovieApi
import com.alandvgarcia.tmdbapp.network.enum.MovieApiTypeEnum
import com.alandvgarcia.tmdbapp.network.model.RepositoryResult
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository() {

    private val movieApi = MovieApi()
    private val database = DriverFactory().createDatabase()

    val moviesPopular = database.movieQueries.selectPopularMovies().asFlow().mapToList()
    val moviesTopRated = database.movieQueries.selectTopRated().asFlow().mapToList()
    val moviesLatest = database.movieQueries.selectLatest().asFlow().mapToList()

    suspend fun getMovies(
        movieApiTypeEnum: MovieApiTypeEnum,
        refreshData: Boolean = false
    ): RepositoryResult<Boolean>{


        if(refreshData){
            clearAll()
        }

        val page = when (movieApiTypeEnum) {
            MovieApiTypeEnum.POPULAR -> database.movieQueries.selectLastMoviePopularPagingKey().executeAsOneOrNull() ?: 1
            MovieApiTypeEnum.TOP_RATED -> database.movieQueries.selectLastMovieTopRatedPagingKey().executeAsOneOrNull() ?: 1
            MovieApiTypeEnum.LATEST -> database.movieQueries.selectLastMovieLatestPagingKey().executeAsOneOrNull() ?: 1
        }.toInt()


        val result = when (movieApiTypeEnum) {
            MovieApiTypeEnum.POPULAR -> movieApi.getPopularMovies(page)
            MovieApiTypeEnum.TOP_RATED -> movieApi.getTopRatedMovies(page)
            MovieApiTypeEnum.LATEST -> movieApi.getLatestMovies(page)
        }

        return when (result) {
            is ClientServiceResult.Success -> {
                syncMoviesDatabase(result.result.results.map { it.parseToDbEntity() }, movieApiTypeEnum)
                RepositoryResult.Success(true)
            }
            is ClientServiceResult.Error -> {
                RepositoryResult.Error(result.error)
            }
        }
    }

    private fun clearAll(){
        database.movieQueries.deleteAll()
        database.movieQueries.deleteAllMovieLatestPagingKey()
        database.movieQueries.deleteAllMoviePopularPagingKey()
        database.movieQueries.deleteAllMovieTopRatedPagingKey()
    }

    private fun syncMoviesDatabase(movies: List<Movie>, movieApiTypeEnum: MovieApiTypeEnum) {
        database.transaction {

            movies.forEach {
                database.movieQueries.insert(it)
            }
            when(movieApiTypeEnum){
                MovieApiTypeEnum.POPULAR -> {
                    val lastPage = database.movieQueries.selectLastMoviePopularPagingKey().executeAsOneOrNull() ?: 0
                    database.movieQueries.insertMoviePopularPagingKey(
                        lastPage+1
                    )
                }
                MovieApiTypeEnum.TOP_RATED -> {
                    val lastPage = database.movieQueries.selectLastMovieTopRatedPagingKey().executeAsOneOrNull() ?: 0
                    database.movieQueries.insertMovieTopRatedPagingKey(
                        lastPage+1
                    )
                }
                MovieApiTypeEnum.LATEST -> {
                    val lastPage = database.movieQueries.selectLastMovieLatestPagingKey().executeAsOneOrNull() ?: 0
                    database.movieQueries.insertMovieLatestPagingKey(
                        lastPage+1
                    )
                }

            }
        }
    }
}