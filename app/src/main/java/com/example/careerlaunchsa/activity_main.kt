package com.example.careerlaunchsa

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.careerlaunchsa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var jobAdapter: JobAdapter
    private var allJobsList = mutableListOf<JobModel>()
    private var currentCategory = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadITSampleJobs()
        setupSearchAndFilters()
        setupBottomNavigation()

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter(allJobsList) { selectedJob ->
            // Offline save or application action
        }
        binding.rvJobFeed.layoutManager = LinearLayoutManager(this)
        binding.rvJobFeed.adapter = jobAdapter
    }

    private fun loadITSampleJobs() {
        allJobsList = mutableListOf(
            JobModel(
                id = "1",
                title = "Junior Kotlin Developer",
                company = "Capitec Tech Hub",
                location = "Stellenbosch, WC",
                closingDate = "Closes: 15 Oct 2026",
                category = "Software Dev"
            ),
            JobModel(
                id = "2",
                title = "Database & SQL Analyst",
                company = "Standard Bank IT",
                location = "Johannesburg, GP",
                closingDate = "Closes: 28 Sep 2026",
                category = "Data & SQL"
            ),
            JobModel(
                id = "3",
                title = "Graduate Full Stack Developer",
                company = "Vodacom Digital",
                location = "Midrand, GP",
                closingDate = "Closes: 10 Oct 2026",
                category = "Software Dev"
            ),
            JobModel(
                id = "4",
                title = "Junior Cloud Engineer",
                company = "Derivco",
                location = "Durban, KZN",
                closingDate = "Closes: 05 Oct 2026",
                category = "Cloud & DevOps"
            )
        )
        jobAdapter.updateList(allJobsList)
    }

    private fun setupSearchAndFilters() {
        // Live Text Search
        binding.etSearchJobs.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyCombinedFilter(s.toString(), currentCategory)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Category Chip Selection
        binding.chipGroupCategories.setOnCheckedStateChangeListener { group, checkedIds ->
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
            val matchesSearch = job.title.contains(query, ignoreCase = true) ||
                    job.company.contains(query, ignoreCase = true) ||
                    job.location.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        jobAdapter.updateList(filteredList)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_browse
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_browse -> true
                R.id.nav_applications -> {
                    startActivity(Intent(this, TrackerActivity::class.java))
                    true
                }
                R.id.nav_cv -> {
                    startActivity(Intent(this, CvActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}