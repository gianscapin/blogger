package com.gscapin.blogger.data.remote.message

import com.google.firebase.firestore.FirebaseFirestore
import com.gscapin.blogger.data.model.ContactMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessageDataSource @Inject constructor() {

    suspend fun getContactMessages(): com.gscapin.blogger.core.Result<List<ContactMessage>>{
        val contactsMessagesList = mutableListOf<ContactMessage>()
        withContext(Dispatchers.IO){
            val querySnapshot = FirebaseFirestore.getInstance().collection("contactMessages").get().await()

            for(contactMessage in querySnapshot.documents){
                contactMessage.toObject(ContactMessage::class.java).let { contactFromFirebase ->

                    if (contactFromFirebase != null) {
                        contactsMessagesList.add(contactFromFirebase)
                    }
                }
            }
        }

        return com.gscapin.blogger.core.Result.Success(contactsMessagesList)
    }
}