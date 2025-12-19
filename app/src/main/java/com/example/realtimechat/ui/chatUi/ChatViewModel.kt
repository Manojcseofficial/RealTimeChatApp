package com.example.realtimechat.ui.chatUi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.realtimechat.data.local.AppDatabase
import com.example.realtimechat.data.local.ChatMessage
import com.example.realtimechat.data.local.MessageStatus
import com.example.realtimechat.data.repository.ChatRepository
import com.example.realtimechat.utils.prepareData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing UI-related data for the chat screen and handling real-time communication.
 *
 * @param application The application context, used to instantiate the database and repository.
 * @see ChatRepository
 * @see ChatMessage
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.get(application).chatDao()
    private val repo = ChatRepository(dao)

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages
    private val messageList = mutableListOf<ChatMessage>()
    private var queuedMessages = mutableListOf<ChatMessage>()


    //    val messages = repo.messages.asLiveData()
    val isOnline = MutableLiveData(true)
    var wsResponse = ""

    init {
        repo.initSocket(
            onMessageReceived = { text ->
                val chatMessage = text.prepareData(MessageStatus.RECEIVED)
                //use for local db
                /*viewModelScope.launch {
                    dao.insert(chatMessage)
                }*/
                messageList.add(chatMessage)
                _messages.postValue(messageList)
            },
            onConnectionChanged = { wsStatus, response ->
                wsResponse = response
                isOnline.postValue(wsStatus)
                if (wsStatus) retryQueued()
            }
        )
    }

    fun connectSocket() {
        repo.connectSocket()
    }

    fun send(text: String) = viewModelScope.launch {
//        repo.sendMessage(text, isOnline.value == true)
        val isSent = repo.send(text)
        val chatMessage = text.prepareData(if (isSent) MessageStatus.SENT else MessageStatus.QUEUED)
        messageList.add(chatMessage)
        _messages.postValue(messageList)
    }

    fun retryQueued() = viewModelScope.launch {
        /*repo.retryQueued(isOnline.value == true) // Retry only if online and has queued items in room db*/

        // One-line check: Get list ONLY if it has queued items, otherwise exit
        val currentList = _messages.value?.takeIf { it.any { msg -> msg.status == MessageStatus.QUEUED } } ?: return@launch

        // Process messages in parallel to speed up the retry queue
        val updatedList = currentList.map { message ->
            if (message.status == MessageStatus.QUEUED) {
                async {
                    try {
                        val isSent = repo.send(message.text)
                        message.copy(status = if (isSent) MessageStatus.SENT else MessageStatus.FAILED)
                    } catch (e: Exception) {
                        // Handle network crashes gracefully so one failure doesn't stop the whole list
                        message.copy(status = MessageStatus.FAILED)
                    }
                }
            } else {
                async { message }
            }
        }.awaitAll() // Wait for all parallel requests to complete

        // Update local mutable list with the new status
        messageList.clear()
        messageList.addAll(updatedList)

        // the new list, DiffUtil Adapter will AUTOMATICALLY updates only changed rows.
        _messages.value = ArrayList(messageList)
    }


    override fun onCleared() {
        super.onCleared()
        repo.disconnect()
    }
}
