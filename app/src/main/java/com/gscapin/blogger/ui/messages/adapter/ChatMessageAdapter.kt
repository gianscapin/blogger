package com.gscapin.blogger.ui.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.gscapin.blogger.core.BaseViewHolder
import com.gscapin.blogger.core.show
import com.gscapin.blogger.data.model.Message
import com.gscapin.blogger.databinding.MessagesBinding

class ChatMessageAdapter(
    private val messageFromChat: List<Message>
): RecyclerView.Adapter<BaseViewHolder<*>>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = MessagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(itemBinding, parent.context)
    }

    private inner class MessageViewHolder(val itemBinding: MessagesBinding, val context: Context): BaseViewHolder<Message>(itemBinding.root) {
        override fun bind(item: Message) {
            setMessageInfo(item)
        }

        private fun setMessageInfo(item: Message) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if(item.idUser == currentUser!!.uid){
                itemBinding.messageCurrentUser.text = item.text
                itemBinding.messageCurrentUser.show()
            }else{
                itemBinding.messageUser.text = item.text
                itemBinding.messageUser.show()
            }
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is MessageViewHolder -> holder.bind(messageFromChat[position])
        }
    }

    override fun getItemCount(): Int = messageFromChat.size

}