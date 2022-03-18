package com.alandvgarcia.tmdbapp.network

import io.ktor.http.*

abstract class ApiSettings {
    companion object {
        private const val apiVersion = "3"
        private const val url =
            "https://api.themoviedb.org"


        private var token: String? = null

        fun getToken(): String
                = token ?: kotlin.run { throw Exception("Not have a token configured") }

        fun setToken(token: String){
            this.token = token
        }
    }

    val urlBuilder = URLBuilder(url).apply {
        path(apiVersion)
        parameters.append("api_key", getToken())
    }
}