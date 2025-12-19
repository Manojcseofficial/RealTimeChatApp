package com.example.realtimechat.ui.chatUi

import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.realtimechat.data.local.ChatMessage
import com.example.realtimechat.data.local.MessageStatus

class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.ChatVH>(ChatDiffCallback()) {

    private val items = mutableListOf<ChatMessage>()

    fun submit(list: List<ChatMessage>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatVH {
        val tv = TextView(parent.context)
        tv.setPadding(8, 8, 8, 8)
        return ChatVH(tv)
    }

    override fun onBindViewHolder(holder: ChatVH, position: Int) {
        val msg = items[position]
        "${msg.text} (${msg.status})".also { holder.tv.text = it }
        holder.tv.setTextColor(
            when (msg.status) {
                MessageStatus.FAILED -> Color.RED
                MessageStatus.QUEUED -> Color.BLUE
                else -> Color.WHITE
            }
        )
    }

    override fun getItemCount() = items.size

    // This class does the magic of updating only changed items
    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }

    class ChatVH(val tv: TextView) : RecyclerView.ViewHolder(tv)
}

