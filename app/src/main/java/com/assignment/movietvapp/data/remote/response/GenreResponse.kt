package com.assignment.movietvapp.data.remote.response

import com.assignment.movietvapp.data.models.Genre
import com.google.gson.annotations.SerializedName

data class GenreResponse(
    @SerializedName("genres")
    val genres: List<Genre>
)
