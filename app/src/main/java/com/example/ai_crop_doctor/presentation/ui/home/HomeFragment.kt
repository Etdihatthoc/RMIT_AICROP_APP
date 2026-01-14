package com.example.ai_crop_doctor.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ai_crop_doctor.R
import com.example.ai_crop_doctor.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Home screen fragment
 *
 * Main landing page with options to:
 * - Take photo for diagnosis (primary action)
 * - View epidemic map
 * - View diagnosis history
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("HomeFragment view created")

        setupClickListeners()
    }

    /**
     * Setup click listeners for all action buttons
     */
    private fun setupClickListeners() {
        // Camera FAB - Navigate to camera screen
        binding.fabCamera.setOnClickListener {
            viewModel.onCameraClicked()
            findNavController().navigate(R.id.action_home_to_camera)
        }

        // Map card - Navigate to epidemic map
        binding.cardMap.setOnClickListener {
            viewModel.onMapClicked()
            findNavController().navigate(R.id.action_home_to_map)
        }

        // History card - Navigate to history screen
        binding.cardHistory.setOnClickListener {
            viewModel.onHistoryClicked()
            findNavController().navigate(R.id.action_home_to_history)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
