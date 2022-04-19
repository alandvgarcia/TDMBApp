package com.alandvgarcia.tmdbapp.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alandvgarcia.tmdbapp.android.ui.component.RatingBar
import com.alandvgarcia.tmdbapp.android.ui.theme.TMDB_AppTheme
import com.alandvgarcia.tmdbapp.android.ui.theme.Teal200
import com.alandvgarcia.tmdbapp.database.appContext
import com.alandvgarcia.tmdbapp.db.Movie
import com.alandvgarcia.tmdbapp.network.ApiSettings
import com.alandvgarcia.tmdbapp.network.enum.MovieApiTypeEnum
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.min

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
                    NavHostView()
                }
            }
        }
    }
}


@Composable
fun NavHostView() {

    val navController = rememberNavController()

    val items = listOf(
        Screen.Popular,
        Screen.TopRated,
        Screen.Lasted
    )

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        NavHost(navController = navController, startDestination = "popular") {
            composable(Screen.Popular.route) { MoviesView(movieApiTypeEnum = MovieApiTypeEnum.POPULAR) }
            composable(Screen.TopRated.route) { MoviesView(movieApiTypeEnum = MovieApiTypeEnum.TOP_RATED) }
            composable(Screen.Lasted.route) { MoviesView(movieApiTypeEnum = MovieApiTypeEnum.LATEST) }
        }
    }

}

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Popular : Screen("popular", R.string.popular)
    object TopRated : Screen("topRated", R.string.topRated)
    object Lasted : Screen("lasted", R.string.lasted)
}

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

const val urlImage = "https://image.tmdb.org/t/p/w400"

@Composable
fun MovieListView(
    movies: List<Movie>,
    isLoading: Boolean,
    refreshData: Boolean,
    onRefresh: () -> Unit,
    onPageEnd: () -> Unit,
    onChangeCategory: () -> Unit,
) {

    val scrollState = rememberLazyListState()
    val scrollOffset: Float = min(
        1f,
        1 - (scrollState.firstVisibleItemScrollOffset / 600f +
                scrollState.firstVisibleItemIndex)
    )

    Scaffold {
        SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing = refreshData), onRefresh = {
            if (!refreshData)
                onRefresh()
        }) {
            Column {
                CategoryDetailsCollapsingToolbar("Movie", scrollOffset) {
                    onChangeCategory()
                }
                Spacer(modifier = Modifier.height(2.dp))
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategoryDetailsCollapsingToolbar(
    title: String,
    scrollOffset: Float,
    changeCategory: () -> Unit
) {
    val size by animateDpAsState(targetValue = max(0.dp, 112.dp * scrollOffset))
    Row(
        modifier = Modifier
            .height(size)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            maxLines = 1,
            modifier = Modifier.padding(16.dp)
        )
        Chip(onClick = { changeCategory() }, modifier = Modifier.padding(16.dp)) {
            Text(text = "Movie Category")
        }

    }
}


@Preview(showBackground = true)
@Composable
fun MoviesViewContent() {
    TMDB_AppTheme {
        MovieListView(
            movies = listOf(),
            false,
            false,
            onRefresh = {},
            onPageEnd = {},
            onChangeCategory = {})
    }
}