package com.example.ai_crop_doctor.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

/**
 * Kotlin extension functions for common operations
 */

// ==================== View Extensions ====================

/**
 * Makes view visible
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * Makes view invisible (takes up space but not shown)
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Makes view gone (doesn't take up space)
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * Toggle visibility between VISIBLE and GONE
 */
fun View.toggleVisibility() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

// ==================== Toast Extensions ====================

/**
 * Show a short toast message
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Show a long toast message
 */
fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Show a short toast message from Fragment
 */
fun Fragment.showToast(message: String) {
    requireContext().showToast(message)
}

/**
 * Show a long toast message from Fragment
 */
fun Fragment.showLongToast(message: String) {
    requireContext().showLongToast(message)
}

// ==================== Date Extensions ====================

/**
 * Format Date to Vietnamese date string
 * Example: "13/01/2024 10:30"
 */
fun Date.toVietnameseDateTime(): String {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN"))
    return format.format(this)
}

/**
 * Format Date to Vietnamese date string (date only)
 * Example: "13/01/2024"
 */
fun Date.toVietnameseDate(): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
    return format.format(this)
}

/**
 * Format Date to time string
 * Example: "10:30"
 */
fun Date.toTimeString(): String {
    val format = SimpleDateFormat("HH:mm", Locale("vi", "VN"))
    return format.format(this)
}

/**
 * Convert ISO 8601 string to Date
 * Example: "2024-01-13T10:30:00" → Date
 */
fun String.toDate(): Date? {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        format.parse(this)
    } catch (e: Exception) {
        null
    }
}

// ==================== String Extensions ====================

/**
 * Check if string is a valid email
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Capitalize first letter of each word
 */
fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }
}

/**
 * Truncate string to max length with ellipsis
 */
fun String.truncate(maxLength: Int): String {
    return if (length <= maxLength) this else "${take(maxLength)}..."
}

// ==================== Double Extensions ====================

/**
 * Format confidence score to percentage
 * Example: 0.8567 → "86%"
 */
fun Double.toPercentage(): String {
    return "${(this * 100).toInt()}%"
}

/**
 * Format confidence score with one decimal place
 * Example: 0.8567 → "85.7%"
 */
fun Double.toPercentageDetailed(): String {
    return String.format("%.1f%%", this * 100)
}

/**
 * Round to 2 decimal places
 */
fun Double.roundTo2Decimals(): Double {
    return String.format("%.2f", this).toDouble()
}

/**
 * Format distance in kilometers with unit
 * Example: 2.345 → "2.3 km"
 */
fun Double.toDistanceString(): String {
    return String.format("%.1f km", this)
}

// ==================== Float Extensions ====================

/**
 * Convert float confidence to percentage string
 */
fun Float.toPercentage(): String {
    return "${(this * 100).toInt()}%"
}

// ==================== Confidence Level Helpers ====================

/**
 * Get confidence level text in Vietnamese
 * Returns: "CAO", "TRUNG BÌNH", "THẤP"
 */
fun Double.getConfidenceLevelVietnamese(): String {
    return when {
        this >= Constants.CONFIDENCE_HIGH_THRESHOLD -> "CAO"
        this >= Constants.CONFIDENCE_LOW_THRESHOLD -> "TRUNG BÌNH"
        else -> "THẤP"
    }
}

/**
 * Get confidence color resource ID based on value
 * Returns: R.color.confidence_high, R.color.confidence_medium, or R.color.confidence_low
 */
fun Double.getConfidenceColorRes(): Int {
    return when {
        this >= Constants.CONFIDENCE_HIGH_THRESHOLD -> android.R.color.holo_green_dark
        this >= Constants.CONFIDENCE_LOW_THRESHOLD -> android.R.color.holo_orange_dark
        else -> android.R.color.holo_red_dark
    }
}
