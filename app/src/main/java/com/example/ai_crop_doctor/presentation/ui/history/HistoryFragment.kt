package com.example.ai_crop_doctor.presentation.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ai_crop_doctor.R
import com.example.ai_crop_doctor.databinding.FragmentHistoryBinding
import com.example.ai_crop_doctor.presentation.ui.diagnosis.DiagnosisResultFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: DiagnosisAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchAndFilters()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = DiagnosisAdapter { diagnosis ->
            // Navigate to diagnosis detail
            val bundle = DiagnosisResultFragment.newBundle(diagnosis.imagePath).apply {
                // You can also pass diagnosis ID to load full details
                putInt("diagnosis_id", diagnosis.id)
            }
            findNavController().navigate(
                R.id.action_history_to_diagnosisDetail,
                bundle
            )
        }

        binding.rvDiagnoses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchAndFilters() {
        // Search functionality
        binding.etSearch.doAfterTextChanged { text ->
            val query = text?.toString() ?: ""
            viewModel.searchDiagnoses(query)
        }

        // Expert reviewed filter
        binding.chipExpertReviewed.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.filterByExpertReviewed(true)
            } else {
                viewModel.clearFilters()
            }
        }

        // High confidence filter
        binding.chipHighConfidence.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.filterByConfidence(0.80)
            } else {
                viewModel.clearFilters()
            }
        }

        // Clear filters
        binding.chipClearFilters.setOnClickListener {
            binding.chipExpertReviewed.isChecked = false
            binding.chipHighConfidence.isChecked = false
            binding.etSearch.text?.clear()
            viewModel.clearFilters()
        }
    }

    private fun observeViewModel() {
        // Observe diagnoses list
        viewModel.diagnoses.observe(viewLifecycleOwner) { diagnoses ->
            adapter.submitList(diagnoses)
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe empty state
        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.rvDiagnoses.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
