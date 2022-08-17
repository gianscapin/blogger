package com.gscapin.blogger.presentation.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.ContactMessage
import com.gscapin.blogger.domain.message.MessageRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val repository: MessageRepoImpl): ViewModel() {
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
}