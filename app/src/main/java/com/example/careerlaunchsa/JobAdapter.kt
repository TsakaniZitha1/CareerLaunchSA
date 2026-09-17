package com.example.careerlaunchsa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.careerlaunchsa.databinding.ActivityItemJobBinding
import com.example.careerlaunchsa.network.JobResponse

class JobAdapter(
    private val jobList: MutableList<JobResponse>,
    private val onSaveClick: (JobResponse) -> Unit,
    private val onApplyClick: (JobResponse) -> Unit
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(val binding: ActivityItemJobBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ActivityItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        holder.binding.tvJobTitle.text = job.title
        holder.binding.tvCompanyName.text = job.company
        holder.binding.tvJobLocation.text = job.location

        holder.binding.btnSaveOffline.setOnClickListener { onSaveClick(job) }
        holder.binding.btnApply.setOnClickListener { onApplyClick(job) }
    }

    override fun getItemCount(): Int = jobList.size

    fun updateData(newJobs: List<JobResponse>) {
        jobList.clear()
        jobList.addAll(newJobs)
        notifyDataSetChanged()
    }
}
