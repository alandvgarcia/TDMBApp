package com.alandvgarcia.tmdbapp.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.alandvgarcia.tmdbapp.Greeting
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.alandvgarcia.tmdbapp.network.ApiSettings
import com.alandvgarcia.tmdbapp.network.MovieApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch(Dispatchers.IO) {
           val result =  MovieApi().getPopularMovies(1)
            Log.d("Result","Result -> $result")
        }

    }



}
