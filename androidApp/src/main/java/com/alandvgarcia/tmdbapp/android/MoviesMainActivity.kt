package com.alandvgarcia.tmdbapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alandvgarcia.tmdbapp.android.ui.theme.TMDB_AppTheme
import com.alandvgarcia.tmdbapp.database.appContext
import com.alandvgarcia.tmdbapp.db.Movie
import com.alandvgarcia.tmdbapp.network.ApiSettings
import com.alandvgarcia.tmdbapp.network.enum.MovieApiTypeEnum
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import okhttp3.internal.filterList

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
                    MoviesView(movieApiTypeEnum = MovieApiTypeEnum.POPULAR)
                }
            }
        }
    }
}

@Composable
fun MoviesView(
    viewModel: MoviesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    movieApiTypeEnum: MovieApiTypeEnum
) {


    var movies by remember {
        mutableStateOf(listOf<Movie>())
    }
    var refreshData by remember {
        mutableStateOf(false)
    }

    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(true) {
        viewModel.moviesFlow(movieApiTypeEnum = movieApiTypeEnum).collectLatest { newMovieList ->
            if (newMovieList.isEmpty()) {
                viewModel.getMovies(movieApiTypeEnum)
            }
            movies = newMovieList
            delay(1500)
            refreshData = false
        }
    }

    MovieListView(
        movies = movies,
        isLoading, refreshData, onRefresh = {
            viewModel.getMovies(movieApiTypeEnum, true)
            refreshData = true
        }, onPageEnd = {
            viewModel.getMovies(movieApiTypeEnum, refreshData)
        })
}

const val urlImage = "https://image.tmdb.org/t/p/w400"

@Composable
fun MovieListView(
    movies: List<Movie>,
    isLoading: Boolean,
    refreshData: Boolean,
    onRefresh: () -> Unit,
    onPageEnd: () -> Unit
) {
    SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing = refreshData), onRefresh = {
        if (!refreshData)
            onRefresh()
    }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(movies) { movie ->
                Card(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    Row {
                        AsyncImage(
                            ImageRequest.Builder(LocalContext.current)
                                .data("$urlImage${movie.posterPath}")
                                .crossfade(true)
                                .build(), contentDescription = null
                        )
                        Column(Modifier.padding(8.dp)) {
                            Text(
                                movie.title ?: "",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                movie.overview ?: "",
                                maxLines = 4,
                                fontWeight = FontWeight.Light,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }.apply {
                    if (movie.id == movies.last().id && !refreshData && !isLoading)
                        onPageEnd()
                }
            }
            if (isLoading)
                item {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun MoviesViewContent() {
    TMDB_AppTheme {
        MovieListView(movies = listOf(), false, false, onRefresh = {}) {

        }
    }
}