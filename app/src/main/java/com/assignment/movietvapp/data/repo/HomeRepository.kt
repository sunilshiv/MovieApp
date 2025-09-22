package com.assignment.movietvapp.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.assignment.movietvapp.data.models.Movies
import com.assignment.movietvapp.data.paging.MovieGenrePagingSource
import com.assignment.movietvapp.data.paging.MoviePagingSource
import com.assignment.movietvapp.data.remote.ApiService
import com.assignment.movietvapp.data.remote.response.GenreResponse
import com.assignment.movietvapp.data.remote.response.MovieResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiService: ApiService) {


    fun getNowPlayingMoviesRepo(): Flow<MovieResponse> = flow {
        val response = apiService.getNowPlayingMovies(2)
        emit(response)
    }.flowOn(Dispatchers.IO)

//    fun getPopularMoviesRepo(): Flow<MovieResponse> = flow {
//        val response = apiService.getPopularMovies(1)
//        emit(response)
//    }.flowOn(Dispatchers.IO)


    fun getAllMoviesPagination(tags: String): Pager<Int, Movies> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviePagingSource(apiService, tags) }
        )
    }
    fun getGenresWiseMovieRepo(tags: Int): Pager<Int, Movies> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MovieGenrePagingSource(apiService, tags) }
        )
    }

    fun getDiscoverMoviesRepo(): Flow<MovieResponse> = flow {
        val response = apiService.getDiscoverMovies(1)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getTrendingMoviesRepo(): Flow<MovieResponse> = flow {
        val response = apiService.getTrendingMovies(1)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getUpcomingMoviesRepo(): Flow<MovieResponse> = flow {
        val response = apiService.getUpcomingMovies(1)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getMovieGenresRepo(): Flow<GenreResponse> = flow {
        val response = apiService.getMovieGenres()
        emit(response)
    }.flowOn(Dispatchers.IO)



}