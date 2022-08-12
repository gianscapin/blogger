package com.gscapin.blogger.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.Post
import com.gscapin.blogger.domain.home.HomeRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepoImpl): ViewModel() {

    fun getLastestPosts() = liveData(Dispatchers.IO) {
        emit(Result.Loading())

        kotlin.runCatching {
            repository.getLastestPosts()
        }.onSuccess { posts ->
            emit(posts)
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }

    fun likePost(postId: String, liked: Boolean) = liveData(Dispatchers.IO) {
        emit(Result.Loading())

        try {
            emit(Result.Success(repository.registerLike(postId, liked)))
        }catch (e: Exception){
            emit(Result.Failure(e))
        }
    }

    val seeNewPosts: StateFlow<Result<List<Post>>> = flow {
        emit(Result.Loading())
        kotlin.runCatching {
            repository.getLastestPosts()
        }.onSuccess { posts ->
            emit(posts)
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Result.Loading()
    )
}