package com.example.careerlaunchsa

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.careerlaunchsa.data.AppDatabase
import com.example.careerlaunchsa.data.JobEntity
import com.example.careerlaunchsa.databinding.ActivityMainBinding
import com.example.careerlaunchsa.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var jobAdapter: JobAdapter
    private var allJobsList = mutableListOf<JobEntity>()
    private var currentCategory = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadITSampleJobs() // Start with local data so screen is never blank
        fetchJobsFromApi()

        setupSearchAndFilters()
        setupBottomNavigation()
        setupHeaderButtons()
    }

    private fun fetchJobsFromApi() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.fetchJobs()
                val jobsFromNetwork = mutableListOf<JobEntity>()

                response.data?.let { dataElement ->
                    val gson = Gson()
                    if (dataElement.isJsonArray) {
                        val type = object : TypeToken<List<JobEntity>>() {}.type
                        val list: List<JobEntity> = gson.fromJson(dataElement, type)
                        jobsFromNetwork.addAll(list)
                    } else if (dataElement.isJsonObject) {
                        val dataObject = dataElement.asJsonObject
                        if (dataObject.has("jobs")) {
                            val jobsElement = dataObject.get("jobs")
                            val type = object : TypeToken<List<JobEntity>>() {}.type
                            val list: List<JobEntity> = gson.fromJson(jobsElement, type)
                            jobsFromNetwork.addAll(list)
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    if (jobsFromNetwork.isNotEmpty()) {
                        allJobsList.clear()
                        allJobsList.addAll(jobsFromNetwork)
                        jobAdapter.updateList(allJobsList)
                        Toast.makeText(this@MainActivity, "Loaded jobs from REST API!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "API returned no jobs, using offline data", Toast.LENGTH_LONG).show()
                        loadITSampleJobs()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "API Error: ${e.message}", Toast.LENGTH_LONG).show()
                    loadITSampleJobs()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter(
            allJobsList,
            onSaveClick = { selectedJob ->
                saveJobToRoom(selectedJob)
            },
            onApplyClick = { selectedJob ->
                Toast.makeText(this, "Applying for ${selectedJob.title}...", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvJobFeed.layoutManager = LinearLayoutManager(this)
        binding.rvJobFeed.adapter = jobAdapter
    }

    private fun saveJobToRoom(job: JobEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                db.jobDao().saveJob(job)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Saved ${job.title} offline!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error saving job", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadITSampleJobs() {
        allJobsList = mutableListOf(
            JobEntity("1", "Junior Kotlin Developer", "Capitec", "Stellenbosch", "Software Dev", "Closes: 15 Oct"),
            JobEntity("2", "SQL Analyst", "Standard Bank", "Johannesburg", "Data & SQL", "Closes: 28 Sep"),
            JobEntity("3", "Full Stack Developer", "Vodacom", "Midrand", "Software Dev", "Closes: 10 Oct"),
            JobEntity("4", "Cloud Engineer", "Derivco", "Durban", "Cloud & DevOps", "Closes: 05 Oct")
        )
        jobAdapter.updateList(allJobsList)
    }

    private fun setupSearchAndFilters() {
        binding.etSearchJobs.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyCombinedFilter(s.toString(), currentCategory)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipGroupCategories.setOnCheckedStateChangeListener { _, checkedIds ->
            currentCategory = when {
                checkedIds.contains(R.id.chipSoftware) -> "Software Dev"
                checkedIds.contains(R.id.chipData) -> "Data & SQL"
                checkedIds.contains(R.id.chipCloud) -> "Cloud & DevOps"
                else -> "All"
            }
            applyCombinedFilter(binding.etSearchJobs.text.toString(), currentCategory)
        }
    }

    private fun applyCombinedFilter(query: String, category: String) {
        val filteredList = allJobsList.filter { job ->
            val matchesCategory = (category == "All" || job.category.equals(category, ignoreCase = true))
            val matchesSearch = job.title.contains(query, ignoreCase = true) || job.company.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        jobAdapter.updateList(filteredList)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_browse
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_browse -> true
                R.id.nav_saved_jobs -> {
                    startActivity(Intent(this, SavedJobsActivity::class.java))
                    true
                }
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

    private fun setupHeaderButtons() {
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, CvActivity::class.java))
        }
    }
}
