package com.assignment.movietvapp.data.remote.response

import com.assignment.movietvapp.data.models.Cast
import com.google.gson.annotations.SerializedName

data class CastResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cast")
    val castResult: List<Cast>
)
