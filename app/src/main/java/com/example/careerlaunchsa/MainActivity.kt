package com.example.careerlaunchsa

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.careerlaunchsa.data.AppDatabase
import com.example.careerlaunchsa.data.CachedJobEntity
import com.example.careerlaunchsa.data.JobEntity
import com.example.careerlaunchsa.databinding.ActivityMainBinding
import com.example.careerlaunchsa.network.RetrofitClient
import com.example.careerlaunchsa.utils.NetworkObserver
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var jobAdapter: JobAdapter
    private var allJobsList = mutableListOf<JobEntity>()
    private var currentCategory = "All"
    private lateinit var networkObserver: NetworkObserver
    private var isCurrentlyOnline = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkObserver = NetworkObserver(this)

        setupRecyclerView()
        setupSearchAndFilters()
        setupBottomNavigation()
        setupHeaderButtons()
        observeNetworkState()
    }

    private fun observeNetworkState() {
        lifecycleScope.launch {
            networkObserver.observe().collectLatest { isOnline ->
                isCurrentlyOnline = isOnline
                if (isOnline) {
                    binding.bannerOffline.visibility = View.GONE
                    fetchJobsFromApi()
                } else {
                    binding.bannerOffline.visibility = View.VISIBLE
                    binding.tvOfflineBanner.text = "Offline Mode — Displaying cached jobs"
                    loadCachedJobsOrDefaults()
                }
            }
        }
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

                val db = AppDatabase.getDatabase(applicationContext)

                withContext(Dispatchers.Main) {
                    if (jobsFromNetwork.isNotEmpty()) {
                        allJobsList.clear()
                        allJobsList.addAll(jobsFromNetwork)
                        applyCombinedFilter(binding.etSearchJobs.text.toString(), currentCategory)
                        binding.bannerOffline.visibility = View.GONE
                        Toast.makeText(this@MainActivity, "Loaded latest jobs online!", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.bannerOffline.visibility = View.VISIBLE
                        binding.tvOfflineBanner.text = "Offline Mode — Displaying cached jobs"
                        loadCachedJobsOrDefaults()
                    }
                }

                // Cache jobs into Room database for offline accessibility
                if (jobsFromNetwork.isNotEmpty()) {
                    val cachedEntities = jobsFromNetwork.map { it.toCachedJobEntity() }
                    db.cachedJobDao().clearCachedJobs()
                    db.cachedJobDao().insertCachedJobs(cachedEntities)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.bannerOffline.visibility = View.VISIBLE
                    binding.tvOfflineBanner.text = "Offline Mode — Displaying cached jobs"
                    loadCachedJobsOrDefaults()
                }
            }
        }
    }

    private fun loadCachedJobsOrDefaults() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            val cachedJobs = db.cachedJobDao().getAllCachedJobs()

            withContext(Dispatchers.Main) {
                if (cachedJobs.isNotEmpty()) {
                    allJobsList.clear()
                    allJobsList.addAll(cachedJobs.map { it.toJobEntity() })
                    applyCombinedFilter(binding.etSearchJobs.text.toString(), currentCategory)
                } else {
                    loadITSampleJobs()
                }
            }
        }
    }

    private fun loadITSampleJobs() {
        val samples = listOf(
            JobEntity("1", "Junior Kotlin Developer", "Capitec", "Stellenbosch", "Software Dev", "Closes: 15 Oct"),
            JobEntity("2", "SQL Analyst", "Standard Bank", "Johannesburg", "Data & SQL", "Closes: 28 Sep"),
            JobEntity("3", "Full Stack Developer", "Vodacom", "Midrand", "Software Dev", "Closes: 10 Oct"),
            JobEntity("4", "Cloud Engineer", "Derivco", "Durban", "Cloud & DevOps", "Closes: 05 Oct")
        )
        allJobsList.clear()
        allJobsList.addAll(samples)
        applyCombinedFilter(binding.etSearchJobs.text.toString(), currentCategory)

        // Save sample jobs to cache database so subsequent offline launches retain them
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            db.cachedJobDao().insertCachedJobs(samples.map { it.toCachedJobEntity() })
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

    private fun CachedJobEntity.toJobEntity() = JobEntity(jobId, title, company, location, category, description)
    private fun JobEntity.toCachedJobEntity() = CachedJobEntity(jobId, title, company, location, category, description)
}
