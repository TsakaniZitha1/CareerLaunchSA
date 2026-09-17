package com.example.careerlaunchsa.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface JobDao {
    @Query("SELECT * FROM saved_jobs")
    suspend fun getAllSavedJobs(): List<JobEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveJob(job: JobEntity)

    @Delete
    suspend fun deleteJob(job: JobEntity)
}