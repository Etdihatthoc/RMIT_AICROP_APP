package com.example.ai_crop_doctor.presentation.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_crop_doctor.R
import com.example.ai_crop_doctor.databinding.ItemDiagnosisBinding
import com.example.ai_crop_doctor.domain.model.Diagnosis
import com.example.ai_crop_doctor.util.loadImage
import com.example.ai_crop_doctor.util.toPercentage
import com.example.ai_crop_doctor.util.toRelativeTimeString
import com.bumptech.glide.Glide
import java.io.File

class DiagnosisAdapter(
    private val onItemClick: (Diagnosis) -> Unit
) : ListAdapter<Diagnosis, DiagnosisAdapter.DiagnosisViewHolder>(DiagnosisDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisViewHolder {
        val binding = ItemDiagnosisBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiagnosisViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: DiagnosisViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiagnosisViewHolder(
        private val binding: ItemDiagnosisBinding,
        private val onItemClick: (Diagnosis) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(diagnosis: Diagnosis) {
            binding.apply {
                // Set disease name
                tvDiseaseName.text = diagnosis.diseaseDetected

                // Set confidence badge
                tvConfidenceBadge.text = diagnosis.confidence.toPercentage()
                val confidenceBg = when {
                    diagnosis.confidence >= 0.80 -> R.drawable.bg_confidence_high
                    diagnosis.confidence >= 0.50 -> R.drawable.bg_confidence_medium
                    else -> R.drawable.bg_confidence_low
                }
                tvConfidenceBadge.setBackgroundResource(confidenceBg)

                // Set location
                val locationDisplay = diagnosis.getLocationDisplay()
                if (locationDisplay.isNotBlank()) {
                    tvLocation.visibility = View.VISIBLE
                    tvLocation.text = locationDisplay
                } else {
                    tvLocation.visibility = View.GONE
                }

                // Set date
                tvDate.text = diagnosis.createdAt.toRelativeTimeString()

                // Set expert review badge
                if (diagnosis.expertReviewed) {
                    tvExpertBadge.visibility = View.VISIBLE
                } else {
                    tvExpertBadge.visibility = View.GONE
                }

                // Load image thumbnail
                val imageFile = File(diagnosis.imagePath)
                if (imageFile.exists()) {
                    Glide.with(ivThumbnail.context)
                        .load(imageFile)
                        .centerCrop()
                        .placeholder(R.drawable.ic_placeholder_image)
                        .into(ivThumbnail)
                } else {
                    ivThumbnail.setImageResource(R.drawable.ic_placeholder_image)
                }

                // Set click listener
                root.setOnClickListener {
                    onItemClick(diagnosis)
                }
            }
        }
    }

    private class DiagnosisDiffCallback : DiffUtil.ItemCallback<Diagnosis>() {
        override fun areItemsTheSame(oldItem: Diagnosis, newItem: Diagnosis): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Diagnosis, newItem: Diagnosis): Boolean {
            return oldItem == newItem
        }
    }
}
