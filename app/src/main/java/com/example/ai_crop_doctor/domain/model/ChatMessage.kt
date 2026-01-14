package com.example.ai_crop_doctor.domain.model

import java.util.Date

/**
 * Domain model for Chat messages with experts
 */
data class ChatMessage(
    val id: Int,
    val diagnosisId: Int?,
    val senderId: String,
    val senderName: String,
    val senderType: SenderType, // farmer or expert
    val message: String,
    val timestamp: Date,
    val isRead: Boolean = false
) {
    enum class SenderType {
        FARMER,
        EXPERT
    }

    /**
     * Check if message is from current user (farmer)
     */
    fun isFromCurrentUser(): Boolean {
        return senderType == SenderType.FARMER
    }
}
