package com.example.ai_crop_doctor.presentation.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ai_crop_doctor.R
import com.example.ai_crop_doctor.databinding.FragmentCameraBinding
import com.example.ai_crop_doctor.util.FileHelper
import com.example.ai_crop_doctor.util.PermissionHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CameraViewModel by viewModels()

    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var flashMode = ImageCapture.FLASH_MODE_OFF

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    // Permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            startCamera()
        } else {
            handlePermissionDenied()
        }
    }

    // Gallery picker launcher
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleImageFromGallery(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        setupClickListeners()
        observeViewModel()

        // Check permissions and start camera
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCapture.setOnClickListener {
            captureImage()
        }

        binding.btnFlash.setOnClickListener {
            toggleFlash()
        }

        binding.btnFlipCamera.setOnClickListener {
            flipCamera()
        }

        binding.btnGallery.setOnClickListener {
            openGallery()
        }
    }

    private fun observeViewModel() {
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnCapture.isEnabled = !isLoading
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        val requiredPermissions = PermissionHelper.getDiagnosisFeaturePermissions()
        return PermissionHelper.arePermissionsGranted(requireContext(), requiredPermissions)
    }

    private fun requestPermissions() {
        val requiredPermissions = PermissionHelper.getDiagnosisFeaturePermissions()
        requestPermissionLauncher.launch(requiredPermissions)
    }

    private fun handlePermissionDenied() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.permissions_required)
            .setMessage(R.string.camera_permission_rationale)
            .setPositiveButton(R.string.ok) { _, _ ->
                requestPermissions()
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }

    private fun startCamera() {
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            } catch (e: Exception) {
                Timber.e(e, "Camera initialization failed")
                Toast.makeText(
                    requireContext(),
                    R.string.error_camera_init,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: return

        // Preview use case
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

        // ImageCapture use case
        imageCapture = ImageCapture.Builder()
            .setFlashMode(flashMode)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()

        // Camera selector
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            // Unbind all use cases before rebinding
            cameraProvider.unbindAll()

            // Bind use cases to camera
            camera = cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

        } catch (e: Exception) {
            Timber.e(e, "Use case binding failed")
            Toast.makeText(
                requireContext(),
                R.string.error_camera_init,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        // Create output file
        val photoFile = try {
            FileHelper.createImageFile(requireContext())
        } catch (e: Exception) {
            Timber.e(e, "Failed to create image file")
            Toast.makeText(requireContext(), R.string.error_image_save, Toast.LENGTH_SHORT).show()
            return
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Show loading
        viewModel.setLoading(true)

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Timber.d("Image saved successfully: ${photoFile.absolutePath}")
                    viewModel.setLoading(false)
                    handleCapturedImage(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    Timber.e(exception, "Image capture failed")
                    viewModel.setLoading(false)
                    Toast.makeText(
                        requireContext(),
                        R.string.error_image_capture,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun handleCapturedImage(imageFile: File) {
        // TODO: Navigate to diagnosis result screen with image file
        Timber.d("Image captured: ${imageFile.absolutePath}")
        Toast.makeText(
            requireContext(),
            R.string.success_image_saved,
            Toast.LENGTH_SHORT
        ).show()

        // For now, just notify viewModel
        viewModel.onImageCaptured(imageFile)
    }

    private fun handleImageFromGallery(uri: Uri) {
        try {
            // Copy URI to app's private storage
            val imageFile = FileHelper.createImageFile(requireContext())
            val success = FileHelper.copyUriToFile(requireContext(), uri, imageFile)

            if (success) {
                handleCapturedImage(imageFile)
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.error_image_save,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to handle gallery image")
            Toast.makeText(
                requireContext(),
                R.string.error_image_save,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun toggleFlash() {
        flashMode = when (flashMode) {
            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
            else -> ImageCapture.FLASH_MODE_OFF
        }

        // Update icon
        val iconRes = when (flashMode) {
            ImageCapture.FLASH_MODE_ON -> R.drawable.ic_flash_on
            ImageCapture.FLASH_MODE_AUTO -> R.drawable.ic_flash_on
            else -> R.drawable.ic_flash_off
        }
        binding.btnFlash.setImageResource(iconRes)

        // Update image capture flash mode
        imageCapture?.flashMode = flashMode

        Timber.d("Flash mode changed to: $flashMode")
    }

    private fun flipCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }

        // Restart camera with new lens
        bindCameraUseCases()
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}
