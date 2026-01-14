package com.example.ai_crop_doctor.presentation.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for HomeFragment
 *
 * Handles home screen state and navigation logic.
 * Currently minimal as home screen is mainly for navigation.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    // Will add repositories here in later phases when we need to display recent diagnoses
) : ViewModel() {

    init {
        Timber.d("HomeViewModel initialized")
    }

    /**
     * Handle camera button click
     * Navigation will be handled in the Fragment
     */
    fun onCameraClicked() {
        Timber.d("Camera button clicked")
        // Navigation logic will be in Fragment using NavController
    }

    /**
     * Handle map button click
     */
    fun onMapClicked() {
        Timber.d("Map button clicked")
    }

    /**
     * Handle history button clicked
     */
    fun onHistoryClicked() {
        Timber.d("History button clicked")
    }
}
