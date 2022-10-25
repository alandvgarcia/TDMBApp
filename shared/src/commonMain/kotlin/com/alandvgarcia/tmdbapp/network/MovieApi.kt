package com.alandvgarcia.tmdbapp.network

import com.alandvgarcia.tmdbapp.network.model.MovieResponse
import com.alandvgarcia.tmdbapp.network.model.PagingResponse
import io.ktor.http.*

class MovieApi(private val clientService: ClientServiceImplementation = ClientService()) :
    ApiSettings() {

    private val urlBuilderMovies = urlBuilder.apply {
        appendPathSegments("movie")
    }

    suspend fun getPopularMovies(page: Int): ClientServiceResult<PagingResponse<MovieResponse>> {
        val url = URLBuilder(urlBuilderMovies).apply {
            appendPathSegments("popular")
            parameters.append("page", page.toString())
        }.buildString()
        return clientService.safeResponse<Nothing, PagingResponse<MovieResponse>>(url)
    }

    suspend fun getTopRatedMovies(page: Int): ClientServiceResult<PagingResponse<MovieResponse>> {
        val url = URLBuilder(urlBuilderMovies).apply {
            appendPathSegments("top_rated")
            parameters.append("page", page.toString())
        }.buildString()
        return clientService.safeResponse<Nothing, PagingResponse<MovieResponse>>(url)
    }

    suspend fun getLatestMovies(page: Int): ClientServiceResult<PagingResponse<MovieResponse>> {
        val url = URLBuilder(urlBuilderMovies).apply {
            appendPathSegments("latest")
            parameters.append("page", page.toString())
        }.buildString()
        return clientService.safeResponse<Nothing, PagingResponse<MovieResponse>>(url)
    }


}