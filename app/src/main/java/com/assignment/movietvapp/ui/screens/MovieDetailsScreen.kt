package com.assignment.movietvapp.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
//import androidx.media3.common.MediaItem
//import androidx.media3.common.MediaMetadata
//import androidx.media3.datasource.DefaultHttpDataSource
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.exoplayer.hls.HlsMediaSource
//import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

import com.ar.youtubeextractor.core.YouTubeExtractor
import com.ar.youtubeextractor.core.onError
import com.ar.youtubeextractor.core.onSuccess
import com.assignment.movietvapp.R
import com.assignment.movietvapp.data.local.WatchListModel
import com.assignment.movietvapp.data.models.Cast
import com.assignment.movietvapp.data.remote.dto.MovieDetailsDTO
import com.assignment.movietvapp.data.remote.response.MovieResponse
import com.assignment.movietvapp.ui.components.HomeSmallThumb
import com.assignment.movietvapp.ui.navigation.MovieAppScreen
import com.assignment.movietvapp.ui.viewmodels.MovieDetailsViewModel
import com.assignment.movietvapp.ui.viewmodels.WatchListViewModel
import com.assignment.movietvapp.utils.CenteredCircularProgressIndicator
import com.assignment.movietvapp.utils.Constants
import com.assignment.movietvapp.utils.Constants.Companion.BASE_POSTER_IMAGE_URL
import com.assignment.movietvapp.utils.MovieState
import com.assignment.movietvapp.utils.ShowError
import com.assignment.movietvapp.utils.netflixFamily
//import com.ar.youtubeextractor.core.YouTubeExtractor
//import com.ar.youtubeextractor.core.onError
//import com.ar.youtubeextractor.core.onSuccess
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.collections.get

@Composable
fun MovieDetailsScreen(navController: NavHostController, movieId: String) {
    var primaryColor = Color(0xFFFFFFFF)
    var viewModel: MovieDetailsViewModel = hiltViewModel()
    var watchListViewModel: WatchListViewModel = hiltViewModel()
    val detailsMovieState by viewModel.detailsMovieResponses.collectAsState()
    val castMovieState by viewModel.castMovieResponses.collectAsState()
    val similarMovieState by viewModel.similarMovieResponses.collectAsState()
    val trailerKey by viewModel.videotrailerResponse.collectAsState()
    var trailer by remember { mutableStateOf<String?>(null) }


    // Fetch data on launch (optional):
    LaunchedEffect(Unit) {
        viewModel.fetchMoviesDetails(movieId)
        viewModel.fetchSimilarMovies(movieId)
        viewModel.fetchCasteOfMovies(movieId)
        viewModel.fetchMovieVideo(movieId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColor)
            .verticalScroll(rememberScrollState())
    ) {
        when (detailsMovieState) {
            is MovieState.Success -> {
                val moviesInfo = (detailsMovieState as MovieState.Success<MovieDetailsDTO?>).data
                DisplayMovieData(moviesInfo, navController,watchListViewModel)
            }

            is MovieState.Error -> {
                ShowError((detailsMovieState as MovieState.Error).message)
            }

            is MovieState.Loading -> {
                CenteredCircularProgressIndicator()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (castMovieState) {
            is MovieState.Success -> {
                val castList =
                    ((castMovieState as MovieState.Success<List<Cast>?>).data as? List<Cast>)
                        ?: emptyList()
                CastMediaSection(castList)
            }

            is MovieState.Error -> {
                ShowError((castMovieState as MovieState.Error).message)
            }

            is MovieState.Loading -> {
                CenteredCircularProgressIndicator()
                // You can show a separate loading indicator here if desired
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
//        when (trailerKey) {
//            is MovieState.Success -> {
//                val movieList = (trailer as MovieState.Success<String?>).data
//               //Log.e("TAG","====>>>${movieList});
//
//            }
//
//            is MovieState.Error -> {
//                ShowError((similarMovieState as MovieState.Error).message)
//            }
//
//            is MovieState.Loading -> {
//                CenteredCircularProgressIndicator()
//
//            }
//        }
        when (similarMovieState) {
            is MovieState.Success -> {
                val movieList = (similarMovieState as MovieState.Success<MovieResponse?>).data
                if (movieList != null) {
                    SimilarMediaSection(movieList,navController)
                }
            }

            is MovieState.Error -> {
                ShowError((similarMovieState as MovieState.Error).message)
            }

            is MovieState.Loading -> {
                CenteredCircularProgressIndicator()

            }
        }
    }
}


@Composable
fun CastMediaSection(castList: List<Cast>) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, top = 22.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cast",
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.black),
                fontFamily = netflixFamily,
                fontSize = 18.sp
            )
        }
        LazyRow(
            modifier = Modifier.padding(
                start = 22.dp, end = 22.dp, top = 8.dp, bottom = 0.dp
            )
        ) {
            items(castList.size) { index ->
                CastMemberItem(castList[index])
            }
        }
    }


}

