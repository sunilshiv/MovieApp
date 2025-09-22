package com.assignment.movietvapp.ui.screens

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.assignment.movietvapp.data.models.Genre
import com.assignment.movietvapp.data.models.Movies
import com.assignment.movietvapp.data.remote.response.GenreResponse
import com.assignment.movietvapp.data.remote.response.MovieResponse
import com.assignment.movietvapp.ui.components.ErrorStrip
import com.assignment.movietvapp.ui.components.HomeHeader
import com.assignment.movietvapp.ui.components.HomeSmallThumb
import com.assignment.movietvapp.ui.components.HomeThumbRectWithTitle
import com.assignment.movietvapp.ui.components.HomeThumbWithTitle
import com.assignment.movietvapp.ui.components.NoInternetScreen
import com.assignment.movietvapp.ui.navigation.MovieAppScreen
import com.assignment.movietvapp.ui.navigation.RootNavigation
import com.assignment.movietvapp.ui.theme.TMDBComposeTheme
import com.assignment.movietvapp.ui.viewmodels.HomeViewModel
import com.assignment.movietvapp.utils.AutoSlidingCarousel
import com.assignment.movietvapp.utils.CenteredCircularProgressIndicator
import com.assignment.movietvapp.utils.Constants
import com.assignment.movietvapp.utils.Constants.Companion.discoverListScreen
import com.assignment.movietvapp.utils.Constants.Companion.nowPlayingAllListScreen
import com.assignment.movietvapp.utils.Constants.Companion.popularAllListScreen
import com.assignment.movietvapp.utils.Constants.Companion.upcomingListScreen
import com.assignment.movietvapp.utils.IncludeApp
import com.assignment.movietvapp.utils.MovieState
import com.assignment.movietvapp.utils.ShowError
import com.assignment.movietvapp.utils.SimpleLightTopAppBar

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashBoardScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TMDBComposeTheme {
                Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
                    RootNavigation()
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MovieHomeScreen(navController: NavHostController) {
    var viewModel: HomeViewModel = hiltViewModel()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        viewModel.registerNetwork(LocalContext.current)
    }
    val networkResult by viewModel.networkType.collectAsState()

    Log.e("TAG_networkResult", "MovieHomeScreen: $networkResult")
    Scaffold(topBar = {
        SimpleLightTopAppBar(
            "Movie TVCompose",
            { navController.navigate(MovieAppScreen.MOVIE_SEARCH.route) },
            { navController.navigate(MovieAppScreen.MOVIE_WATCHLIST.route) }
        )
    }) { padding: PaddingValues ->
        if (networkResult != "No Connection") {
            Column(modifier = Modifier.padding(padding)) {
                HomeScreen(viewModel, navController)
            }
        } else {
            NoInternetScreen()
        }
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), navController: NavHostController) {
    val showExitDialog = remember { mutableStateOf(false) }
    //Exit Dialog Code
    if (showExitDialog.value) {
        IncludeApp().showExitDialog {
            showExitDialog.value = false
        }
    }
    BackHandler(enabled = true) {
        if (showExitDialog.value) {
            // Exit the app
            android.os.Process.killProcess(android.os.Process.myPid())
        } else {
            showExitDialog.value = true
        }
    }


    val discoveryMovieState by viewModel.discoveryMovieResponses.collectAsState()
    val trendingMovieState by viewModel.trendingMovieResponses.collectAsState()
    val nowPlayingMovieState by viewModel.nowPlayingMoviesResponses.collectAsState()
    val upcomingMovieState by viewModel.upcomingMoviesResponses.collectAsState()
    val genresMovieState by viewModel.genresMoviesResponses.collectAsState()
    val moviesLazyPagingItems = viewModel.popularAllListState.collectAsLazyPagingItems()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (trendingMovieState) {
            is MovieState.Success -> {
                val movies =
                    (trendingMovieState as MovieState.Success<MovieResponse?>).data?.results.orEmpty()
                DisplayHomeSlider(movies.take(5))
            }

            is MovieState.Error -> {
                val errorMessage = (trendingMovieState as MovieState.Error).message
                ShowError(errorMessage)
            }

            is MovieState.Loading -> {
                CenteredCircularProgressIndicator()
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        when (genresMovieState) {
            is MovieState.Success -> {

                val genres = (genresMovieState as MovieState.Success<GenreResponse?>).data?.genres

                HomeHeader("Genres", {}, false)
                Spacer(modifier = Modifier.height(8.dp))
                DisplayGenreList(genres, navController)
            }

            is MovieState.Error -> {
                val errorMessage = (genresMovieState as MovieState.Error).message
                ShowError(errorMessage)
            }

            is MovieState.Loading -> {
                CenteredCircularProgressIndicator()
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        when (nowPlayingMovieState) {
            is MovieState.Success -> {

                val movies =
                    (nowPlayingMovieState as MovieState.Success<MovieResponse?>).data?.results.orEmpty()
                HomeHeader(
                    "Now Playing",
                    { navController.navigate(MovieAppScreen.MOVIE_SEE_ALL.route + "/${nowPlayingAllListScreen}") },
                    true
                )


                DisplayMovieList(movies, navController)
            }

            is MovieState.Error -> {
                val errorMessage = (nowPlayingMovieState as MovieState.Error).message
                ShowError(errorMessage)
            }

            is MovieState.Loading -> {
                CenteredCircularProgressIndicator()
            }

        }
        Spacer(modifier = Modifier.height(8.dp))

        HomeHeader(
            "Popular Movies",
            { navController.navigate(MovieAppScreen.MOVIE_SEE_ALL.route + "/${popularAllListScreen}") },
            true
        )
        LazyRow {
            items(moviesLazyPagingItems.itemCount) { index ->
                HomeSmallThumb(
                    Constants.BASE_POSTER_IMAGE_URL + moviesLazyPagingItems[index]?.posterPath
                ) { navController.navigate(MovieAppScreen.MOVIE_HOME_DETAILS.route + "/${moviesLazyPagingItems[index]?.id}") }
            }
            moviesLazyPagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { CenteredCircularProgressIndicator() }
                    }

                    loadState.append is LoadState.Loading -> {
                        item { CenteredCircularProgressIndicator() }
                    }

                    loadState.refresh is LoadState.Error -> {
                        val e = moviesLazyPagingItems.loadState.refresh as LoadState.Error
                        item { e.error.localizedMessage?.let { ErrorStrip(message = it) } }
                    }

                    loadState.append is LoadState.Error -> {
                        val e = moviesLazyPagingItems.loadState.append as LoadState.Error
                        item { e.error.localizedMessage?.let { ErrorStrip(message = it) } }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (discoveryMovieState) {
            is MovieState.Success -> {

                val movies =
                    (discoveryMovieState as MovieState.Success<MovieResponse?>).data?.results.orEmpty()
                HomeHeader(
                    "Discover Movies \uD83C\uDF1F",
                    { navController.navigate(MovieAppScreen.MOVIE_SEE_ALL.route + "/${discoverListScreen}") },
                    true
                )
                DisplayDiscoverList(movies, navController)
            }

            is MovieState.Error -> {
                val errorMessage = (discoveryMovieState as MovieState.Error).message
                ShowError(errorMessage)
            }

            is MovieState.Loading -> {
                CenteredCircularProgressIndicator()
            }

        }
        Spacer(modifier = Modifier.height(16.dp))


        when (upcomingMovieState) {
            is MovieState.Success -> {
                val movies =
                    (upcomingMovieState as MovieState.Success<MovieResponse?>).data?.results.orEmpty()
                HomeHeader(
                    "Upcoming Movies",
                    { navController.navigate(MovieAppScreen.MOVIE_SEE_ALL.route + "/${upcomingListScreen}") },
                    true
                )
                ShowUpcomingUi(movies, navController)
            }

            is MovieState.Error -> {
                val errorMessage = (upcomingMovieState as MovieState.Error).message
                ShowError(errorMessage)
            }

            is MovieState.Loading -> {
                CenteredCircularProgressIndicator()
            }

        }
        Spacer(modifier = Modifier.height(16.dp))


    }
}

@Composable
fun DisplayHomeSlider(
    moviesList: List<Movies>,
) {
    Column {
        AutoSlidingCarousel(
            images = moviesList, modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)

                .padding(8.dp)

        )

    }
}

@Composable
fun ShowUpcomingUi(list: List<Movies>, navController: NavHostController) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        state = rememberLazyListState()
    ) {
        items(list.size) { index ->
            list[index].let {
                HomeThumbWithTitle(it) {
                    navController.navigate(MovieAppScreen.MOVIE_HOME_DETAILS.route + "/${list[index].id}")
                }
            }
        }
    }
}

@Composable
fun DisplayDiscoverList(movies: List<Movies>, navController: NavHostController) {
    LazyRow {
        items(movies.size) { index ->
            movies[index].let {
                HomeThumbRectWithTitle(
                    Constants.BASE_BACKDROP_IMAGE_URL + it.backdropPath, it.title
                ) {
                    navController.navigate(MovieAppScreen.MOVIE_HOME_DETAILS.route + "/${it.id}")
                }
            }

        }
    }
}

@Composable
fun DisplayMovieList(movies: List<Movies>, navController: NavHostController) {
    LazyRow {
        items(movies.size) { index ->
            HomeSmallThumb(Constants.BASE_POSTER_IMAGE_URL + movies[index].posterPath) {
                navController.navigate(MovieAppScreen.MOVIE_HOME_DETAILS.route + "/${movies[index].id}")
            }
        }
    }
}

@Composable
fun DisplayGenreList(genre: List<Genre>?, navController: NavHostController) {
    LazyRow {
        items(genre!!.size) { index ->
            // HomeGenre(genre[index].name) { navController.navigate(MovieAppScreen.MOVIE_GENRE_WISE.route + "/${genre[index].id}" + "/${genre[index].name}") }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SearchBarSample() {
    var text by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }) {
        SearchBar(modifier = Modifier
            .align(Alignment.TopCenter)
            .semantics { traversalIndex = 0f },
            query = text,
            onQueryChange = { text = it },
            onSearch = {
                if (!text.isEmpty()) {
                    expanded = false
                } else {
                    Toast.makeText(context, "Enter the text", Toast.LENGTH_LONG).show()
                }
            },
            placeholder = { Text("Hinted search text") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
            active = expanded,
            onActiveChange = {
                expanded = it
            }) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                repeat(4) { idx ->
                    val resultText = "Suggestion $idx"
                    ListItem(headlineContent = { Text(resultText) },
                        supportingContent = { Text("Additional info") },
                        leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .clickable {
                                text = resultText
                                expanded = false
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp))
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.semantics { traversalIndex = 1f },
        ) {
            val list = List(100) { "Text $it" }
            items(count = list.size) {
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
        }
    }
}