package com.example.careerlaunchsa.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedJobDao {
    @Query("SELECT * FROM cached_jobs")
    suspend fun getAllCachedJobs(): List<CachedJobEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedJobs(jobs: List<CachedJobEntity>)

    @Query("DELETE FROM cached_jobs")
    suspend fun clearCachedJobs()
}