@Composable
fun CastMemberItem(cast: Cast) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(120.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        if (cast.profilePath != null) {
            Image(
                painter = rememberAsyncImagePainter(model = Constants.BASE_POSTER_IMAGE_URL + cast.profilePath),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.user), // Replace with your placeholder
                    contentDescription = "Cast Member Placeholder",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black), startY = 100f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = cast.name, style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.Black,
                    fontFamily = netflixFamily,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            )
        }
    }


}

@Composable
fun DisplayMovieData(
    moviesInfo: MovieDetailsDTO?,
    navController: NavHostController,
    watchListViewModel: WatchListViewModel
) {
    var showTrailer by remember { mutableStateOf(false) }
    watchListViewModel.exist(moviesInfo!!.id)
    var  exist = watchListViewModel.exist.value

    Log.e("TAG_exist_>", "DisplayMovieData: "+exist )
    var context = LocalContext.current
    val date = SimpleDateFormat.getDateInstance().format(Date())
    val myListMovie = WatchListModel(
        mediaId = moviesInfo!!.id,
        imagePath = moviesInfo.posterPath,
        title = moviesInfo.title,
        releaseDate = moviesInfo.releaseDate,
        rating = moviesInfo.voteAverage,
        addedOn = date
    )
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black), startY = 100f
                    )
                )

        ) {

            Image(painter = rememberAsyncImagePainter(Constants.BASE_BACKDROP_IMAGE_URL + moviesInfo!!.backdropPath),
                contentDescription = "Backdrop Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .graphicsLayer {
                        alpha = 0.7f
                    })
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black), startY = 100f
                        )
                    )
            )
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                tint = Color.Black,
                contentDescription = "",
                modifier = Modifier
                    .clickable {
                        navController.popBackStack()
                    }
                    .padding(24.dp)
            )
            Image(
                painter = rememberAsyncImagePainter(BASE_POSTER_IMAGE_URL + moviesInfo.posterPath),
                contentDescription = "Poster Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(160.dp)
                    .height(240.dp)
                    .offset(y = 75.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(8.dp))
            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 4.dp),
                onClick = {
                    if (exist != 0) {
                        watchListViewModel.removeFromWatchList(mediaId = moviesInfo.id)
                        Toast.makeText(
                            context,
                            "Remove From your Watch List",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        watchListViewModel.addToWatchList(myListMovie)
                        Toast.makeText(
                            context,
                            "Added to your Watch List",
                            Toast.LENGTH_SHORT
                        ).show()
                        Toast.makeText(
                            context,
                            "Added to your Watch List",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                Icon(
                    imageVector = if (exist != 0) {
                        Icons.Default.Bookmark
                    } else {
                        Icons.Default.BookmarkBorder
                    },
                    tint = Color.Black,
                    contentDescription = "Review"
                )
            }

        }
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = moviesInfo!!.title,
            fontFamily = netflixFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))


        Text(text="▶ Play Trailer",
            fontFamily = netflixFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,

            modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Blue),
                        startY = 10f // Adjust as needed
                    )
                )
                .clickable {
                    showTrailer = true
                    //  composable(

                    //)
                    // Add your action here
                }
                .padding(8.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.Center
            ) {
                items(moviesInfo.genres.size) { index ->
                    GenreChip(genre = moviesInfo.genres[index].name)
                }
            }

        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            ) {
                val context = LocalContext.current
                MovieField(context.getString(R.string.release_date), moviesInfo.releaseDate)
                MovieField("Duration", moviesInfo?.runtime.toString() + " min.")
                MovieField("Rating", "⭐ " + moviesInfo.voteAverage)
                MovieField("Language", moviesInfo.spokenLanguages[0].name)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        moviesInfo.overview?.let { OverviewSection(it, moviesInfo.tagline) }

    }

    if (showTrailer) {
        playvide(                          // <- a composable
            movieId = moviesInfo.id.toString(),
            onClose = { showTrailer = false }
        )
    }
}


