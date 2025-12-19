package com.example.realtimechat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Query("SELECT * FROM ChatMessage ORDER BY timestamp ASC")
    fun getMessages(): Flow<List<ChatMessage>>

    @Insert
    suspend fun insert(message: ChatMessage)

    @Query("UPDATE ChatMessage SET status=:status WHERE id=:id")
    suspend fun updateStatus(id: String, status: MessageStatus)

    @Query("SELECT * FROM ChatMessage WHERE status='QUEUED'")
    suspend fun getQueued(): List<ChatMessage>

    @Query("DELETE FROM ChatMessage")
    suspend fun clearAll()
}

