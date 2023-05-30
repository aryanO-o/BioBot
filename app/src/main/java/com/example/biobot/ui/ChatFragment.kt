package com.example.biobot.ui

import android.app.AlertDialog
import android.os.Build.VERSION_CODES.R
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.android.marsphotos.network.MarsApi
import com.example.biobot.R
import com.example.biobot.data.Message
import com.example.biobot.database.MessageDatabase
import com.example.biobot.databinding.FragmentChatBinding
import com.example.biobot.utils.Constants
import com.example.biobot.utils.Constants.RECEIVE_ID
import com.example.biobot.utils.Time
import kotlinx.coroutines.*
import java.util.*
import kotlin.random.Random


class ChatFragment : Fragment(), LifecycleOwner {

    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: MessagingAdapter


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, com.example.biobot.R.layout.fragment_chat, null, false
        )

        val dataSource = MessageDatabase.getInstance(requireContext()).messageDatabaseDao
        val viewModelFactory = ChatFragmentViewModelFactory(dataSource!!)

        val messageViewModel = ViewModelProvider(this, viewModelFactory).get(ChatFragmentViewModel::class.java)
        binding.lifecycleOwner = this  // Add this line
        binding.viewModel = messageViewModel

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
        val timeStamp: String = Time.timeStamp() + "-" + UUID.randomUUID().toString()

        if (message.isNotEmpty()) {
            binding.etMessage.setText("")

            adapter.insertMessage(Message(message, Constants.SEND_ID, timeStamp))
            binding.rvMessages.scrollToPosition(adapter.itemCount - 1)

//            binding.viewModel!!.saveMessage(adapter)
//            binding.viewModel!!.addMessages(this, adapter)
            bioBotResponse(message)
        }
    }

    private fun bioBotResponse(message: String) {
        val response = "This is a response"
        coroutineScope.launch {
            try {
                val listResult = MarsApi.retrofitService.getResponse(message)
                val timeStamp: String = Time.timeStamp() + "-" + UUID.randomUUID().toString()
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
        binding.viewModel!!.addMessages(this, adapter)
    }

    private fun firstMessage(msg: String){
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main){
                val timeStamp: String = Time.timeStamp()  + "-" + UUID.randomUUID().toString()
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

    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Save Conversation")
        builder.setMessage("Do you want to save the conversation?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            binding.viewModel?.saveMessage(adapter)
            dialog.dismiss()
            requireActivity().finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            binding.viewModel?.deleteMessages()
            requireActivity().finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
                showSaveDialog()

        }
    }



    override fun onDestroy() {
        super.onDestroy()
        binding.viewModel?.saveMessage(adapter)
        viewModelJob.cancel()
    }


}