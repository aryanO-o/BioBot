package com.example.biobot.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.biobot.data.MessageEntity

@Dao
interface MessageDao {
    @Insert
    fun insert(message: MessageEntity)

    @Query("SELECT * FROM message_entity ORDER BY time ASC")
    fun getAllMessages(): LiveData<List<MessageEntity>>

    @Query("DELETE FROM message_entity")
    fun clear()
}
