package com.alandvgarcia.tmdbapp.database

import com.alandvgarcia.tmdbapp.db.TMDBAppDatabase
import com.squareup.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): TMDBAppDatabase {
    val driver = driverFactory.createDriver()
    return TMDBAppDatabase(driver)
}