package com.example.biobot.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.biobot.database.MessageDao

class ChatFragmentViewModelFactory(
    private val dao: MessageDao
): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatFragmentViewModel::class.java)) {
            return ChatFragmentViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}