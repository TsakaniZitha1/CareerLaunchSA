package com.example.careerlaunchsa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.careerlaunchsa.data.AppDatabase
import com.example.careerlaunchsa.databinding.ActivityTrackerBinding
import com.example.careerlaunchsa.network.JobResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackerBinding
    private lateinit var trackerAdapter: JobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTrackerList()
        loadSavedApplications()
    }

    private fun setupTrackerList() {
        trackerAdapter = JobAdapter(
            mutableListOf(),
            onSaveClick = { }, // No-op inside tracker
            onApplyClick = { }  // No-op inside tracker
        )
        binding.rvTrackerList.layoutManager = LinearLayoutManager(this)
        binding.rvTrackerList.adapter = trackerAdapter
    }

    private fun loadSavedApplications() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val savedEntities = db.jobDao().getAllSavedJobs()
            val mappedJobs = savedEntities.map { entity ->
                JobResponse(
                    id = entity.jobId,
                    title = entity.title,
                    company = entity.company,
                    location = entity.location,
                    category = entity.category,
                    description = entity.description
                )
            }
            withContext(Dispatchers.Main) {
                trackerAdapter.updateData(mappedJobs)
            }
        }
    }
}
