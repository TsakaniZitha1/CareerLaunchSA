package com.example.careerlaunchsa.network

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class JobResponse(
    @SerializedName("data")
    val data: JsonElement?
)