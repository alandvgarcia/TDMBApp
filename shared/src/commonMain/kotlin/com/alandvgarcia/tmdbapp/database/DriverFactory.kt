package com.alandvgarcia.tmdbapp.database

import com.alandvgarcia.tmdbapp.db.TMDBAppDatabase
import com.squareup.sqldelight.db.SqlDriver

expect fun createDriver(): SqlDriver


class DriverFactory {
    fun createDatabase(): TMDBAppDatabase {
        return TMDBAppDatabase(createDriver())
    }
}