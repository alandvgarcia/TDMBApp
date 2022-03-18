package com.alandvgarcia.tmdbapp.database

import com.alandvgarcia.tmdbapp.db.TMDBAppDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver


actual fun createDriver(): SqlDriver {
    return NativeSqliteDriver(TMDBAppDatabase.Schema, "tmdb.db")
}