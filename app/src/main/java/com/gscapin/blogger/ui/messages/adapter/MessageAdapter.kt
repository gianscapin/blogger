package com.gscapin.blogger.ui.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gscapin.blogger.core.BaseViewHolder
import com.gscapin.blogger.data.model.ContactMessage
import com.gscapin.blogger.databinding.ContactItemBinding

class MessageAdapter(
    private val messageList: List<ContactMessage>,
    private val onContactClickListener: OnContactClickListener
): RecyclerView.Adapter<BaseViewHolder<*>>(){
    private var contactClickListener: OnContactClickListener? = null

    init {
        contactClickListener = onContactClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(itemBinding, parent.context)
    }

    private inner class ContactViewHolder(val itemBinding: ContactItemBinding, val context: Context) : BaseViewHolder<ContactMessage>(itemBinding.root){
        override fun bind(item: ContactMessage) {
            setContactInfo(item)
            clickContactAction(item)
        }

        private fun clickContactAction(item: ContactMessage) {
            itemBinding.message.setOnClickListener {
                contactClickListener?.onContactClickListener(item)
            }
        }

        private fun setContactInfo(item: ContactMessage) {
            Glide.with(context).load(item.user?.userPhotoUrl).centerCrop().into(itemBinding.contactImage)
            itemBinding.nameContact.text = item.user?.username.toString()
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is ContactViewHolder -> holder.bind(messageList[position])
        }
    }

    override fun getItemCount(): Int = messageList.size

}

interface OnContactClickListener {
    fun onContactClickListener(contactMessage: ContactMessage)
}
