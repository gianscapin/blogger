package com.gscapin.blogger.data.remote.message

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.Chat
import com.gscapin.blogger.data.model.ContactMessage
import com.gscapin.blogger.data.model.Message
import com.gscapin.blogger.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
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

    suspend fun sendUserMessage(id: String): Result<String> {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var userToMessaging =
            FirebaseFirestore.getInstance().collection("users").document(id).get().await()


        var userDb =
            FirebaseFirestore.getInstance().collection("users").document(currentUser!!.uid).get()
                .await()


        userToMessaging.toObject(User::class.java).let { userMessaging ->
            userDb.toObject(User::class.java).let { userFirebase ->

                val existChat = isExistChat(userFirebase!!, userMessaging!!)
                if (existChat == null) {
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
                    return Result.Success(idChat)
                }else{
                    return Result.Success(existChat)
                }


            }
        }

    }


    private fun isExistChat(userFirebase: User, userMessaging: User): String? {
        if (userFirebase.contacts != null && userMessaging.contacts != null) {
            val contactsCurrentUser = userFirebase.contacts
            val contactOtherUser = userMessaging.contacts
            var chat: String

            for (contact in contactsCurrentUser!!) {
                val idChat = contact.idMessage
                for (contactOther in contactOtherUser!!) {
                    if (contactOther.idMessage == idChat) {
                        chat = idChat!!
                        return chat
                    }
                }
            }
            return null
        } else {
            return null
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

    suspend fun sendMessage(text:String, idChat: String){

        val chat = FirebaseFirestore.getInstance().collection("chat").document(idChat).get().await()
        val currentUser = FirebaseAuth.getInstance().currentUser

        chat.toObject(Chat::class.java).let { chatDb ->

            var list = chatDb!!.text?.toMutableList()

            list!!.add(Message(
                text = text,
                date = Timestamp.now().toDate(),
                idUser = currentUser!!.uid
            ))
            chatDb.apply {
                chatDb.text = list.toList()
            }

            FirebaseFirestore.getInstance().collection("chat").document(idChat)
                .set(chatDb, SetOptions.merge()).await()
        }

    }

    suspend fun getLastestMessages(idChat: String): Result<List<Message>>{
        val messagesList:List<Message>

        withContext(Dispatchers.IO){
            val chat = FirebaseFirestore.getInstance().collection("chat").document(idChat).get().await()

            chat.toObject(Chat::class.java).let { chatFromFirebase ->
                messagesList = chatFromFirebase?.text!!
            }
        }

        return Result.Success(messagesList)
    }
}