package com.example.careerlaunchsa.network

import retrofit2.http.GET

data class JobResponse(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val category: String,
    val description: String
)

interface JobApiService {
    @GET("jobs")
    suspend fun fetchJobs(): List<JobResponse>
}