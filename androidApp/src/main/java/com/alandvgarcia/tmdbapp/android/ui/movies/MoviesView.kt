package com.alandvgarcia.tmdbapp.android.ui.movies

import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.alandvgarcia.tmdbapp.android.MoviesViewModel
import com.alandvgarcia.tmdbapp.android.R
import com.alandvgarcia.tmdbapp.db.Movie
import com.alandvgarcia.tmdbapp.network.enum.MovieApiTypeEnum
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MoviesView(
    viewModel: MoviesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    movieApiTypeEnum: MovieApiTypeEnum
) {

    val moviesTypeEnum =
        listOf(MovieApiTypeEnum.POPULAR, MovieApiTypeEnum.TOP_RATED, MovieApiTypeEnum.LATEST)

    var movies by remember {
        mutableStateOf(listOf<Movie>())
    }

    var refreshData by remember {
        mutableStateOf(false)
    }

    var currentMovieApiType by remember {
        mutableStateOf(movieApiTypeEnum)
    }

    val coroutineScope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsState()

    MovieListView(
        stringResource(
            id =
            when (currentMovieApiType) {
                MovieApiTypeEnum.POPULAR -> R.string.popular
                MovieApiTypeEnum.LATEST -> R.string.lasted
                MovieApiTypeEnum.TOP_RATED -> R.string.topRated
                else -> {
                    R.string.popular
                }
            }
        ),
        movies = movies,
        isLoading, refreshData, onRefresh = {
            refreshData = true
            viewModel.getMovies(movieApiTypeEnum, refreshData)
        }, onPageEnd = {
            viewModel.getMovies(movieApiTypeEnum)
        }, onChangeCategory = {
            val index = moviesTypeEnum.indexOf(currentMovieApiType)
            currentMovieApiType =
                moviesTypeEnum[if (index < moviesTypeEnum.size - 1) index + 1 else 0]
            refreshData = true
        }).apply {
        coroutineScope.launch {
            viewModel.moviesFlow(movieApiTypeEnum = currentMovieApiType)
                .collectLatest { newMovieList ->
                    if (newMovieList.isEmpty()) {
                        viewModel.getMovies(currentMovieApiType)
                    }
                    movies = newMovieList
                    delay(1500)
                    refreshData = false
                }
        }
    }
}