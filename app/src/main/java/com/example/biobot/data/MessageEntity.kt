package com.example.biobot.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_entity")
data class MessageEntity(
    val message: String,
    val id: String,
    @PrimaryKey(autoGenerate = false)
    val time: String
)
