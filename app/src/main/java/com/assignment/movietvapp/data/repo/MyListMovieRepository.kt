package com.assignment.movietvapp.data.repo

import com.assignment.movietvapp.data.local.MovieDao
import com.assignment.movietvapp.data.local.WatchListModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MyListMovieRepository @Inject constructor(private val movieDao: MovieDao) {
    suspend fun insertMovieInList(myListMovie: WatchListModel) {
        movieDao.insertMovieInList( myListMovie)
    }

    suspend fun removeFromList(mediaId: Int) {
        movieDao.removeFromList(mediaId)
    }

    suspend fun deleteList() {
        movieDao.deleteList()
    }

    suspend fun exist(mediaId: Int): Int {
        return movieDao.exists(mediaId)
    }

    fun getAllData(): Flow<List<WatchListModel>> {
        return movieDao.getAllWatchListData()
    }
}