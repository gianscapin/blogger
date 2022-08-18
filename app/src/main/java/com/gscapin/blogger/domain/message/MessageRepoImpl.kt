package com.gscapin.blogger.domain.message

import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.ContactMessage
import com.gscapin.blogger.data.remote.message.MessageDataSource
import javax.inject.Inject

class MessageRepoImpl @Inject constructor(private val dataSource: MessageDataSource): MessageRepo {
    override suspend fun getContactMessages(): Result<List<ContactMessage>> = dataSource.getContactMessages()
    override suspend fun sendUserMessage(id: String) = dataSource.sendUserMessage(id)
    override suspend fun getContactMessagesFromCurrentUser(): Result<List<ContactMessage>> = dataSource.getContactMessagesFromCurrentUser()
}