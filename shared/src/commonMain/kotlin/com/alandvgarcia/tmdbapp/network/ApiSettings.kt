package com.alandvgarcia.tmdbapp.network

import com.alandvgarcia.tmdbapp.BuildKonfig
import io.ktor.http.*

abstract class ApiSettings {
    companion object {
        private const val apiVersion = "3"
        private const val url =
            "https://api.themoviedb.org"

        fun getToken(): String
                = BuildKonfig.movieApiKey
    }

    val urlBuilder = URLBuilder(url).apply {
        path(apiVersion)
        parameters.append("api_key", getToken())
    }
}