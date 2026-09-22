package com.example.careerlaunchsa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.careerlaunchsa.data.AppDatabase
import com.example.careerlaunchsa.data.JobEntity
import com.example.careerlaunchsa.databinding.ActivitySavedJobsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedJobsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedJobsBinding
    private lateinit var jobAdapter: JobAdapter
    private var savedJobsList = mutableListOf<JobEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        setupRecyclerView()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        // Automatically sync and refresh database content whenever page opens
        loadSavedJobsFromRoom()
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter(
            savedJobsList,
            onSaveClick = { selectedJob ->
                deleteJobFromRoom(selectedJob)
            },
            onApplyClick = { selectedJob ->
                Toast.makeText(this, "Applying for ${selectedJob.title}...", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvSavedJobs.layoutManager = LinearLayoutManager(this)
        binding.rvSavedJobs.adapter = jobAdapter
    }

    private fun loadSavedJobsFromRoom() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                val jobsFromDb = db.jobDao().getAllSavedJobs()

                withContext(Dispatchers.Main) {
                    // Update layout cards list
                    jobAdapter.updateList(jobsFromDb)
                    
                    // Dynamic verification badge text: displays the real-time record count from Room
                    binding.tvSavedCountSubtitle.text = "${jobsFromDb.size} jobs saved offline"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SavedJobsActivity, "Error loading saved jobs", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteJobFromRoom(job: JobEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                db.jobDao().deleteJob(job)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SavedJobsActivity, "Removed ${job.title}", Toast.LENGTH_SHORT).show()
                    loadSavedJobsFromRoom() // Refresh screen immediately
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SavedJobsActivity, "Error removing job", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_saved_jobs
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_browse -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_saved_jobs -> true
                R.id.nav_applications -> {
                    startActivity(Intent(this, TrackerActivity::class.java))
                    true
                }
                R.id.nav_cv -> {
                    startActivity(Intent(this, CvActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, activity_settings::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
