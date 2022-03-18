package com.alandvgarcia.tmdbapp.network

import com.alandvgarcia.tmdbapp.network.model.MovieResponse
import com.alandvgarcia.tmdbapp.network.model.PagingResponse
import io.ktor.http.*

class MovieApi : ApiSettings() {

    private val urlBuilderMovies = urlBuilder.apply {
        pathComponents("movie")
    }

    suspend fun getPopularMovies(page: Int): ClientServiceResult<PagingResponse<MovieResponse>> {
        val url = URLBuilder(urlBuilderMovies).apply {
            pathComponents("popular")
            parameters.append("page", page.toString())
        }.buildString()
        return ClientService().safeResponse<Nothing, PagingResponse<MovieResponse>>(url)
    }
}