package com.gscapin.blogger.presentation.post

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.domain.post.PostRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private val repo: PostRepoImpl): ViewModel() {

    fun uploadPost(description: String, image: Bitmap) = liveData(Dispatchers.IO){
        emit(Result.Loading())

        try {
            emit(Result.Success(repo.uploadPost(description, image)))
        }catch (e: Exception){
            emit(Result.Failure(e))
        }
    }
}