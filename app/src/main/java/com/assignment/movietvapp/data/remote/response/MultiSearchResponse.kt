package com.assignment.movietvapp.data.remote.response

import com.assignment.movietvapp.data.models.Search
import com.google.gson.annotations.SerializedName

data class MultiSearchResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<Search>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

