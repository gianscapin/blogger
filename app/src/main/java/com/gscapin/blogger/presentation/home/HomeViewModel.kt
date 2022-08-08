package com.gscapin.blogger.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.domain.home.HomeRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
}