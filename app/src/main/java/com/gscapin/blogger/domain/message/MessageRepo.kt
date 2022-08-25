package com.gscapin.blogger.domain.message

import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.ContactMessage
import com.gscapin.blogger.data.model.Message
import javax.inject.Singleton

@Singleton
interface MessageRepo {
    suspend fun getContactMessages(): Result<List<ContactMessage>>
    suspend fun sendUserMessage(id: String): Result<String>
    suspend fun getContactMessagesFromCurrentUser(): Result<List<ContactMessage>>
    suspend fun sendMessage(text: String, idChat: String)
    suspend fun getLastestMessages(idChat: String): Result<List<Message>>
}