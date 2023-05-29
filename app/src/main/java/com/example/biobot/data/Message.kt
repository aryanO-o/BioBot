package com.example.biobot.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_table")
data class Message(
    @ColumnInfo(name = "message")
    val message: String,
    @ColumnInfo(name = "id")
    val id: String,
    @PrimaryKey
    val time: String)
