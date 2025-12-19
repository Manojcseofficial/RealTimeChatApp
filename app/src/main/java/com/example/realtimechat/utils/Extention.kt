package com.example.realtimechat.utils

import com.example.realtimechat.data.local.ChatMessage
import com.example.realtimechat.data.local.MessageStatus
import java.util.UUID

/**
 * Extension function on [String] to create a [ChatMessage] object.
 *
 * @param msgStatus The status to assign to the new message (e.g., Sent, Received).
 * @return A new [ChatMessage] instance containing the text, generated ID, timestamp, and status.
 */
fun String.prepareData(msgStatus: MessageStatus): ChatMessage {
    return ChatMessage(
        id = UUID.randomUUID().toString(),
        text = this,
        timestamp = System.currentTimeMillis(),
        status = msgStatus
    )
}