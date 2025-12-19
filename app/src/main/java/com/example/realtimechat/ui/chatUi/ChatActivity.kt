package com.example.realtimechat.ui.chatUi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realtimechat.databinding.ActivityChatBinding
import com.example.realtimechat.utils.NetworkState

/**
 * The main activity responsible for handling the chat user interface and interaction.
 *
 * Key responsibilities:
 * - Initializes the UI components via ViewBinding.
 * - Configures the message list adapter.
 * - Observes LiveData from the ViewModel to update the UI.
 * - Handles the "Send" button click event.
 * - Registers and unregisters network state callbacks during the Activity lifecycle.
 */
class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var vm: ChatViewModel
    private val adapter = ChatAdapter()
    private lateinit var networkState: NetworkState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm = ViewModelProvider(this)[ChatViewModel::class.java]

        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = adapter

        vm.messages.observe(this) { messages ->
            binding.tvEmptyStatus.isVisible = messages.isEmpty()
            adapter.submit(messages)
            binding.rvChat.scrollToPosition(messages.size - 1)
        }

        binding.btnSend.setOnClickListener {
            val msg = binding.etMessage.text.toString()
            if (msg.isNotBlank()) {
                vm.send(msg)
                binding.etMessage.setText("")
            }
        }

        vm.isOnline.observe(this) { isOnline ->
            if (!isOnline) binding.tvOffline.text = vm.wsResponse
            binding.tvOffline.isVisible = !isOnline
        }

        networkState = NetworkState(application, {
            vm.connectSocket()
        }, {
//            vm.isOnline.postValue(false)
        })

        networkState.register()

    }

    override fun onDestroy() {
        super.onDestroy()
        networkState.unregister()
    }
}