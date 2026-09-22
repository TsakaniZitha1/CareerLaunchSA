package com.example.careerlaunchsa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.careerlaunchsa.data.JobEntity
import com.example.careerlaunchsa.databinding.ActivityItemJobBinding

class JobAdapter(
    private val jobList: MutableList<JobEntity>,
    private val onSaveClick: (JobEntity) -> Unit,
    private val onApplyClick: (JobEntity) -> Unit
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(val binding: ActivityItemJobBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ActivityItemJobBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        holder.binding.apply {
            tvJobTitle.text = job.title
            tvCompanyName.text = job.company
            tvJobLocation.text = job.location

            // Dynamic company avatar initials
            val initials = if (job.company.length >= 2) job.company.substring(0, 2).uppercase() else "SA"
            tvCompanyAvatar.text = initials

            btnSaveOffline.setOnClickListener { onSaveClick(job) }
            btnApply.setOnClickListener { onApplyClick(job) }
        }
    }

    override fun getItemCount(): Int = jobList.size

    fun updateList(newList: List<JobEntity>) {
        jobList.clear()
        jobList.addAll(newList)
        notifyDataSetChanged()
    }
}
