package com.example.careerlaunchsa.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_jobs")
data class CachedJobEntity(
    @PrimaryKey val jobId: String,
    val title: String,
    val company: String,
    val location: String,
    val category: String,
    val description: String
)
