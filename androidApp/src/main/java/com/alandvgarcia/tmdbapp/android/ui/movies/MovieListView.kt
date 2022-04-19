package com.alandvgarcia.tmdbapp.android.ui.movies

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alandvgarcia.tmdbapp.android.ui.component.CollapsingMovieToolbar
import com.alandvgarcia.tmdbapp.android.ui.component.RatingBar
import com.alandvgarcia.tmdbapp.db.Movie
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState



private const val urlImage = "https://image.tmdb.org/t/p/w400"

@Composable
fun MovieListView(
    moviesCategoryTitle: String,
    movies: List<Movie>,
    isLoading: Boolean,
    refreshData: Boolean,
    onRefresh: () -> Unit,
    onPageEnd: () -> Unit,
    onChangeCategory: () -> Unit,
) {

    val scrollState = rememberLazyListState()

    Scaffold {
        SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing = refreshData), onRefresh = {
            if (!refreshData)
                onRefresh()
        }) {
            Column {

                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    item {
                        CollapsingMovieToolbar(
                            "Movies $moviesCategoryTitle",
                            category = moviesCategoryTitle,
                            scrollState
                        ) {
                            onChangeCategory()
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    items(movies) { movie ->
                        Card(
                            Modifier
                                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
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
                                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                                        RatingBar(
                                            rating = movie.voteAverage?.toFloat()?.div(1.90f)
                                                ?: 0.0F,
                                            modifier = Modifier.height(18.dp)
                                        )
                                    }
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
    }
}