@Composable
fun OverviewSection(overview: String, tagline: String?) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 22.dp),
            text = tagline!!,
            fontFamily = netflixFamily,
            fontSize = 17.sp,
            fontStyle = FontStyle.Italic,
            color = colorResource(id = R.color.black),
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))


        Text(
            modifier = Modifier.padding(horizontal = 22.dp),
            text = overview,
            fontFamily = netflixFamily,
            fontSize = 16.sp,
            color = colorResource(id = R.color.black),
            lineHeight = 16.sp
        )

    }
}

@Composable
fun GenreChip(genre: String) {
    Card(
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, Color.Black),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text = genre,
            color = Color.Black,
            fontFamily = netflixFamily,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(8.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun MovieField(name: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = name,
            fontFamily = netflixFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 1.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
            text = value,
            fontFamily = netflixFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp),
        )
    }
}

@Composable
fun SimilarMediaSection(
    media: MovieResponse,
    navController: NavHostController,
) {
    val mediaList = media.results
    if (mediaList.isNotEmpty()) Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, top = 22.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Similar Movies",
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.black),
                fontFamily = netflixFamily,
                fontSize = 18.sp
            )


        }
        LazyRow(
            modifier = Modifier.padding(
                start = 22.dp, end = 22.dp, top = 8.dp, bottom = 16.dp
            )
        ) {
            items(media.results.size) {
                HomeSmallThumb(
                    BASE_POSTER_IMAGE_URL + mediaList[it].posterPath
                ) {
                    navController.navigate(MovieAppScreen.MOVIE_HOME_DETAILS.route + "/${mediaList[it].id}")
                }
            }
        }
    }

}
@Composable
fun playvide(movieId: String,onClose: () -> Unit){
    var viewModel: MovieDetailsViewModel = hiltViewModel()
    val trailerKey by viewModel.videotrailerResponse.collectAsState()
    var trailer by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {

        viewModel.fetchMovieVideo(movieId)


    }

    when (trailerKey) {
        is MovieState.Success -> {
            val movieList = (trailerKey as MovieState.Success<String?>).data

            TrailerPlayerScreen(movieList.toString())


        }

        is MovieState.Error -> {
            ShowError((trailer as MovieState.Error).message)
        }

        is MovieState.Loading -> {
            CenteredCircularProgressIndicator()

        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun TrailerPlayerScreen(youtubeKey: String) {
    val context = LocalContext.current
    var videoUrl by remember { mutableStateOf<String?>(null) }
    var videoTitle by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(youtubeKey) {
        val youtubeExtractor = YouTubeExtractor()

        val url = "https://www.youtube.com/watch?v=$youtubeKey"

        youtubeExtractor.extractVideoData(url).onSuccess{

            videoUrl = it.streamingData.hlsManifestUrl
            videoTitle = it.videoDetails?.title
        }.onError{  //error ->
            //error message
        }


    }

    if (videoUrl == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setDisplayTitle(videoTitle)   // this is the visible name
                    .setTitle(videoTitle)          // legacy player view reads this too
                    .build()
            )
            .build()



        // Create a data source factory.
        val dataSourceFactory: DefaultHttpDataSource.Factory = DefaultHttpDataSource.Factory()
// Create a HLS media source pointing to a playlist uri.
        val hlsMediaSource =
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUrl!!))
// Create a player instance.

        val exoplayer = remember {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
        }
        DisposableEffect(Unit) { onDispose { exoplayer.release() } }

        BackHandler {

            exoplayer.stop()
            exoplayer.release()
            //onBack()
        }



        exoplayer.setMediaSource(hlsMediaSource)


        //  exoplayer.prepare()
        Box(Modifier.fillMaxSize()) {
            AndroidView(factory = { PlayerView(it).apply { player = exoplayer

            }
            } ,
                modifier = Modifier.fillMaxSize()
                    .aspectRatio(16 / 9f))

            // Title overlay
//            Text(
//                text = videoTitle?: "KID",
//                color = Color.White,
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier
//                    .align(Alignment.TopStart)
//                    .padding(16.dp)
//                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
//                    .padding(horizontal = 8.dp, vertical = 4.dp)
//            )
        }
    }
}