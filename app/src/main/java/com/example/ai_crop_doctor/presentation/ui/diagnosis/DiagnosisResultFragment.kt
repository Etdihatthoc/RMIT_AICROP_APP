package com.example.ai_crop_doctor.presentation.ui.diagnosis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ai_crop_doctor.R
import com.example.ai_crop_doctor.databinding.FragmentDiagnosisResultBinding
import com.example.ai_crop_doctor.domain.model.Diagnosis
import com.example.ai_crop_doctor.util.loadImage
import com.example.ai_crop_doctor.util.toPercentage
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class DiagnosisResultFragment : Fragment() {

    private var _binding: FragmentDiagnosisResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiagnosisResultViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiagnosisResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()

        // Get arguments (image file path passed from CameraFragment)
        arguments?.let { args ->
            val imageFilePath = args.getString(ARG_IMAGE_PATH)
            imageFilePath?.let {
                val imageFile = File(it)
                if (imageFile.exists()) {
                    displayImage(imageFile)
                    // Trigger diagnosis
                    viewModel.submitDiagnosis(imageFile)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnRequestExpert.setOnClickListener {
            requestExpertReview()
        }

        binding.btnSave.setOnClickListener {
            saveDiagnosis()
        }
    }

    private fun observeViewModel() {
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe diagnosis result
        viewModel.diagnosisResult.observe(viewLifecycleOwner) { diagnosis ->
            diagnosis?.let {
                displayDiagnosisResult(it)
            }
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        // Observe save success
        viewModel.savingSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    R.string.success_diagnosis_saved,
                    Toast.LENGTH_SHORT
                ).show()
                // Navigate back to home
                findNavController().navigate(R.id.action_diagnosisResult_to_home)
            }
        }
    }

    private fun displayImage(imageFile: File) {
        Glide.with(this)
            .load(imageFile)
            .centerCrop()
            .into(binding.ivCapturedImage)
    }

    private fun displayDiagnosisResult(diagnosis: Diagnosis) {
        Timber.d("Displaying diagnosis result: ${diagnosis.diseaseDetected}")

        // Display disease name
        binding.tvDiseaseName.text = diagnosis.diseaseDetected

        // Display confidence
        binding.tvConfidencePercentage.text = diagnosis.confidence.toPercentage()
        binding.tvConfidenceLevel.text = diagnosis.getConfidenceLevelVietnamese()

        // Update confidence badge color
        val confidenceBgColor = when {
            diagnosis.confidence >= 0.80 -> R.color.confidence_high
            diagnosis.confidence >= 0.50 -> R.color.confidence_medium
            else -> R.color.confidence_low
        }
        binding.tvConfidenceLevel.setBackgroundResource(
            when {
                diagnosis.confidence >= 0.80 -> R.drawable.bg_confidence_high
                diagnosis.confidence >= 0.50 -> R.drawable.bg_confidence_medium
                else -> R.drawable.bg_confidence_low
            }
        )

        // Display severity
        binding.tvSeverity.text = diagnosis.getSeverityDisplay()

        // Display location if available
        val locationDisplay = diagnosis.getLocationDisplay()
        if (locationDisplay.isNotBlank()) {
            binding.locationCard.visibility = View.VISIBLE
            binding.tvLocation.text = locationDisplay
        } else {
            binding.locationCard.visibility = View.GONE
        }

        // Display treatment suggestions
        binding.tvTreatment.text = diagnosis.fullResponse

        // Show expert review banner if needed
        if (diagnosis.needsExpertReview()) {
            binding.expertReviewBanner.visibility = View.VISIBLE
        } else {
            binding.expertReviewBanner.visibility = View.GONE
        }
    }

    private fun requestExpertReview() {
        val diagnosis = viewModel.diagnosisResult.value ?: return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Yêu cầu chuyên gia")
            .setMessage("Bạn có muốn gửi chẩn đoán này đến chuyên gia để được tư vấn thêm không?")
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.requestExpertReview(diagnosis.id)
                Toast.makeText(
                    requireContext(),
                    R.string.success_expert_requested,
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun saveDiagnosis() {
        val diagnosis = viewModel.diagnosisResult.value ?: return
        viewModel.saveDiagnosis(diagnosis)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_IMAGE_PATH = "image_path"

        fun newBundle(imageFilePath: String): Bundle {
            return Bundle().apply {
                putString(ARG_IMAGE_PATH, imageFilePath)
            }
        }
    }
}
