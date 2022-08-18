package com.gscapin.blogger.domain.message

import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.ContactMessage
import javax.inject.Singleton

@Singleton
interface MessageRepo {
    suspend fun getContactMessages(): Result<List<ContactMessage>>
    suspend fun sendUserMessage(id: String)
    suspend fun getContactMessagesFromCurrentUser(): Result<List<ContactMessage>>
}