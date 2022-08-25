package com.gscapin.blogger.presentation.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.Chat
import com.gscapin.blogger.data.model.ContactMessage
import com.gscapin.blogger.data.model.Message
import com.gscapin.blogger.domain.message.MessageRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val repository: MessageRepoImpl) : ViewModel() {

    private val messagesState = MutableStateFlow<Result<List<Message>>>(Result.Loading())
    //val messagesState: StateFlow<Result<List<Message>>> = _messagesState

    val seeContactMessages: StateFlow<Result<List<ContactMessage>>> = flow {
        emit(Result.Loading())
        kotlin.runCatching {
            repository.getContactMessages()
        }.onSuccess { contactMessages ->
            emit(contactMessages)
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Result.Loading()
    )

    fun sendUserMessage(userId: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading())

        try {
            emit(repository.sendUserMessage(userId))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getContactListFromCurrentUser() = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        kotlin.runCatching {
            repository.getContactMessagesFromCurrentUser()
        }.onSuccess { contactMessages ->
            emit(contactMessages)
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }

    fun sendMessage(text: String, idChat: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repository.sendMessage(text, idChat)))
            //latestMessages(idChat)
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun lastestMessages(idChat: String): StateFlow<Result<List<Message>>> = flow {
        kotlin.runCatching {
            repository.getLastestMessages(idChat)
        }.onSuccess { messages ->
            emit(messages)
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Result.Loading()
    )

    fun latestMessages(idChat: String) = viewModelScope.launch {
        kotlin.runCatching {
            //repository.getLastestMessages(idChat)
            FirebaseFirestore.getInstance().collection("chat").document(idChat)
                .addSnapshotListener { snapshot, e ->
                    onMessagesChange(snapshot)
                }
        }.onFailure {
            messagesState.value = Result.Failure(Exception(it.message))
        }
    }

    private fun onMessagesChange(snapshot: DocumentSnapshot?) {
        val listMessages: MutableList<Message> = mutableListOf()
        val chat = snapshot!!.toObject(Chat::class.java)
        for(message in chat?.text!!){
            listMessages.add(message)
        }
        messagesState.value = Result.Success(listMessages)
    }

    fun getMessages(): StateFlow<Result<List<Message>>> = messagesState
}