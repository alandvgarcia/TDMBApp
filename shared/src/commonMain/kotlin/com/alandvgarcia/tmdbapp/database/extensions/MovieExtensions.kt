package com.alandvgarcia.tmdbapp.database.extensions

import com.alandvgarcia.tmdbapp.db.Movie
import com.alandvgarcia.tmdbapp.network.model.MovieResponse

fun MovieResponse.parseToDbEntity(): Movie {
    return Movie(
        id = id,
        isAdult = adult,
        backdropPath = backdropPath,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        voteAverage = voteAverage,
        voteCount = voteCount?.toLong()
    )
}