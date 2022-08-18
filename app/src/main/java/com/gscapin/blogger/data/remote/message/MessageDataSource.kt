package com.gscapin.blogger.data.remote.message

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.ContactMessage
import com.gscapin.blogger.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessageDataSource @Inject constructor() {

    suspend fun getContactMessages(): com.gscapin.blogger.core.Result<List<ContactMessage>> {
        val contactsMessagesList = mutableListOf<ContactMessage>()
        withContext(Dispatchers.IO) {
            val querySnapshot =
                FirebaseFirestore.getInstance().collection("contactMessages").get().await()

            for (contactMessage in querySnapshot.documents) {
                contactMessage.toObject(ContactMessage::class.java).let { contactFromFirebase ->

                    if (contactFromFirebase != null) {
                        contactsMessagesList.add(contactFromFirebase)
                    }
                }
            }
        }

        return com.gscapin.blogger.core.Result.Success(contactsMessagesList)
    }

    suspend fun getContactMessagesFromCurrentUser(): Result<List<ContactMessage>>{
        val currentUser = FirebaseAuth.getInstance().currentUser

        val userDb = FirebaseFirestore.getInstance().collection("users").document(currentUser!!.uid).get().await()

        userDb.toObject(User::class.java).let { userFirebase ->
            return Result.Success(userFirebase?.contacts!!)
        }
    }

    suspend fun sendUserMessage(id: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var userToMessaging =
            FirebaseFirestore.getInstance().collection("users").document(id).get().await()


        var userDb =
            FirebaseFirestore.getInstance().collection("users").document(currentUser!!.uid).get()
                .await()

        userToMessaging.toObject(User::class.java).let { userMessaging ->
            val contactMessage = ContactMessage(
                user = User(
                    username = userMessaging!!.username,
                    email = userMessaging.email,
                    userPhotoUrl = userMessaging.userPhotoUrl
                )
            )
            userDb.toObject(User::class.java).let { userFirebase ->


                val listContacts = userFirebase!!.contacts?.toMutableList()
                listContacts!!.add(contactMessage)
                userFirebase.apply {
                    contacts = listContacts
                }

                FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)
                    .set(userFirebase, SetOptions.merge()).await()

            }
        }

    }
}