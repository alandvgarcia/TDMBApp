package com.alandvgarcia.tmdbapp.database

import android.content.Context
import com.alandvgarcia.tmdbapp.db.TMDBAppDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(TMDBAppDatabase.Schema, context, "tmdbAppDatabase.db")
    }
}