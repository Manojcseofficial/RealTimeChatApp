package com.example.realtimechat.data.repository

import com.example.realtimechat.data.local.ChatDao
import com.example.realtimechat.data.local.ChatMessage
import com.example.realtimechat.data.local.MessageStatus
import com.example.realtimechat.data.socket.SocketHandler
import java.util.UUID

/**
 * Repository responsible for managing chat message data flow between the local database and the WebSocket connection.
 *
 * This class handles:
 * - Initialization and management of the WebSocket connection via [SocketHandler].
 * - Persisting chat messages locally using [ChatDao].
 * - Sending messages to the server and updating their local status (Queued, Sending, Sent, Failed).
 * - Retrying queued messages when the connection is re-established.
 *
 * @property dao The Data Access Object for performing local database operations on chat messages.
 */
class ChatRepository(
    private val dao: ChatDao
) {

    private lateinit var socketManager: SocketHandler

    fun initSocket(
        onMessageReceived: (String) -> Unit,
        onConnectionChanged: (Boolean, String) -> Unit
    ) {
        socketManager = SocketHandler(onMessageReceived, onConnectionChanged)
    }

    /*========USE ONY FOR LOCAL DB===========*/
    val messages = dao.getMessages()
    suspend fun sendMessage(text: String, isOnline: Boolean) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            timestamp = System.currentTimeMillis(),
            status = if (isOnline) MessageStatus.SENDING else MessageStatus.QUEUED
        )

        dao.insert(message)

        if (isOnline) {
            val sent = send(text)
            dao.updateStatus(
                message.id,
                if (sent) MessageStatus.SENT else MessageStatus.FAILED
            )
        }
    }

    suspend fun retryQueued(isOnline: Boolean) {
        if (!isOnline) return

        dao.getQueued().forEach {
            socketManager.send(it.text)
            dao.updateStatus(it.id, MessageStatus.SENT)
        }
    }
    /*========END===========*/

    fun send(text: String): Boolean {
        return socketManager.send(text)
    }

    fun connectSocket() {
        socketManager.connect()
    }

    fun disconnect() {
        socketManager.disconnect()
    }
}
