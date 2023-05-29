package com.example.biobot.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.biobot.data.Message
import com.example.biobot.databinding.MessageItemBinding
import com.example.biobot.utils.Constants.RECEIVE_ID
import com.example.biobot.utils.Constants.SEND_ID

class MessagingAdapter: RecyclerView.Adapter<MessagingAdapter.MessageViewHolder>() {

    var messagesList = mutableListOf<Message>()

    inner class MessageViewHolder(private var binding: MessageItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {

                //Remove message on the item clicked
                messagesList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }

        fun hide(message: Message) {
            when (message.id) {
                SEND_ID -> {
                    binding.tvMessage.apply {
                        text = message.message
                        visibility = View.VISIBLE
                    }
                    binding.tvBotMessage.visibility = View.GONE
                }
                RECEIVE_ID -> {
                    binding.tvBotMessage.apply {
                        text = message.message
                        visibility = View.VISIBLE
                    }
                    binding.tvMessage.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(MessageItemBinding.inflate(LayoutInflater.from(parent.context)));
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messagesList[position]
        holder.hide(currentMessage)
    }

    fun insertMessage(message: Message) {
        this.messagesList.add(message)
        notifyItemInserted(messagesList.size)
    }

}