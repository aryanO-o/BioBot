package com.example.biobot.ui

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.room.Dao
import com.example.biobot.data.Message
import com.example.biobot.data.MessageEntity
import com.example.biobot.database.MessageDao
import com.example.biobot.utils.Constants
import com.example.biobot.utils.Time
import kotlinx.coroutines.*
import java.util.*
import kotlin.random.Random

class ChatFragmentViewModel(private val dao: MessageDao) : ViewModel() {
    private val viewModelJob = Job()
    val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun addMessages(owner: LifecycleOwner, adapter: MessagingAdapter): Int {
        var cnt = 0
        dao.getAllMessages().observe(owner, { messages ->
            messages?.let {
                adapter.clearMessages()
                uiScope.launch {
                    withContext(Dispatchers.IO) {
                        for (messageEntity in it) {
                            val message = Message(
                                messageEntity.id,
                                messageEntity.message,
                                messageEntity.time
                            )
                            withContext(Dispatchers.Main) {
                                adapter.insertMessage(message)
                                cnt++
                            }
                        }
                    }
                }
            }
        })
        return cnt
    }





    fun saveMessage(adapter: MessagingAdapter) {
        val entityList = adapter.messagesList.toList() // Create a copy of the list
        uiScope.launch {
            withContext(Dispatchers.IO) {
                for (entity in entityList) {
                    val time: String = entity.time + "-" + UUID.randomUUID().toString()
                    dao.insert(MessageEntity(entity.id, entity.message, time))
                }
            }
        }
    }

    fun deleteMessages() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                dao.clear()
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
