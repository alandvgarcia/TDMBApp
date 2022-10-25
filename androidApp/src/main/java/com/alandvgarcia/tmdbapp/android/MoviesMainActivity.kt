package com.alandvgarcia.tmdbapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alandvgarcia.tmdbapp.android.ui.movies.MovieListView
import com.alandvgarcia.tmdbapp.android.ui.movies.MoviesView
import com.alandvgarcia.tmdbapp.android.ui.theme.TMDB_AppTheme
import com.alandvgarcia.tmdbapp.database.appContext
import com.alandvgarcia.tmdbapp.network.ApiSettings
import com.alandvgarcia.tmdbapp.network.enum.MovieApiTypeEnum

class MoviesMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        appContext = applicationContext

        super.onCreate(savedInstanceState)
        setContent {
            TMDB_AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold() {
                        MoviesView(movieApiTypeEnum = MovieApiTypeEnum.POPULAR)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MoviesViewContent() {
    TMDB_AppTheme {
        MovieListView(
            "",
            movies = listOf(),
            false,
            false,
            onRefresh = {},
            onPageEnd = {},
            onChangeCategory = {},
        )
    }
}