package com.assignment.movietvapp.data.repo

import com.assignment.movietvapp.data.remote.ApiService
import com.assignment.movietvapp.data.remote.dto.MovieDetailsDTO
import com.assignment.movietvapp.data.remote.response.CastResponse
import com.assignment.movietvapp.data.remote.response.MovieResponse
import com.assignment.movietvapp.data.remote.response.VideoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MovieDetailsRepository @Inject constructor(private val apiService: ApiService) {


    fun getMoviesDetailsRepo(movieId: String): Flow<MovieDetailsDTO> = flow {
        val response = apiService.getMoviesDetails(movieId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)


    fun getCastMoviesRepo(movieId: String): Flow<CastResponse> = flow {
        val response = apiService.getMovieCast(movieId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getSimilarMoviesRepo(movieId: String): Flow<MovieResponse> = flow {
        val response = apiService.getSimilarMovies(movieId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getMovieTrailerRepo(movieId: String): Flow<VideoData> = flow {
        val response = apiService.getMovieVideos(movieId.toInt())
        emit(response)
    }.flowOn(Dispatchers.IO)

}