package com.example.realtimechat.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatMessage(
    @PrimaryKey val id: String,
    val text: String,
    val timestamp: Long,
    val status: MessageStatus
)

enum class MessageStatus {
    SENDING,
    SENT,
    RECEIVED,
    FAILED,
    QUEUED
}
