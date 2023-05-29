package com.example.biobot.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.android.marsphotos.network.MarsApi
import com.example.biobot.R
import com.example.biobot.data.Message
import com.example.biobot.databinding.FragmentChatBinding
import com.example.biobot.utils.Constants
import com.example.biobot.utils.Constants.RECEIVE_ID
import com.example.biobot.utils.Time
import kotlinx.coroutines.*


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: MessagingAdapter


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_chat, null, false
        )

        recyclerView()
        clickEvent()
        firstMessage("Hello I am BioBot, How can I help you today?")

        // Inflate the layout for this fragment
        return binding.root


    }

    private fun clickEvent() {
        binding.btnSend.setOnClickListener {
            sendMessage()
        }
        binding.etMessage.setOnClickListener{
            GlobalScope.launch {
                delay(1000)
                withContext(Dispatchers.Main){
                    binding.rvMessages.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }
    }

    private fun sendMessage() {
        val message = binding.etMessage.text.toString()
        val timeStamp: String = Time.timeStamp()

        if (message.isNotEmpty()) {
            binding.etMessage.setText("")

            adapter.insertMessage(Message(message, Constants.SEND_ID, timeStamp))
            binding.rvMessages.scrollToPosition(adapter.itemCount - 1)

            bioBotResponse(message)
        }
    }

    private fun bioBotResponse(message: String) {
        val response = "This is a response"
        coroutineScope.launch {
            try {
                val listResult = MarsApi.retrofitService.getResponse(message)
                val timeStamp: String = Time.timeStamp()
                val message = Message(listResult.message, RECEIVE_ID, timeStamp)
                adapter.insertMessage(message)
                binding.rvMessages.scrollToPosition(adapter.itemCount - 1)
            } catch (t: Throwable) {
                Toast.makeText(context, "Error: $t", Toast.LENGTH_LONG).show()
                Log.d("MainActivity","Error: $t")
            }
        }
    }

    private fun recyclerView(){
        adapter = MessagingAdapter()
        binding.rvMessages.adapter = adapter
        binding.rvMessages.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
    }

    private fun firstMessage(msg: String){
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main){
                val timeStamp: String = Time.timeStamp()
                val message = Message(msg, RECEIVE_ID, timeStamp)
                adapter.insertMessage(message)
                binding.rvMessages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    override fun onStart(){
        super.onStart()
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main){
                binding.rvMessages.scrollToPosition(adapter.itemCount - 1)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelJob.cancel()
    }


}