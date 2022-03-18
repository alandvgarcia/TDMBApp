package com.alandvgarcia.tmdbapp.database

import android.content.Context
import com.alandvgarcia.tmdbapp.db.TMDBAppDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver


lateinit var appContext: Context

actual fun createDriver(): SqlDriver {
    return AndroidSqliteDriver(TMDBAppDatabase.Schema, appContext, "tmdb.db")
}