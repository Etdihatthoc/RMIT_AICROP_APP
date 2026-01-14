package com.example.ai_crop_doctor.presentation.ui.expert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ai_crop_doctor.databinding.FragmentExpertChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpertChatFragment : Fragment() {

    private var _binding: FragmentExpertChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpertChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupClickListeners()

        // Show empty state initially
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupClickListeners() {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text?.toString()
            if (!message.isNullOrBlank()) {
                sendMessage(message)
                binding.etMessage.text?.clear()
            }
        }
    }

    private fun sendMessage(message: String) {
        // TODO: Implement actual chat functionality with WebSocket or Firebase
        Toast.makeText(context, "Tin nhắn: $message", Toast.LENGTH_SHORT).show()
        binding.emptyState.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
