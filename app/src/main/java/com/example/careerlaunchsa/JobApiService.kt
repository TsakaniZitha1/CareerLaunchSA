package com.example.careerlaunchsa.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface JobApiService {

    // Replaced the broken /public endpoint with the correct records endpoint
    @GET("v2/records/e1d0ce6c-ab56-4c56-9ba6-9afd5a23da51")
    suspend fun fetchJobs(): JobResponse

    companion object {
        private const val BASE_URL = "https://api.myjson.online/"

        fun create(): JobApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(JobApiService::class.java)
        }
    }
}