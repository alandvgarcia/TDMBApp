package com.alandvgarcia.tmdbapp.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alandvgarcia.tmdbapp.network.MovieApi
import com.alandvgarcia.tmdbapp.repository.MovieApiTypeEnum
import com.alandvgarcia.tmdbapp.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoviesViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val movieRepository = MovieRepository()

    val popularMoviesFlow = movieRepository.moviesPopular

    fun getMovies(page: Int) = viewModelScope.launch(Dispatchers.IO) {

        if(_isLoading.value) return@launch

        _isLoading.value = true
        val result = movieRepository.getMovies(page, MovieApiTypeEnum.POPULAR)
        _isLoading.value = false
        Log.d("Result", "Result -> $result")
    }
}


