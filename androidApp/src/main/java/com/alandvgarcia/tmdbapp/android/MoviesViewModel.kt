package com.alandvgarcia.tmdbapp.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alandvgarcia.tmdbapp.db.Movie
import com.alandvgarcia.tmdbapp.network.enum.MovieApiTypeEnum
import com.alandvgarcia.tmdbapp.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MoviesViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val movieRepository = MovieRepository()

    fun moviesFlow(movieApiTypeEnum: MovieApiTypeEnum): Flow<List<Movie>> {
        return when (movieApiTypeEnum) {
            MovieApiTypeEnum.POPULAR -> movieRepository.moviesPopular.filterNotNull()
            MovieApiTypeEnum.TOP_RATED -> movieRepository.moviesTopRated.filterNotNull()
            MovieApiTypeEnum.LATEST -> movieRepository.moviesLatest.filterNotNull()
        }
    }

    fun getMovies(movieApiTypeEnum: MovieApiTypeEnum, refreshData: Boolean = false) =
        viewModelScope.launch(Dispatchers.IO) {
            if (_isLoading.value) return@launch
            _isLoading.value = true
            delay(1000)
            movieRepository.getMovies(movieApiTypeEnum, refreshData)
            _isLoading.value = false
        }
}


