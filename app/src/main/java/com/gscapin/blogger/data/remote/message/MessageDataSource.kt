package com.gscapin.blogger.data.remote.message

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.Chat
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

    suspend fun getContactMessagesFromCurrentUser(): Result<List<ContactMessage>> {
        val currentUser = FirebaseAuth.getInstance().currentUser

        val userDb =
            FirebaseFirestore.getInstance().collection("users").document(currentUser!!.uid).get()
                .await()

        userDb.toObject(User::class.java).let { userFirebase ->
            if (userFirebase?.contacts == null) {
                val listEmpty: List<ContactMessage> = emptyList()
                return Result.Success(listEmpty)
            } else {
                val contacts: List<ContactMessage> = userFirebase.contacts!!
                return Result.Success(contacts!!)
            }
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
            userDb.toObject(User::class.java).let { userFirebase ->

                val existChat = isExistChat(userFirebase!!, userMessaging!!)
                if (!existChat) {
                    val idChat =
                        createChatToDb(userFirebase!!, userMessaging!!, currentUser.uid, id)

                    val contactMessage = ContactMessage(
                        user = listOf(
                            User(
                                username = userMessaging.username,
                                email = userMessaging.email,
                                userPhotoUrl = userMessaging.userPhotoUrl
                            ),
                            User(
                                username = userFirebase.username,
                                email = userFirebase.email,
                                userPhotoUrl = userFirebase.userPhotoUrl
                            )
                        ),
                        idMessage = idChat
                    )

                    if (userFirebase?.contacts != null) {
                        val listContacts = userFirebase.contacts?.toMutableList()
                        listContacts!!.add(contactMessage)
                        userFirebase.apply {
                            contacts = listContacts
                        }

                        FirebaseFirestore.getInstance().collection("users")
                            .document(currentUser.uid)
                            .set(userFirebase, SetOptions.merge()).await()
                    } else {
                        val listContacts: MutableList<ContactMessage> =
                            mutableListOf<ContactMessage>()
                        listContacts.add(contactMessage)
                        userFirebase.apply {
                            contacts = listContacts
                        }

                        FirebaseFirestore.getInstance().collection("users")
                            .document(currentUser.uid)
                            .set(userFirebase, SetOptions.merge()).await()
                    }

                    if (userMessaging.contacts != null) {
                        val listContacts = userMessaging.contacts?.toMutableList()
                        listContacts!!.add(contactMessage)
                        userMessaging.apply {
                            contacts = listContacts
                        }

                        FirebaseFirestore.getInstance().collection("users").document(id)
                            .set(userMessaging, SetOptions.merge()).await()
                    } else {
                        val listContacts: MutableList<ContactMessage> =
                            mutableListOf<ContactMessage>()
                        listContacts.add(contactMessage)
                        userMessaging.apply {
                            contacts = listContacts
                        }

                        FirebaseFirestore.getInstance().collection("users").document(id)
                            .set(userMessaging, SetOptions.merge()).await()
                    }
                }


            }
        }

    }

    private fun isExistChat(userFirebase: User, userMessaging: User): Boolean {
        if (userFirebase.contacts != null && userMessaging.contacts != null) {
            val contactsCurrentUser = userFirebase.contacts
            val contactOtherUser = userMessaging.contacts

            for (contact in contactsCurrentUser!!) {
                val idChat = contact.idMessage
                for (contactOther in contactOtherUser!!) {
                    if (contactOther.idMessage == idChat) {
                        return true
                    }
                }
            }
            return false
        } else {
            return false
        }


    }

    private suspend fun createChatToDb(
        userFirebase: User,
        userMessaging: User,
        currentUserId: String,
        id: String
    ): String {
        return FirebaseFirestore.getInstance().collection("chat").add(
            Chat(
                text = emptyList(),
                user = listOf(
                    User(
                        username = userFirebase.username,
                        email = userFirebase.email,
                        userPhotoUrl = userFirebase.userPhotoUrl,
                        contacts = userFirebase.contacts,
                        id = currentUserId
                    ),
                    User(
                        username = userMessaging.username,
                        email = userMessaging.email,
                        userPhotoUrl = userMessaging.userPhotoUrl,
                        contacts = userMessaging.contacts,
                        id = id
                    )
                )
            )
        ).await().id
    }
}