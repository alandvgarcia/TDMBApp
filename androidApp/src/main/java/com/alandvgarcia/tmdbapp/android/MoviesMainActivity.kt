package com.alandvgarcia.tmdbapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alandvgarcia.tmdbapp.android.ui.theme.TMDB_AppTheme
import com.alandvgarcia.tmdbapp.database.appContext
import com.alandvgarcia.tmdbapp.db.Movie
import com.alandvgarcia.tmdbapp.network.ApiSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MoviesMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        ApiSettings.setToken("8aa61303fe43973122e7b287a5c13c42")
        appContext = applicationContext

        super.onCreate(savedInstanceState)
        setContent {
            TMDB_AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainView()
                }
            }
        }
    }
}

@Composable
fun MainView(viewModel: MoviesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    var page by remember {
        mutableStateOf(1)
    }

    PopularMoviesListView(movies = viewModel.popularMoviesFlow) {
        page++
    }.apply {
        viewModel.getMovies(page)
    }
}

@Composable
fun PopularMoviesListView(movies: Flow<List<Movie>>, onPageEnd: () -> Unit) {

    val moviesState by movies.collectAsState(initial = listOf())

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(moviesState) { movie ->
            Row {
                AsyncImage(
                    ImageRequest.Builder(LocalContext.current)
                        .data("https://image.tmdb.org/t/p/w400${movie.posterPath}")
                        .crossfade(true)
                        .build(), contentDescription = null
                )
                Column {
                    Text(movie.title ?: "")
                    Text(movie.overview ?: "")
                }
            }.apply {
                if (movie.id == moviesState.last().id)
                    onPageEnd()
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun MoviesViewContent() {
    TMDB_AppTheme {
        PopularMoviesListView(movies = flow { }) {

        }
    }
}